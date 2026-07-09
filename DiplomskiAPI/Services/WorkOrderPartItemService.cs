using DiplomskiAPI.Data;
using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using DiplomskiAPI.Models;

namespace DiplomskiAPI.Services
{
    public class WorkOrderPartItemService : IWorkOrderPartItemService
    {
        private readonly ApplicationDbContext _context;

        public WorkOrderPartItemService(ApplicationDbContext context)
        {
            _context = context;
        }

        public List<WorkOrderPartItemDto> GetByWorkOrderId(int workOrderId)
        {
            return _context.WorkOrderParts
                .Where(w => w.WorkOrderId == workOrderId)
                .Join(
                    _context.Parts,
                    item => item.PartId,
                    part => part.Id,
                    (item, part) => new WorkOrderPartItemDto
                    {
                        Id = item.Id,
                        Quantity = item.Quantity,
                        UnitPrice = item.UnitPrice,
                        TotalPrice = item.TotalPrice,
                        Part = new PartDto
                        {
                            Id = part.Id,
                            Name = part.Name,
                            Manufacturer = part.Manufacturer,
                            Price = part.Price,
                            StockQuantity = part.StockQuantity,
                            MinimumStock = part.MinimumStock
                        }
                    })
                .ToList();
        }

        public WorkOrderPartItem? AddPartToWorkOrder(WorkOrderPartCreateDto request)
        {
            var part = _context.Parts.FirstOrDefault(p => p.Id == request.PartId);

            if (part == null || request.Quantity <= 0)
            {
                return null;
            }

            var totalPrice = request.Quantity * part.Price;

            var workOrderPart = new WorkOrderPartItem
            {
                WorkOrderId = request.WorkOrderId,
                PartId = request.PartId,
                Quantity = request.Quantity,
                UnitPrice = part.Price,
                TotalPrice = totalPrice
            };

            _context.WorkOrderParts.Add(workOrderPart);
            _context.SaveChanges();

            UpdateWorkOrderEstimatedCost(request.WorkOrderId);

            return workOrderPart;
        }

        public bool Delete(int id)
        {
            var item = _context.WorkOrderParts.FirstOrDefault(wop => wop.Id == id);

            if (item == null)
            {
                return false;
            }

            var workOrderId = item.WorkOrderId;

            _context.WorkOrderParts.Remove(item);
            _context.SaveChanges();

            UpdateWorkOrderEstimatedCost(workOrderId);

            return true;
        }

        private void UpdateWorkOrderEstimatedCost(int workOrderId)
        {
            var workOrder = _context.WorkOrders.FirstOrDefault(w => w.Id == workOrderId);

            if (workOrder == null)
            {
                return;
            }

            var servicesTotal = _context.WorkOrderServices
                .Where(x => x.WorkOrderId == workOrderId)
                .Sum(x => x.TotalPrice);

            var partsTotal = _context.WorkOrderParts
                .Where(x => x.WorkOrderId == workOrderId)
                .Sum(x => x.TotalPrice);

            var total = servicesTotal + partsTotal;

            workOrder.EstimatedCost = total;
            workOrder.FinalCost = total;

            _context.SaveChanges();
        }
    }
}