using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace DiplomskiAPI.Controllers
{
    [Authorize]
    [ApiController]
    [Route("api/[controller]")]
    public class VehiclesController : ControllerBase
    {
        private readonly IVehicleService _vehicleService;

        public VehiclesController(IVehicleService vehicleService)
        {
            _vehicleService = vehicleService;
        }

        [HttpGet]
        public IActionResult GetVehicles()
        {
            var vehicles = _vehicleService.GetAll();
            return Ok(vehicles);
        }

        [HttpGet("user/{userId}")]
        public IActionResult GetVehiclesByUser(int userId)
        {
            var vehicles = _vehicleService.GetByUserId(userId);
            return Ok(vehicles);
        }

        [HttpGet("{id}")]
        public IActionResult GetVehicleById(int id)
        {
            var vehicle = _vehicleService.GetById(id);

            if (vehicle == null)
            {
                return NotFound("Vozilo nije pronađeno.");
            }

            return Ok(vehicle);
        }

        [HttpPost]
        public IActionResult CreateVehicle(VehicleCreateDto request)
        {
            var vehicle = _vehicleService.Create(request);
            return Ok(vehicle);
        }

        [HttpPut("{id}")]
        public IActionResult UpdateVehicle(int id, VehicleCreateDto request)
        {
            var vehicle = _vehicleService.Update(id, request);

            if (vehicle == null)
            {
                return NotFound("Vozilo nije pronađeno.");
            }

            return Ok(vehicle);
        }

        [HttpDelete("{id}")]
        public IActionResult DeleteVehicle(int id)
        {
            var deleted = _vehicleService.Delete(id);

            if (!deleted)
            {
                return NotFound("Vozilo nije pronađeno.");
            }

            return Ok("Vozilo je uspješno obrisano.");
        }
    }
}