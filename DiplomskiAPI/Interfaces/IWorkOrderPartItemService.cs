using DiplomskiAPI.DTOs;
using DiplomskiAPI.Models;

namespace DiplomskiAPI.Interfaces
{
    public interface IWorkOrderPartItemService
    {
        List<WorkOrderPartItemDto> GetByWorkOrderId(int workOrderId);
        WorkOrderPartItem? AddPartToWorkOrder(WorkOrderPartCreateDto request);
        bool Delete(int id);
        void UpdateWorkOrderEstimatedCost(int workOrderId);
    }
}