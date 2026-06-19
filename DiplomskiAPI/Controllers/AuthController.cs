using DiplomskiAPI.Data;
using DiplomskiAPI.DTOs;
using DiplomskiAPI.Models;
using Microsoft.AspNetCore.Mvc;

namespace DiplomskiAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AuthController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        public AuthController(ApplicationDbContext context)
        {
            _context = context;
        }

        [HttpPost("register")]
        public IActionResult Register(RegisterDto request)
        {
            var existingUser = _context.Users
                .FirstOrDefault(u => u.Email == request.Email);

            if (existingUser != null)
            {
                return BadRequest("Korisnik s ovom email adresom već postoji.");
            }

            var passwordHash = BCrypt.Net.BCrypt.HashPassword(request.Password);

            var user = new User
            {
                FirstName = request.FirstName,
                LastName = request.LastName,
                Email = request.Email,
                PasswordHash = passwordHash,
                Phone = request.Phone,
                Address = request.Address,
                Role = "USER",
                IsActive = true,
                CreatedAt = DateTime.UtcNow
            };

            _context.Users.Add(user);
            _context.SaveChanges();

            return Ok("Registracija uspješna.");
        }

        [HttpPost("login")]
        public IActionResult Login(LoginDto request)
        {
            var user = _context.Users.FirstOrDefault(u => u.Email == request.Email);

            if (user == null)
            {
                return BadRequest("Neispravan email ili lozinka.");
            }

            if (!user.IsActive)
            {
                return BadRequest("Korisnički račun je deaktiviran.");
            }

            bool isPasswordValid = BCrypt.Net.BCrypt.Verify(request.Password, user.PasswordHash);

            if (!isPasswordValid)
            {
                return BadRequest("Neispravan email ili lozinka.");
            }

            return Ok(new
            {
                id = user.Id,
                firstName = user.FirstName,
                lastName = user.LastName,
                fullName = user.FirstName + " " + user.LastName,
                email = user.Email,
                role = user.Role
            });
        }

    }
}