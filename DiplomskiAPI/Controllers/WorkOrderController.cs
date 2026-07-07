using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace DiplomskiAPI.Controllers
{
    [Authorize]
    [ApiController]
    [Route("api/[controller]")]
    public class WorkOrdersController : ControllerBase
    {
        private readonly IWorkOrderService _workOrderService;

        public WorkOrdersController(IWorkOrderService workOrderService)
        {
            _workOrderService = workOrderService;
        }

        [HttpGet]
        public IActionResult GetAll()
        {
            return Ok(_workOrderService.GetAll());
        }

        [HttpGet("{id}")]
        public IActionResult GetById(int id)
        {
            var workOrder = _workOrderService.GetById(id);

            if (workOrder == null)
            {
                return NotFound("Radni nalog nije pronađen.");
            }

            return Ok(workOrder);
        }

        [HttpPost]
        public IActionResult Create(WorkOrderCreateDto request)
        {
            var workOrder = _workOrderService.Create(request);
            return Ok(workOrder);
        }

        [HttpPut("{id}/status")]
        public IActionResult UpdateStatus(int id, [FromBody] string status)
        {
            var workOrder = _workOrderService.UpdateStatus(id, status);

            if (workOrder == null)
            {
                return NotFound("Radni nalog nije pronađen.");
            }

            return Ok(workOrder);
        }

        [HttpDelete("{id}")]
        public IActionResult Delete(int id)
        {
            var deleted = _workOrderService.Delete(id);

            if (!deleted)
            {
                return NotFound("Radni nalog nije pronađen.");
            }

            return Ok("Radni nalog je uspješno obrisan.");
        }

        [HttpGet("vehicle/{vehicleId}")]
        public IActionResult GetByVehicleId(int vehicleId)
        {
            return Ok(_workOrderService.GetByVehicleId(vehicleId));
        }

        [HttpGet("user/{userId}")]
        public IActionResult GetByUserId(int userId)
        {
            return Ok(_workOrderService.GetByUserId(userId));
        }

        [HttpGet("{id}/details")]
        public IActionResult GetDetails(int id)
        {
            var workOrder = _workOrderService.GetDetailsById(id);

            if (workOrder == null)
            {
                return NotFound("Radni nalog nije pronađen.");
            }

            return Ok(workOrder);
        }

        [HttpPut("{id}")]
        public IActionResult Update(int id, WorkOrderUpdateDto request)
        {
            var workOrder = _workOrderService.Update(id, request);

            if (workOrder == null)
                return NotFound();

            return Ok(workOrder);
        }

    }
}