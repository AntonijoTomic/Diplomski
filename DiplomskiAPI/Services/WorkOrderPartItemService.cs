using DiplomskiAPI.Data;
using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using DiplomskiAPI.Models;
using Microsoft.EntityFrameworkCore;

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
                .Include(x => x.Part)
                .Where(x => x.WorkOrderId == workOrderId)
                .Select(x => new WorkOrderPartItemDto
                {
                    Id = x.Id,
                    Quantity = x.Quantity,
                    UnitPrice = x.UnitPrice,
                    TotalPrice = x.TotalPrice,

                    Part = new PartDto
                    {
                        Id = x.Part.Id,
                        Name = x.Part.Name,
                        Manufacturer = x.Part.Manufacturer,
                        Price = x.Part.Price,
                        StockQuantity = x.Part.StockQuantity,
                        MinimumStock = x.Part.MinimumStock
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

            if (part == null || request.Quantity <= 0)
            {
                return null;
            }

            if (part.StockQuantity < request.Quantity)
            {
                throw new InvalidOperationException(
                    $"Nema dovoljno zalihe za dio '{part.Name}'. " +
                    $"Dostupno: {part.StockQuantity}"
                );
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
            part.StockQuantity -= request.Quantity;

            _context.WorkOrderParts.Add(workOrderPart);
            _context.SaveChanges();

            UpdateWorkOrderEstimatedCost(request.WorkOrderId);

            return workOrderPart;
        }

        public bool Delete(int id)
        {
            var item = _context.WorkOrderParts
            .Include(x => x.Part)
            .FirstOrDefault(x => x.Id == id);

            if (item == null)
            {
                return false;
            }

            var workOrderId = item.WorkOrderId;
            item.Part.StockQuantity += item.Quantity;
            _context.WorkOrderParts.Remove(item);
            _context.SaveChanges();

            UpdateWorkOrderEstimatedCost(workOrderId);

            return true;
        }

        public void UpdateWorkOrderEstimatedCost(int workOrderId)
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