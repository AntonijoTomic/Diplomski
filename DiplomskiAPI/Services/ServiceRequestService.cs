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

        public List<ServiceRequest> GetAll()
        {
            return _context.ServiceRequests.ToList();
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
                        VehicleId = sr.VehicleId,

                        VehicleName = v.Brand + " " + v.Model,
                        LicensePlate = v.LicensePlate,

                        ProblemDescription = sr.ProblemDescription,
                        ServiceType = sr.ServiceType,
                        Urgency = sr.Urgency,
                        Status = sr.Status,
                        CreatedAt = sr.CreatedAt
                    })
                .OrderByDescending(x => x.CreatedAt)
                .ToList();
        }

        public ServiceRequest? GetById(int id)
        {
            return _context.ServiceRequests.FirstOrDefault(sr => sr.Id == id);
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
    }
}