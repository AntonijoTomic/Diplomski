using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace DiplomskiAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class PartsController : ControllerBase
    {
        private readonly IPartService _partService;

        public PartsController(IPartService partService)
        {
            _partService = partService;
        }

        [HttpGet]
        public IActionResult GetAll()
        {
            return Ok(_partService.GetAll());
        }

        [HttpPost]
        public IActionResult Create(PartDto dto)
        {
            return Ok(_partService.Create(dto));
        }

        [HttpPut("{id}")]
        public IActionResult Update(int id, PartDto dto)
        {
            var part = _partService.Update(id, dto);

            if (part == null)
            {
                return NotFound();
            }

            return Ok(part);
        }
    }
}