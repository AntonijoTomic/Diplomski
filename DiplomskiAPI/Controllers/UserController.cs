using DiplomskiAPI.Data;
using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using DiplomskiAPI.Services.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Security.Claims;

namespace DiplomskiAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class UsersController : ControllerBase
    {
        private readonly ApplicationDbContext _context;
        private readonly IUser _userService;
        private readonly IUserDashboardService _userDashboardService;

        public UsersController(ApplicationDbContext context, IUser userService, IUserDashboardService userDashboardService)
        {
            _context = context;
            _userService = userService;
            _userDashboardService= userDashboardService;
        }

        [HttpGet]
        public IActionResult GetUsers()
        {
            var users = _context.Users.ToList();

            return Ok(users);
        }

        [HttpPut("profile")]
        [Authorize]
        public async Task<IActionResult> UpdateProfile(UpdateProfileDto request)
        {
            var userIdClaim = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;

            if (!int.TryParse(userIdClaim, out int userId))
            {
                return Unauthorized();
            }

            var user = await _context.Users.FindAsync(userId);

            if (user == null)
            {
                return NotFound();
            }

            user.FirstName = request.FirstName;
            user.LastName = request.LastName;
            user.Phone = request.PhoneNumber;
            user.Address = request.Address;

            await _context.SaveChangesAsync();

            return Ok();
        }

        [HttpGet("profile")]
        [Authorize]
        public async Task<IActionResult> GetProfile()
        {
            var userIdClaim = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;

            if (!int.TryParse(userIdClaim, out int userId))
            {
                return Unauthorized(new
                {
                    message = "JWT ne sadrži ispravan korisnički ID.",
                    claimValue = userIdClaim
                });
            }

            var user = await _context.Users
                .Where(u => u.Id == userId)
                .Select(u => new
                {
                    u.Id,
                    u.FirstName,
                    u.LastName,
                    u.Email,
                    PhoneNumber = u.Phone,
                    u.Address,
                    u.Role,
                    u.IsActive
                })
                .FirstOrDefaultAsync();

            if (user == null)
            {
                return NotFound(new
                {
                    message = "Korisnik nije pronađen.",
                    userIdFromToken = userId
                });
            }

            return Ok(user);
        }

        [HttpPut("change-password")]
        [Authorize]
        public IActionResult ChangePassword(ChangePasswordDto request)
        {
            var userIdClaim = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;

            int userId = int.Parse(userIdClaim!);

            _userService.ChangePassword(userId, request);

            return Ok();
        }

        [HttpPut("deactivate")]
        [Authorize]
        public IActionResult Deactivate()
        {
            var userIdClaim = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;

            int userId = int.Parse(userIdClaim!);

            _userService.DeactivateAccount(userId);

            return Ok();
        }

        [HttpGet("dashboard")]
        [Authorize]
        public IActionResult GetDashboard()
        {
            var userIdClaim = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;

            if (string.IsNullOrEmpty(userIdClaim))
                return Unauthorized();

            var dashboard = _userDashboardService.GetDashboard(int.Parse(userIdClaim));

            return Ok(dashboard);
        }
    }
}