using DiplomskiAPI.DTOs;
using DiplomskiAPI.Models;

namespace DiplomskiAPI.Interfaces
{
    public interface IWorkOrderService
    {
        List<WorkOrderDto> GetAll();

        WorkOrder? GetById(int id);

        WorkOrderDto Create(WorkOrderCreateDto request);

        WorkOrderDto? UpdateStatus(int id, string status);

        bool Delete(int id);

        List<WorkOrder> GetByVehicleId(int vehicleId);
        List<WorkOrder> GetByUserId(int userId);
        WorkOrderDto? GetDetailsById(int id);
        WorkOrderDto? Update(int id, WorkOrderUpdateDto request);
    }
}