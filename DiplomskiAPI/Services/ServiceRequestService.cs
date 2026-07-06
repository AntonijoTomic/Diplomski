using DiplomskiAPI.Data;
using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using DiplomskiAPI.Models;

namespace DiplomskiAPI.Services
{
    public class ServiceRequestService : IServiceRequestService
    {
        private readonly ApplicationDbContext _context;

        public ServiceRequestService(ApplicationDbContext context)
        {
            _context = context;
        }

        public List<ServiceRequestDto> GetAll()
        {
            return _context.ServiceRequests
     .Join(
         _context.Vehicles,
         sr => sr.VehicleId,
         v => v.Id,
         (sr, v) => new { sr, v })
     .Join(
         _context.Users,
         x => x.sr.UserId,
         u => u.Id,
         (x, u) => new ServiceRequestDto
         {
             Id = x.sr.Id,
             Vehicle = new VehicleDto
             {
                 Id = x.v.Id,
                 Brand = x.v.Brand,
                 Model = x.v.Model,
                 Year = x.v.Year,
                 LicensePlate = x.v.LicensePlate,
                 Vin = x.v.Vin,
                 FuelType = x.v.FuelType,
                 Mileage = x.v.Mileage,
                 RegistrationDate = x.v.RegistrationDate,
                 Note = x.v.Note
             },
             ProblemDescription = x.sr.ProblemDescription,
             ServiceType = x.sr.ServiceType,
             Urgency = x.sr.Urgency,
             Status = x.sr.Status,
             Note = x.sr.Note,
             CreatedAt = x.sr.CreatedAt,

             User = new UserDto
             {
                 Id = u.Id,
                 FirstName = u.FirstName,
                 LastName = u.LastName,
                 Email = u.Email,
                 PhoneNumber = u.Phone
             }
         })
         .OrderByDescending(x => x.CreatedAt)
        .ToList();
        }

        public List<ServiceRequestDto> GetByUserId(int userId)
        {
            return _context.ServiceRequests
                .Where(sr => sr.UserId == userId)
                .Join(
                    _context.Vehicles,
                    sr => sr.VehicleId,
                    v => v.Id,
                    (sr, v) => new ServiceRequestDto
                    {
                        Id = sr.Id,
                        Vehicle = new VehicleDto
                        {
                            Id = v.Id,
                            Brand = v.Brand,
                            Model = v.Model,
                            Year = v.Year,
                            LicensePlate =v.LicensePlate,
                            Vin = v.Vin,
                            FuelType = v.FuelType,
                            Mileage = v.Mileage,
                            RegistrationDate = v.RegistrationDate,
                            Note = v.Note
                        },
                        ProblemDescription = sr.ProblemDescription,
                        ServiceType = sr.ServiceType,
                        Urgency = sr.Urgency,
                        Status = sr.Status,
                        CreatedAt = sr.CreatedAt
                    })
                .OrderByDescending(x => x.CreatedAt)
                .ToList();
        }

        public ServiceRequestDto? GetById(int id)
        {
            return _context.ServiceRequests
                .Where(sr => sr.Id == id)
                .Join(
                    _context.Vehicles,
                    sr => sr.VehicleId,
                    v => v.Id,
                    (sr, v) => new { sr, v })
                .Join(
                    _context.Users,
                    x => x.sr.UserId,
                    u => u.Id,
                    (x, u) => new ServiceRequestDto
                    {
                        Id = x.sr.Id,

                        ProblemDescription = x.sr.ProblemDescription,
                        ServiceType = x.sr.ServiceType,
                        Urgency = x.sr.Urgency,
                        Status = x.sr.Status,
                        Note = x.sr.Note,
                        CreatedAt = x.sr.CreatedAt,
                        DesiredDate = x.sr.DesiredDate,

                        User = new UserDto
                        {
                            Id = u.Id,
                            FirstName = u.FirstName,
                            LastName = u.LastName,
                            Email = u.Email,
                            PhoneNumber = u.Phone
                        },
                        Vehicle = new VehicleDto
                        {
                            Id = x.v.Id,
                            Brand = x.v.Brand,
                            Model = x.v.Model,
                            Year = x.v.Year,
                            LicensePlate = x.v.LicensePlate,
                            Vin = x.v.Vin,
                            FuelType = x.v.FuelType,
                            Mileage = x.v.Mileage,
                            RegistrationDate = x.v.RegistrationDate,
                            Note = x.v.Note
                        }
                    })
                .FirstOrDefault();
        }

        public ServiceRequest Create(ServiceRequestCreateDto request)
        {
            var serviceRequest = new ServiceRequest
            {
                UserId = request.UserId,
                VehicleId = request.VehicleId,
                ProblemDescription = request.ProblemDescription,
                ServiceType = request.ServiceType,
                DesiredDate = request.DesiredDate,
                Urgency = request.Urgency,
                Note = request.Note,
                Status = "PENDING",
                CreatedAt = DateTime.UtcNow
            };

            _context.ServiceRequests.Add(serviceRequest);
            _context.SaveChanges();

            return serviceRequest;
        }

        public ServiceRequest? UpdateStatus(int id, string status)
        {
            var serviceRequest = _context.ServiceRequests.FirstOrDefault(sr => sr.Id == id);

            if (serviceRequest == null)
            {
                return null;
            }

            serviceRequest.Status = status;
            _context.SaveChanges();

            return serviceRequest;
        }

        public bool Delete(int id)
        {
            var serviceRequest = _context.ServiceRequests.FirstOrDefault(sr => sr.Id == id);

            if (serviceRequest == null)
            {
                return false;
            }

            _context.ServiceRequests.Remove(serviceRequest);
            _context.SaveChanges();

            return true;
        }
        public List<ServiceRequest> GetByVehicleId(int vehicleId)
        {
            return _context.ServiceRequests
                .Where(sr => sr.VehicleId == vehicleId)
                .ToList();
        }

        public ServiceRequest? Update(int id, ServiceRequestCreateDto request)
        {
            var serviceRequest = _context.ServiceRequests
         .FirstOrDefault(sr => sr.Id == id);

            if (serviceRequest == null)
            {
                return null;
            }

            serviceRequest.UserId = request.UserId;
            serviceRequest.VehicleId = request.VehicleId;
            serviceRequest.ProblemDescription = request.ProblemDescription;
            serviceRequest.ServiceType = request.ServiceType;
            serviceRequest.DesiredDate = request.DesiredDate;
            serviceRequest.Urgency = request.Urgency;
            serviceRequest.Note = request.Note;

            _context.SaveChanges();

            return serviceRequest;
        }
    
    }
}