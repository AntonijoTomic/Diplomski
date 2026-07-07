using DiplomskiAPI.Data;
using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using DiplomskiAPI.Models;

namespace DiplomskiAPI.Services
{
    public class WorkOrderServiceItemService : IWorkOrderServiceItemService
    {
        private readonly ApplicationDbContext _context;

        public WorkOrderServiceItemService(ApplicationDbContext context)
        {
            _context = context;
        }

        public List<WorkOrderServiceItemDto> GetByWorkOrderId(int workOrderId)
        {
            return _context.WorkOrderServices
                .Where(w => w.WorkOrderId == workOrderId)
                .Join(
                    _context.Services,
                    item => item.ServiceId,
                    service => service.Id,
                    (item, service) => new WorkOrderServiceItemDto
                    {
                        Id = item.Id,
                        Hours = item.Hours,
                        HourlyRate = item.HourlyRate,
                        TotalPrice = item.TotalPrice,

                        Service = new ServiceDto
                        {
                            Id = service.Id,
                            Name = service.Name,
                            Description = service.Description,
                            Price = service.Price
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

            return workOrderService;
        }

        public bool Delete(int id)
        {
            var item = _context.WorkOrderServices.FirstOrDefault(wos => wos.Id == id);

            if (item == null)
            {
                return false;
            }

            _context.WorkOrderServices.Remove(item);
            _context.SaveChanges();

            return true;
        }
    }
}