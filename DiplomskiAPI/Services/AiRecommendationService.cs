using DiplomskiAPI.Data;
using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using Microsoft.EntityFrameworkCore;
using DiplomskiAPI.Configuration;
using Microsoft.Extensions.Options;
using System.Net.Http.Headers;
using System.Text;
using System.Text.Json;
using DiplomskiAPI.Models;

namespace DiplomskiAPI.Services
{
    public class AiRecommendationService : IAiRecommendationService
    {
        private readonly ApplicationDbContext _context;
        private readonly HttpClient _httpClient;
        private readonly OpenAiOptions _options;

        public AiRecommendationService(ApplicationDbContext context,IHttpClientFactory httpClientFactory,IOptions<OpenAiOptions> options)
        {
            _context = context;
            _httpClient = httpClientFactory.CreateClient();
            _options = options.Value;
        }

        public async Task<AiRecommendationResponseDto?> RecommendAsync(int workOrderId)
        {
            var response = new AiRecommendationResponseDto();
            var workOrder = await _context.WorkOrders
                .Include(w => w.ServiceRequest)
                    .ThenInclude(sr => sr.Vehicle)
                .FirstOrDefaultAsync(w => w.Id == workOrderId);

            if (workOrder == null)
            {
                return null;
            }

            var existingPartIds = _context.WorkOrderParts
               .Where(x => x.WorkOrderId == workOrderId)
               .Select(x => x.PartId)
               .ToList();

            var availableParts = await _context.Parts
                .Where(p => !existingPartIds.Contains(p.Id))
                .OrderBy(p => p.Id)
                .ToListAsync();


            var existingServiceIds = _context.WorkOrderServices
                .Where(x => x.WorkOrderId == workOrderId)
                .Select(x => x.ServiceId)
                .ToList();

            var availableServices = await _context.Services
                .Where(s => !existingServiceIds.Contains(s.Id))
                .OrderBy(s => s.Id)
                .ToListAsync();


            var prompt = BuildPrompt(workOrder, availableParts,availableServices);

            var aiResponse = await AskOpenAiAsync(prompt);

        
            System.Diagnostics.Debug.WriteLine(aiResponse);


            var result = ParseResponse(aiResponse);

            response.Parts = result.Parts
                .Where(p => availableParts.Any(ap => ap.Id == p.Id))
                .ToList();

            foreach (var part in response.Parts)
            {
                part.Name = availableParts
                    .First(p => p.Id == part.Id)
                    .Name;
            }

            response.Services = result.Services
            .Where(s => availableServices.Any(a => a.Id == s.Id))
            .ToList();

            foreach (var service in response.Services)
            {
                var serviceFromDatabase = availableServices
                  .First(s => s.Id == service.Id);

                        service.Name = serviceFromDatabase.Name;
                        service.HourlyRate = (double)serviceFromDatabase.Price;
                    }

            return response;
        }

