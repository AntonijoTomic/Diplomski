using DiplomskiAPI.Data;
using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using DiplomskiAPI.Models;
using Microsoft.EntityFrameworkCore;

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
                .Include(sr => sr.Vehicle)
                .Include(sr => sr.User)
                .Select(sr => new ServiceRequestDto
                {
                    Id = sr.Id,
                    ProblemDescription = sr.ProblemDescription,
                    ServiceType = sr.ServiceType,
                    Urgency = sr.Urgency,
                    Status = sr.Status,
                    Note = sr.Note,
                    CreatedAt = sr.CreatedAt,
                    DesiredDate = sr.DesiredDate,

                    Vehicle = new VehicleDto
                    {
                        Id = sr.Vehicle.Id,
                        Brand = sr.Vehicle.Brand,
                        Model = sr.Vehicle.Model,
                        Year = sr.Vehicle.Year,
                        LicensePlate = sr.Vehicle.LicensePlate,
                        Vin = sr.Vehicle.Vin,
                        FuelType = sr.Vehicle.FuelType,
                        Mileage = sr.Vehicle.Mileage,
                        RegistrationDate = sr.Vehicle.RegistrationDate,
                        Note = sr.Vehicle.Note
                    },

                    User = new UserDto
                    {
                        Id = sr.User.Id,
                        FirstName = sr.User.FirstName,
                        LastName = sr.User.LastName,
                        Email = sr.User.Email,
                        PhoneNumber = sr.User.Phone
                    }
                })
                .OrderByDescending(x => x.CreatedAt)
                .ToList();
        }

        public List<ServiceRequestDto> GetByUserId(int userId)
        {
            return _context.ServiceRequests
                .Include(sr => sr.Vehicle)
                .Where(sr => sr.UserId == userId)
                .Select(sr => new ServiceRequestDto
                {
                    Id = sr.Id,
                    ProblemDescription = sr.ProblemDescription,
                    ServiceType = sr.ServiceType,
                    Urgency = sr.Urgency,
                    Status = sr.Status,
                    CreatedAt = sr.CreatedAt,

                    Vehicle = new VehicleDto
                    {
                        Id = sr.Vehicle.Id,
                        Brand = sr.Vehicle.Brand,
                        Model = sr.Vehicle.Model,
                        Year = sr.Vehicle.Year,
                        LicensePlate = sr.Vehicle.LicensePlate,
                        Vin = sr.Vehicle.Vin,
                        FuelType = sr.Vehicle.FuelType,
                        Mileage = sr.Vehicle.Mileage,
                        RegistrationDate = sr.Vehicle.RegistrationDate,
                        Note = sr.Vehicle.Note
                    }
                })
                .OrderByDescending(x => x.CreatedAt)
                .ToList();
        }
        public ServiceRequestDto? GetById(int id)
        {
            return _context.ServiceRequests
                .Include(sr => sr.Vehicle)
                .Include(sr => sr.User)
                .Where(sr => sr.Id == id)
                .Select(sr => new ServiceRequestDto
                {
                    Id = sr.Id,
                    ProblemDescription = sr.ProblemDescription,
                    ServiceType = sr.ServiceType,
                    Urgency = sr.Urgency,
                    Status = sr.Status,
                    Note = sr.Note,
                    CreatedAt = sr.CreatedAt,
                    DesiredDate = sr.DesiredDate,

                    Vehicle = new VehicleDto
                    {
                        Id = sr.Vehicle.Id,
                        Brand = sr.Vehicle.Brand,
                        Model = sr.Vehicle.Model,
                        Year = sr.Vehicle.Year,
                        LicensePlate = sr.Vehicle.LicensePlate,
                        Vin = sr.Vehicle.Vin,
                        FuelType = sr.Vehicle.FuelType,
                        Mileage = sr.Vehicle.Mileage,
                        RegistrationDate = sr.Vehicle.RegistrationDate,
                        Note = sr.Vehicle.Note
                    },

                    User = new UserDto
                    {
                        Id = sr.User.Id,
                        FirstName = sr.User.FirstName,
                        LastName = sr.User.LastName,
                        Email = sr.User.Email,
                        PhoneNumber = sr.User.Phone
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