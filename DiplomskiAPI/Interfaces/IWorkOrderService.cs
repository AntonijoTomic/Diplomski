using DiplomskiAPI.DTOs;
using DiplomskiAPI.Models;

namespace DiplomskiAPI.Interfaces
{
    public interface IWorkOrderService
    {
        List<WorkOrder> GetAll();

        WorkOrder? GetById(int id);

        WorkOrder Create(WorkOrderCreateDto request);

        WorkOrder? UpdateStatus(int id, string status);

        bool Delete(int id);
    }
}