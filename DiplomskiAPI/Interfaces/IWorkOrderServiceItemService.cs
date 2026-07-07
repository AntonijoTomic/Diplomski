using DiplomskiAPI.DTOs;
using DiplomskiAPI.Models;

namespace DiplomskiAPI.Interfaces
{
    public interface IWorkOrderServiceItemService
    {
        List<WorkOrderServiceItemDto> GetByWorkOrderId(int workOrderId);

        WorkOrderServiceItem? AddServiceToWorkOrder(WorkOrderServiceCreateDto request);

        bool Delete(int id);
    }
}