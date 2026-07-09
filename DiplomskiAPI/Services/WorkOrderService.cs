using DiplomskiAPI.Data;
using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using DiplomskiAPI.Models;
using Microsoft.EntityFrameworkCore;

namespace DiplomskiAPI.Services
{
    public class WorkOrderService : IWorkOrderService
    {
        private readonly ApplicationDbContext _context;

        public WorkOrderService(ApplicationDbContext context)
        {
            _context = context;
        }

        public List<WorkOrderDto> GetAll()
        {
            return _context.WorkOrders
                .Include(w => w.ServiceRequest)
                    .ThenInclude(sr => sr.Vehicle)
                .Include(w => w.ServiceRequest)
                    .ThenInclude(sr => sr.User)
                .Select(w => MapToDetailedDto(w))
                .ToList();
        }

        public WorkOrder? GetById(int id)
        {
            return _context.WorkOrders.FirstOrDefault(w => w.Id == id);
        }

        public WorkOrderDto Create(WorkOrderCreateDto request)
        {
            var serviceRequest = _context.ServiceRequests
                .FirstOrDefault(sr => sr.Id == request.ServiceRequestId);

            if (serviceRequest == null)
            {
                throw new Exception("Servisni zahtjev nije pronađen.");
            }

            var existingWorkOrder = _context.WorkOrders
                .FirstOrDefault(w => w.ServiceRequestId == request.ServiceRequestId);

            if (existingWorkOrder != null) // ako postoji vraca njega
            {
                return MapToDto(existingWorkOrder);
            }

            int nextNumber = _context.WorkOrders.Count() + 1;

            var workOrder = new WorkOrder
            {
                ServiceRequestId = request.ServiceRequestId,
                AdminId = request.AdminId,
                OrderNumber = $"RN-{DateTime.UtcNow.Year}-{nextNumber:D4}",
                Diagnosis = null,
                Status = "OPEN",
                EstimatedCost = 0,
                FinalCost = 0,
                OpenedAt = DateTime.UtcNow,
                ClosedAt = null,
                FinalReport = null
            };

            _context.WorkOrders.Add(workOrder);
            _context.SaveChanges();

            return MapToDto(workOrder);
        }

        public WorkOrderDto? UpdateStatus(int id, string status)
        {
            var workOrder = _context.WorkOrders.FirstOrDefault(w => w.Id == id);

            if (workOrder == null)
            {
                return null;
            }

            workOrder.Status = status;

            if (status == "COMPLETED")
            {
                workOrder.ClosedAt = DateTime.UtcNow;
            }

            _context.SaveChanges();

            return MapToDto(workOrder);
        }

        public bool Delete(int id)
        {
            var workOrder = _context.WorkOrders.FirstOrDefault(w => w.Id == id);

            if (workOrder == null)
            {
                return false;
            }

            _context.WorkOrders.Remove(workOrder);
            _context.SaveChanges();

            return true;
        }

        public List<WorkOrder> GetByVehicleId(int vehicleId)
        {
            return _context.WorkOrders
                .Include(w => w.ServiceRequest)
                .Where(w => w.ServiceRequest.VehicleId == vehicleId)
                .ToList();
        }

        public List<WorkOrder> GetByUserId(int userId)
        {
            return _context.WorkOrders
                .Include(w => w.ServiceRequest)
                .Where(w => w.ServiceRequest.UserId == userId)
                .ToList();
        }

        public WorkOrderDto? GetDetailsById(int id)
        {
            var workOrder = _context.WorkOrders
                .Include(w => w.ServiceRequest)
                    .ThenInclude(sr => sr.Vehicle)
                .Include(w => w.ServiceRequest)
                    .ThenInclude(sr => sr.User)
                .FirstOrDefault(w => w.Id == id);

            return workOrder == null ? null : MapToDetailedDto(workOrder);
        }

        public WorkOrderDto? Update(int id, WorkOrderUpdateDto request)
        {
            var workOrder = _context.WorkOrders.FirstOrDefault(w => w.Id == id);

            if (workOrder == null)
                return null;

            workOrder.Diagnosis = request.Diagnosis;
            workOrder.FinalReport = request.FinalReport;

            _context.SaveChanges();

            return MapToDto(workOrder);
        }

        private WorkOrderDto MapToDto(WorkOrder workOrder)
        {
            return new WorkOrderDto
            {
                Id = workOrder.Id,
                OrderNumber = workOrder.OrderNumber,
                ServiceRequestId = workOrder.ServiceRequestId,
                AdminId = workOrder.AdminId,
                Diagnosis = workOrder.Diagnosis,
                Status = workOrder.Status,
                EstimatedCost = workOrder.EstimatedCost,
                FinalCost = workOrder.FinalCost,
                OpenedAt = workOrder.OpenedAt,
                ClosedAt = workOrder.ClosedAt,
                FinalReport = workOrder.FinalReport
            };
        }

        private WorkOrderDto MapToDetailedDto(WorkOrder w)
        {
            var sr = w.ServiceRequest;
            var v = sr.Vehicle;
            var u = sr.User;

            return new WorkOrderDto
            {
                Id = w.Id,
                OrderNumber = w.OrderNumber,
                ServiceRequestId = w.ServiceRequestId,
                AdminId = w.AdminId,
                Diagnosis = w.Diagnosis,
                Status = w.Status,
                EstimatedCost = w.EstimatedCost,
                FinalCost = w.FinalCost,
                OpenedAt = w.OpenedAt,
                ClosedAt = w.ClosedAt,
                FinalReport = w.FinalReport,

                ServiceRequest = new ServiceRequestDto
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
                        Id = v.Id,
                        Brand = v.Brand,
                        Model = v.Model,
                        Year = v.Year,
                        LicensePlate = v.LicensePlate,
                        Vin = v.Vin,
                        FuelType = v.FuelType,
                        Mileage = v.Mileage,
                        RegistrationDate = v.RegistrationDate,
                        Note = v.Note
                    },

                    User = new UserDto
                    {
                        Id = u.Id,
                        FirstName = u.FirstName,
                        LastName = u.LastName,
                        Email = u.Email,
                        PhoneNumber = u.Phone
                    }
                }
            };
        }
    }
}