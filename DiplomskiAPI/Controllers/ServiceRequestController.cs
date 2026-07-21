using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace DiplomskiAPI.Controllers
{
    [Authorize]
    [ApiController]
    [Route("api/[controller]")]
    public class ServiceRequestsController : ControllerBase
    {
        private readonly IServiceRequestService _serviceRequestService;

        public ServiceRequestsController(IServiceRequestService serviceRequestService)
        {
            _serviceRequestService = serviceRequestService;
        }

        [HttpGet]
        public IActionResult GetAll()
        {
            return Ok(_serviceRequestService.GetAll());
        }

        [HttpGet("{id}")]
        public IActionResult GetById(int id)
        {
            var request = _serviceRequestService.GetById(id);

            if (request == null)
            {
                return NotFound("Servisni zahtjev nije pronađen.");
            }

            return Ok(request);
        }

        [HttpGet("user/{userId}")]
        public IActionResult GetByUserId(int userId)
        {
            return Ok(_serviceRequestService.GetByUserId(userId));
        }

        [HttpPost]
        public IActionResult Create(ServiceRequestCreateDto request)
        {
            try
            {
                var createdRequest = _serviceRequestService.Create(request);
                return Ok(createdRequest);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpPut("{id}/status")]
        public IActionResult UpdateStatus(int id, [FromBody] string status)
        {
            var updatedRequest = _serviceRequestService.UpdateStatus(id, status);

            if (updatedRequest == null)
            {
                return NotFound("Servisni zahtjev nije pronađen.");
            }

            return Ok(updatedRequest);
        }

        [HttpDelete("{id}")]
        public IActionResult Delete(int id)
        {
            var deleted = _serviceRequestService.Delete(id);

            if (!deleted)
            {
                return NotFound("Servisni zahtjev nije pronađen.");
            }

            return Ok("Servisni zahtjev je uspješno obrisan.");
        }

        [HttpGet("vehicle/{vehicleId}")]
        public IActionResult GetByVehicleId(int vehicleId)
        {
            return Ok(_serviceRequestService.GetByVehicleId(vehicleId));
        }

        [HttpPut("{id}")]
        public IActionResult Update(int id, ServiceRequestCreateDto request)
        {
            try
            {
                var updatedRequest = _serviceRequestService.Update(id, request);

                if (updatedRequest == null)
                {
                    return NotFound("Servisni zahtjev nije pronađen.");
                }

                return Ok(updatedRequest);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }
    }

}