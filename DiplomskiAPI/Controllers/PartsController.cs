using DiplomskiAPI.Data;
using DiplomskiAPI.DTOs;
using Microsoft.AspNetCore.Mvc;

namespace DiplomskiAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class PartsController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        public PartsController(ApplicationDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        public IActionResult GetAll()
        {
            var parts = _context.Parts
                .Select(p => new PartDto
                {
                    Id = p.Id,
                    Name = p.Name,
                    Manufacturer = p.Manufacturer,
                    Price = p.Price,
                    StockQuantity = p.StockQuantity,
                    MinimumStock = p.MinimumStock
                })
                .ToList();

            return Ok(parts);
        }
    }
}