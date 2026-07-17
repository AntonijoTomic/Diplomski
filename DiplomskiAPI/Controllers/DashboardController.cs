using System.Security.Claims;
using DiplomskiAPI.Data;
using DiplomskiAPI.DTOs;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace DiplomskiAPI.Controllers
{
    [ApiController]
    [Route("api/dashboard")]
    [Authorize]
    public class DashboardController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        public DashboardController(ApplicationDbContext context)
        {
            _context = context;
        }

        [HttpGet("summary")]
        public IActionResult GetSummary()
        {
            var role = User.FindFirst(ClaimTypes.Role)?.Value;
            var userIdClaim = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;

            if (!int.TryParse(userIdClaim, out int userId))
            {
                return Unauthorized();
            }

            DashboardSummaryDto dto;

            if (role == "ADMIN")
            {
                dto = new DashboardSummaryDto
                {
                    VehicleCount = _context.Vehicles.Count(),
                    ServiceRequestCount = _context.ServiceRequests.Count(),
                    WorkOrderCount = _context.WorkOrders.Count(),
                    UserCount = _context.Users.Count()
                };
            }
            else
            {
                dto = new DashboardSummaryDto
                {
                    VehicleCount = _context.Vehicles.Count(v => v.UserId == userId),
                    ServiceRequestCount = _context.ServiceRequests.Count(sr => sr.UserId == userId),
                    WorkOrderCount = _context.WorkOrders.Count(w => w.ServiceRequest.UserId == userId),
                    UserCount = 0
                };
            }

            return Ok(dto);
        }
    }
}