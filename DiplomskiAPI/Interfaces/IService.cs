using DiplomskiAPI.DTOs;
using DiplomskiAPI.Models;

namespace DiplomskiAPI.Interfaces
{
    public interface IServiceService
    {
        List<Service> GetAll();
        Service? GetById(int id);
        Service Create(ServiceCreateDto request);
        Service? Update(int id, ServiceCreateDto request);
        bool Delete(int id);
    }
}