        private string BuildPrompt(WorkOrder workOrder, List<Part> availableParts, List<Service> availableServices)
        {
            var prompt = new StringBuilder();
            prompt.AppendLine(
                "Ti si iskusni automehaničar."
             );

            prompt.AppendLine(
                "Na temelju podataka o vozilu i dijagnoze odaberi autodijelove koje treba naručiti i usluge koje treba obaviti."
            );

            prompt.AppendLine(
                "Koristi ISKLJUČIVO dijelove i usluge iz ponuđenog popisa."
            );

            prompt.AppendLine(
                "Nemoj izmišljati nove dijelove i usluge."
            );
            prompt.AppendLine(
                "Ako opis problema ili dijagnoza upućuju na zamjenu dijela, obavezno odaberi odgovarajući dio iz ponuđenog popisa."
            );

            prompt.AppendLine(
                "Nemoj vratiti praznu listu dijelova samo zato što dijagnoza ne navodi točan naziv dijela; zaključi koji su dijelovi uobičajeno potrebni za preporučeni zahvat."
            );

            prompt.AppendLine(
                "Za svaku odabranu uslugu provjeri zahtijeva li ona zamjenu ili ugradnju nekog od dostupnih dijelova."
            );
            prompt.AppendLine(
                 "Kod tekućina (motorno ulje, antifriz, ulje mjenjača, kočiona tekućina i slično) količina predstavlja broj pakiranja, a ne broj litara."
             );

            prompt.AppendLine(
                "Ako je dio pakiranje od 5 L i vozilu treba oko 5 L ulja, predloži količinu 1, a ne 5."
            );

            prompt.AppendLine(
                "Vrati ISKLJUČIVO JSON bez dodatnog teksta."
            );

            prompt.AppendLine();

            prompt.AppendLine("Podaci vozila:");

            prompt.AppendLine($"Marka: {workOrder.ServiceRequest.Vehicle.Brand}");
            prompt.AppendLine($"Model: {workOrder.ServiceRequest.Vehicle.Model}");
            prompt.AppendLine($"Godina: {workOrder.ServiceRequest.Vehicle.Year}");
            prompt.AppendLine($"Kilometraža: {workOrder.ServiceRequest.Vehicle.Mileage}");

            prompt.AppendLine();

            prompt.AppendLine("Opis problema:");
            prompt.AppendLine(workOrder.ServiceRequest.ProblemDescription);

            prompt.AppendLine();

            prompt.AppendLine("Dijagnoza:");
            prompt.AppendLine(
                string.IsNullOrWhiteSpace(workOrder.Diagnosis)
                    ? "Nije unesena."
                    : workOrder.Diagnosis
            );

            prompt.AppendLine();

            prompt.AppendLine("Dostupni dijelovi:");

            foreach (var part in availableParts)
            {
                prompt.AppendLine($"{part.Id} - {part.Name}");
            }

            prompt.AppendLine();

            prompt.AppendLine("Dostupne usluge:");

            foreach (var service in availableServices)
            {
                prompt.AppendLine($"{service.Id} - {service.Name}");
            }

            prompt.AppendLine();

            prompt.AppendLine("""
                    Vrati odgovor u ovom obliku:

                    {
                      "parts": [
                        {
                          "id": 1,
                          "quantity": 1
                        }
                      ],
                      "Services": [
                        {
                           "id": 1,
                          "hours": 1
                        }
                      ]
                    }

                       **Odaberi sve dijelove koji su realno potrebni za popravak.
                     Odaberi sve usluge koje su realno potrebne za popravak.
                     Koristi isključivo ID-eve iz ponuđenih popisa.
                     Quantity mora biti cijeli broj veći od 0.
                     Hours mora biti broj veći od 0.
                     Praznu listu dijelova vrati samo ako za popravak zaista nije potreban nijedan dostupni dio.
                     Praznu listu usluga vrati samo ako zaista nije potrebna nijedna dostupna usluga.
                     Nemoj dodavati nikakav tekst izvan JSON-a.
                    """);

                    

            return prompt.ToString();
        }

        private async Task<string> AskOpenAiAsync(string prompt)
        {
           
            var requestBody = new
            {
                model = _options.Model,
                input = prompt
            };

            var json = JsonSerializer.Serialize(requestBody);

            using var request = new HttpRequestMessage(
                HttpMethod.Post,
                "https://api.openai.com/v1/responses"
            );

            request.Headers.Authorization =
                new AuthenticationHeaderValue(
                    "Bearer",
                    _options.ApiKey
                );

            request.Content = new StringContent(
                json,
                Encoding.UTF8,
                "application/json"
            );

            using var response = await _httpClient.SendAsync(request);

            var responseContent =
                await response.Content.ReadAsStringAsync();

            if (!response.IsSuccessStatusCode)
            {
                throw new Exception(
                    $"OpenAI greška ({(int)response.StatusCode}): " +
                    responseContent
                );
            }

            using var document =
                JsonDocument.Parse(responseContent);

            var root = document.RootElement;

            if (!root.TryGetProperty("output", out var output))
            {
                throw new Exception(
                    "OpenAI odgovor ne sadrži output."
                );
            }

            foreach (var outputItem in output.EnumerateArray())
            {
                if (!outputItem.TryGetProperty("content",out var content))
                {
                    continue;
                }

                foreach (var contentItem in content.EnumerateArray())
                {
                    if (contentItem.TryGetProperty(
                            "type",
                            out var type)
                        && type.GetString() == "output_text"
                        && contentItem.TryGetProperty(
                            "text",
                            out var text))
                    {
                        return text.GetString() ?? string.Empty;
                    }
                }
            }

            throw new Exception(
                "OpenAI odgovor ne sadrži tekst."
            );
        }


        private AiRecommendationResponseDto ParseResponse(string aiResponse)
        {
            aiResponse = aiResponse.Trim();

            if (aiResponse.StartsWith("```"))
            {
                aiResponse = aiResponse
                    .Replace("```json", "")
                    .Replace("```", "")
                    .Trim();
            }

            var response = JsonSerializer.Deserialize<AiRecommendationResponseDto>(
                aiResponse,
                new JsonSerializerOptions
                {
                    PropertyNameCaseInsensitive = true
                });

            return response ?? new AiRecommendationResponseDto();
        }
    }
}