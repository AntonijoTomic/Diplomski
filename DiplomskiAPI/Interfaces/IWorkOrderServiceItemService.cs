using DiplomskiAPI.DTOs;
using DiplomskiAPI.Models;

namespace DiplomskiAPI.Interfaces
{
    public interface IWorkOrderServiceItemService
    {
        List<WorkOrderServiceItem> GetByWorkOrderId(int workOrderId);

        WorkOrderServiceItem? AddServiceToWorkOrder(WorkOrderServiceCreateDto request);

        bool Delete(int id);
    }
}