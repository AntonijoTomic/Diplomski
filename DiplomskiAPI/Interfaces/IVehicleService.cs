using DiplomskiAPI.DTOs;
using DiplomskiAPI.Models;

namespace DiplomskiAPI.Interfaces
{
    public interface IVehicleService
    {
        List<Vehicle> GetAll();
        List<Vehicle> GetByUserId(int userId);
        Vehicle? GetById(int id);
        Vehicle Create(VehicleCreateDto request);
        Vehicle? Update(int id, VehicleCreateDto request);
        bool Delete(int id);
    }
}