using DiplomskiAPI.Data;
using DiplomskiAPI.DTOs;
using DiplomskiAPI.Models;
using Microsoft.AspNetCore.Mvc;

namespace DiplomskiAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class VehiclesController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        public VehiclesController(ApplicationDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        public IActionResult GetVehicles()
        {
            var vehicles = _context.Vehicles.ToList();
            return Ok(vehicles);
        }

        [HttpGet("user/{userId}")]
        public IActionResult GetVehiclesByUser(int userId)
        {
            var vehicles = _context.Vehicles
                .Where(v => v.UserId == userId)
                .ToList();

            return Ok(vehicles);
        }

        [HttpPost]
        public IActionResult CreateVehicle(VehicleCreateDto request)
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
                CreatedAt = DateTime.UtcNow
            };

            _context.Vehicles.Add(vehicle);
            _context.SaveChanges();

            return Ok(vehicle);
        }

        [HttpGet("{id}")]
        public IActionResult GetVehicleById(int id)
        {
            var vehicle = _context.Vehicles.FirstOrDefault(v => v.Id == id);

            if (vehicle == null)
            {
                return NotFound("Vozilo nije pronađeno.");
            }

            return Ok(vehicle);
        }

        [HttpDelete("{id}")]
        public IActionResult DeleteVehicle(int id)
        {
            var vehicle = _context.Vehicles.FirstOrDefault(v => v.Id == id);

            if (vehicle == null)
            {
                return NotFound("Vozilo nije pronađeno.");
            }

            _context.Vehicles.Remove(vehicle);
            _context.SaveChanges();

            return Ok("Vozilo je uspješno obrisano.");
        }

        [HttpPut("{id}")]
        public IActionResult UpdateVehicle(int id, VehicleCreateDto request)
        {
            var vehicle = _context.Vehicles.FirstOrDefault(v => v.Id == id);

            if (vehicle == null)
            {
                return NotFound("Vozilo nije pronađeno.");
            }

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

            return Ok(vehicle);
        }
    }
}