using DiplomskiAPI.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace DiplomskiAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize(Roles = "ADMIN")]
    public class AiRecommendationsController : ControllerBase
    {
        private readonly IAiRecommendationService _aiRecommendationService;

        public AiRecommendationsController(
            IAiRecommendationService aiRecommendationService)
        {
            _aiRecommendationService = aiRecommendationService;
        }

        [HttpPost("work-orders/{workOrderId}/recommend-parts")]
        public async Task<IActionResult> RecommendParts(int workOrderId)
        {
            var result = await _aiRecommendationService
                .RecommendPartsAsync(workOrderId);

            if (result == null)
            {
                return NotFound("Radni nalog nije pronađen.");
            }

            return Ok(result);
        }
    }
}