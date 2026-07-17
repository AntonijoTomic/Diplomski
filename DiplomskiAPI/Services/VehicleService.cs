using DiplomskiAPI.Data;
using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using DiplomskiAPI.Models;

namespace DiplomskiAPI.Services
{
    public class VehicleService : IVehicleService
    {
        private readonly ApplicationDbContext _context;

        public VehicleService(ApplicationDbContext context)
        {
            _context = context;
        }

        public List<Vehicle> GetAll()
        {
            return _context.Vehicles
                .Where(v => v.IsActive)
                .OrderByDescending(v => v.CreatedAt)
                .ToList();
        }

        public List<Vehicle> GetByUserId(int userId)
        {
            return _context.Vehicles
                .Where(v => v.UserId == userId && v.IsActive)
                .OrderByDescending(v => v.CreatedAt)
                .ToList();
        }

        public Vehicle? GetById(int id)
        {
            return _context.Vehicles
                .FirstOrDefault(v => v.Id == id && v.IsActive);
        }

        public Vehicle Create(VehicleCreateDto request)
        {
            var vehicle = new Vehicle
            {
                UserId = request.UserId,
                Brand = request.Brand,
                Model = request.Model,
                Year = request.Year,
                LicensePlate = request.LicensePlate,
                Vin = request.Vin,
                FuelType = request.FuelType,
                Mileage = request.Mileage,
                RegistrationDate = request.RegistrationDate,
                Note = request.Note,
                CreatedAt = DateTime.UtcNow,
                IsActive = true
            };

            _context.Vehicles.Add(vehicle);
            _context.SaveChanges();

            return vehicle;
        }

        public Vehicle? Update(int id, VehicleCreateDto request)
        {
            var vehicle = _context.Vehicles
                .FirstOrDefault(v => v.Id == id && v.IsActive);

            if (vehicle == null)
            {
                return null;
            }

            vehicle.UserId = request.UserId;
            vehicle.Brand = request.Brand;
            vehicle.Model = request.Model;
            vehicle.Year = request.Year;
            vehicle.LicensePlate = request.LicensePlate;
            vehicle.Vin = request.Vin;
            vehicle.FuelType = request.FuelType;
            vehicle.Mileage = request.Mileage;
            vehicle.RegistrationDate = request.RegistrationDate;
            vehicle.Note = request.Note;

            _context.SaveChanges();

            return vehicle;
        }

        public bool Delete(int id)
        {
            var vehicle = _context.Vehicles
                .FirstOrDefault(v => v.Id == id && v.IsActive);

            if (vehicle == null)
            {
                return false;
            }

            vehicle.IsActive = false;
            _context.SaveChanges();

            return true;
        }
    }
}