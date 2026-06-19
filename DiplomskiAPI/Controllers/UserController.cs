using DiplomskiAPI.Data;
using Microsoft.AspNetCore.Mvc;

namespace DiplomskiAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class UsersController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        public UsersController(ApplicationDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        public IActionResult GetUsers()
        {
            var users = _context.Users.ToList();

            return Ok(users);
        }
    }
}