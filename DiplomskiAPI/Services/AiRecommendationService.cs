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

        public async Task<AiPartRecommendationResponseDto?> RecommendPartsAsync(
                      int workOrderId)
        {
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

            var prompt = BuildPrompt(workOrder, availableParts);

            var aiResponse = await AskOpenAiAsync(prompt);

        
            System.Diagnostics.Debug.WriteLine(aiResponse);

            
            var result = ParseResponse(aiResponse);

            result.Parts = result.Parts
                .Where(p => availableParts.Any(ap => ap.Id == p.Id))
                .ToList();

            return result;
        }

        private string BuildPrompt(WorkOrder workOrder, List<Part> availableParts)
        {
            var prompt = new StringBuilder();
            prompt.AppendLine(
                         "Ti si iskusni automehaničar."
             );

            prompt.AppendLine(
                "Na temelju podataka o vozilu i dijagnoze odaberi autodijelove koje treba naručiti."
            );

            prompt.AppendLine(
                "Koristi ISKLJUČIVO dijelove iz ponuđenog popisa."
            );

            prompt.AppendLine(
                "Nemoj izmišljati nove dijelove."
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

            prompt.AppendLine("""
                    Vrati odgovor u ovom obliku:

                    {
                      "parts": [
                        {
                          "id": 1,
                          "name": "Naziv dijela"
                        }
                      ]
                    }
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


        private AiPartRecommendationResponseDto ParseResponse(string aiResponse)
        {
            aiResponse = aiResponse.Trim();

            if (aiResponse.StartsWith("```"))
            {
                aiResponse = aiResponse
                    .Replace("```json", "")
                    .Replace("```", "")
                    .Trim();
            }

            var response = JsonSerializer.Deserialize<AiPartRecommendationResponseDto>(
                aiResponse,
                new JsonSerializerOptions
                {
                    PropertyNameCaseInsensitive = true
                });

            return response ?? new AiPartRecommendationResponseDto();
        }
    }
}