using DiplomskiAPI.Data;
using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using DiplomskiAPI.Models;
using Microsoft.EntityFrameworkCore;

namespace DiplomskiAPI.Services
{
    public class WorkOrderServiceItemService : IWorkOrderServiceItemService
    {
        private readonly ApplicationDbContext _context;
        private readonly IWorkOrderPartItemService _WorkOrderServiceItemService;

        public WorkOrderServiceItemService(ApplicationDbContext context, IWorkOrderPartItemService workOrderServiceItemService)
        {
            _context = context;
            _WorkOrderServiceItemService = workOrderServiceItemService;
        }

        public List<WorkOrderServiceItemDto> GetByWorkOrderId(int workOrderId)
        {
            return _context.WorkOrderServices
             .Include(x => x.Service)
             .Where(x => x.WorkOrderId == workOrderId)
             .Select(x => new WorkOrderServiceItemDto
             {
                 Id = x.Id,
                 Hours = x.Hours,
                 HourlyRate = x.HourlyRate,
                 TotalPrice = x.TotalPrice,

                 Service = new ServiceDto
                 {
                     Id = x.Service.Id,
                     Name = x.Service.Name,
                     Description = x.Service.Description,
                     Price = x.Service.Price
                 }
             })
             .ToList();
        }

        public WorkOrderServiceItem? AddServiceToWorkOrder(WorkOrderServiceCreateDto request)
        {
            var service = _context.Services.FirstOrDefault(s => s.Id == request.ServiceId);

            if (service == null)
            {
                return null;
            }

            var totalPrice = request.Hours * service.Price;

            var workOrderService = new WorkOrderServiceItem
            {
                WorkOrderId = request.WorkOrderId,
                ServiceId = request.ServiceId,
                Hours = request.Hours,
                HourlyRate = service.Price,
                TotalPrice = totalPrice
            };

            _context.WorkOrderServices.Add(workOrderService);
            _context.SaveChanges();
            _WorkOrderServiceItemService.UpdateWorkOrderEstimatedCost(request.WorkOrderId);

            return workOrderService;
        }

        public bool Delete(int id)
        {
            var item = _context.WorkOrderServices.FirstOrDefault(wos => wos.Id == id);

            if (item == null)
            {
                return false;
            }
            var workOrderId = item.WorkOrderId;
            _context.WorkOrderServices.Remove(item);
            _context.SaveChanges();
            _WorkOrderServiceItemService.UpdateWorkOrderEstimatedCost(workOrderId);
            return true;
        }
       
    }
}