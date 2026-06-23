using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace DiplomskiAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ServicesController : ControllerBase
    {
        private readonly IServiceService _serviceService;

        public ServicesController(IServiceService serviceService)
        {
            _serviceService = serviceService;
        }

        [HttpGet]
        public IActionResult GetAll()
        {
            return Ok(_serviceService.GetAll());
        }

        [HttpGet("{id}")]
        public IActionResult GetById(int id)
        {
            var service = _serviceService.GetById(id);

            if (service == null)
            {
                return NotFound("Servisna usluga nije pronađena.");
            }

            return Ok(service);
        }

        [HttpPost]
        public IActionResult Create(ServiceCreateDto request)
        {
            var service = _serviceService.Create(request);
            return Ok(service);
        }

        [HttpPut("{id}")]
        public IActionResult Update(int id, ServiceCreateDto request)
        {
            var service = _serviceService.Update(id, request);

            if (service == null)
            {
                return NotFound("Servisna usluga nije pronađena.");
            }

            return Ok(service);
        }

        [HttpDelete("{id}")]
        public IActionResult Delete(int id)
        {
            var deleted = _serviceService.Delete(id);

            if (!deleted)
            {
                return NotFound("Servisna usluga nije pronađena.");
            }

            return Ok("Servisna usluga je uspješno obrisana.");
        }
    }
}