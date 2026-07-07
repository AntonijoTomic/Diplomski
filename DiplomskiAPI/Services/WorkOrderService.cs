using DiplomskiAPI.Data;
using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using DiplomskiAPI.Models;

namespace DiplomskiAPI.Services
{
    public class WorkOrderService : IWorkOrderService
    {
        private readonly ApplicationDbContext _context;

        public WorkOrderService(ApplicationDbContext context)
        {
            _context = context;
        }

        public List<WorkOrder> GetAll()
        {
            return _context.WorkOrders.ToList();
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

        public WorkOrder? UpdateStatus(int id, string status)
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

            return workOrder;
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
           .Join(
               _context.ServiceRequests,
               w => w.ServiceRequestId,
               sr => sr.Id,
               (w, sr) => new { w, sr }
           )
           .Where(x => x.sr.VehicleId == vehicleId)
           .Select(x => x.w)
           .ToList();
        }
        

        public List<WorkOrder> GetByUserId(int userId)
        {
            return _context.WorkOrders
        .Join(
            _context.ServiceRequests,
            w => w.ServiceRequestId,
            sr => sr.Id,
            (w, sr) => new { w, sr }
        )
        .Where(x => x.sr.UserId == userId)
        .Select(x => x.w)
        .ToList();
        }

        public WorkOrderDto? GetDetailsById(int id)
        {
            return _context.WorkOrders
                .Where(w => w.Id == id)
                .Join(_context.ServiceRequests,
                    w => w.ServiceRequestId,
                    sr => sr.Id,
                    (w, sr) => new { w, sr })
                .Join(_context.Vehicles,
                    x => x.sr.VehicleId,
                    v => v.Id,
                    (x, v) => new { x.w, x.sr, v })
                .Join(_context.Users,
                    x => x.sr.UserId,
                    u => u.Id,
                    (x, u) => new WorkOrderDto
                    {
                        Id = x.w.Id,
                        OrderNumber = x.w.OrderNumber,
                        ServiceRequestId = x.w.ServiceRequestId,
                        AdminId = x.w.AdminId,
                        Diagnosis = x.w.Diagnosis,
                        Status = x.w.Status,
                        EstimatedCost = x.w.EstimatedCost,
                        FinalCost = x.w.FinalCost,
                        OpenedAt = x.w.OpenedAt,
                        ClosedAt = x.w.ClosedAt,
                        FinalReport = x.w.FinalReport,

                        ServiceRequest = new ServiceRequestDto
                        {
                            Id = x.sr.Id,
                            ProblemDescription = x.sr.ProblemDescription,
                            ServiceType = x.sr.ServiceType,
                            Urgency = x.sr.Urgency,
                            Status = x.sr.Status,
                            Note = x.sr.Note,
                            CreatedAt = x.sr.CreatedAt,
                            DesiredDate = x.sr.DesiredDate,

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

                            User = new UserDto
                            {
                                Id = u.Id,
                                FirstName = u.FirstName,
                                LastName = u.LastName,
                                Email = u.Email,
                                PhoneNumber = u.Phone
                            }
                        }
                    })
                .FirstOrDefault();
         }
        public WorkOrderDto? Update(int id, WorkOrderUpdateDto request)
        {
            var workOrder = _context.WorkOrders.FirstOrDefault(w => w.Id == id);

            if (workOrder == null)
                return null;

            workOrder.Diagnosis = request.Diagnosis;
            workOrder.FinalReport = request.FinalReport;

            _context.SaveChanges();

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
    }
}