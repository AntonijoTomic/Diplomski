using DiplomskiAPI.DTOs;
using DiplomskiAPI.Models;

namespace DiplomskiAPI.Interfaces
{
    public interface IServiceRequestService
    {
        List<ServiceRequest> GetAll();
        List<ServiceRequestDto> GetByUserId(int userId);
        ServiceRequestDto? GetById(int id);
        ServiceRequest Create(ServiceRequestCreateDto request);
        ServiceRequest? UpdateStatus(int id, string status);
        bool Delete(int id);
        List<ServiceRequest> GetByVehicleId(int vehicleId);
        ServiceRequest? Update(int id, ServiceRequestCreateDto request);

    }
}
