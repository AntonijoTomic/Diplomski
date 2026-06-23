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

        public WorkOrder Create(WorkOrderCreateDto request)
        {
            string orderNumber =
                $"WO-{DateTime.Now:yyyyMMdd}-{(_context.WorkOrders.Count() + 1):D3}";

            var workOrder = new WorkOrder
            {
                OrderNumber = orderNumber,
                ServiceRequestId = request.ServiceRequestId,
                UserId = request.UserId,
                VehicleId = request.VehicleId,
                AdminId = request.AdminId,
                ProblemDescription = request.ProblemDescription,
                Diagnosis = request.Diagnosis,
                Status = "RECEIVED",
                EstimatedCost = request.EstimatedCost,
                FinalCost = 0,
                OpenedAt = DateTime.UtcNow
            };

            _context.WorkOrders.Add(workOrder);
            _context.SaveChanges();

            return workOrder;
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
                .Where(w => w.VehicleId == vehicleId)
                .ToList();
        }

        public List<WorkOrder> GetByUserId(int userId)
        {
            return _context.WorkOrders
                .Where(w => w.UserId == userId)
                .ToList();
        }
    }
}