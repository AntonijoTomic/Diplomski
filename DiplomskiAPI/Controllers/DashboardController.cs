using System.Security.Claims;
using DiplomskiAPI.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace DiplomskiAPI.Controllers
{
    [ApiController]
    [Route("api/dashboard")]
    [Authorize]
    public class DashboardController : ControllerBase
    {
        private readonly IDashboardService _dashboardService;

        public DashboardController(
            IDashboardService dashboardService)
        {
            _dashboardService = dashboardService;
        }

        [HttpGet("summary")]
        public IActionResult GetSummary()
        {
            var role = User.FindFirst(
                ClaimTypes.Role
            )?.Value;

            var userIdClaim = User.FindFirst(
                ClaimTypes.NameIdentifier
            )?.Value;

            if (!int.TryParse(userIdClaim, out int userId))
            {
                return Unauthorized();
            }

            var dto = role == "ADMIN"
                ? _dashboardService.GetAdminSummary()
                : _dashboardService.GetUserSummary(userId);

            return Ok(dto);
        }
    }
}