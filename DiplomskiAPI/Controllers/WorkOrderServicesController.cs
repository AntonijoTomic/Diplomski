using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace DiplomskiAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class WorkOrderServicesController : ControllerBase
    {
        private readonly IWorkOrderServiceItemService _workOrderServiceItemService;

        public WorkOrderServicesController(IWorkOrderServiceItemService workOrderServiceItemService)
        {
            _workOrderServiceItemService = workOrderServiceItemService;
        }

        [HttpGet("work-order/{workOrderId}")]
        public IActionResult GetByWorkOrderId(int workOrderId)
        {
            return Ok(_workOrderServiceItemService.GetByWorkOrderId(workOrderId));
        }

        [HttpPost]
        public IActionResult AddServiceToWorkOrder(WorkOrderServiceCreateDto request)
        {
            var result = _workOrderServiceItemService.AddServiceToWorkOrder(request);

            if (result == null)
            {
                return NotFound("Servisna usluga nije pronađena.");
            }

            return Ok(result);
        }

        [HttpDelete("{id}")]
        public IActionResult Delete(int id)
        {
            var deleted = _workOrderServiceItemService.Delete(id);

            if (!deleted)
            {
                return NotFound("Usluga na radnom nalogu nije pronađena.");
            }

            return Ok("Usluga je uspješno uklonjena s radnog naloga.");
        }
    }
}