using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace DiplomskiAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class WorkOrderPartsController : ControllerBase
    {
        private readonly IWorkOrderPartItemService _workOrderPartItemService;

        public WorkOrderPartsController(IWorkOrderPartItemService workOrderPartItemService)
        {
            _workOrderPartItemService = workOrderPartItemService;
        }

        [HttpGet("work-order/{workOrderId}")]
        public IActionResult GetByWorkOrderId(int workOrderId)
        {
            return Ok(_workOrderPartItemService.GetByWorkOrderId(workOrderId));
        }

        [HttpPost]
        public IActionResult AddPartToWorkOrder(WorkOrderPartCreateDto request)
        {
            var result = _workOrderPartItemService.AddPartToWorkOrder(request);

            if (result == null)
            {
                return NotFound("Autodio nije pronađen ili količina nije ispravna.");
            }

            return Ok(result);
        }

        [HttpDelete("{id}")]
        public IActionResult Delete(int id)
        {
            var deleted = _workOrderPartItemService.Delete(id);

            if (!deleted)
            {
                return NotFound("Autodio na radnom nalogu nije pronađen.");
            }

            return Ok("Autodio je uspješno uklonjen s radnog naloga.");
        }
    }
}