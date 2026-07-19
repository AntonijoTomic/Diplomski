using DiplomskiAPI.DTOs;

namespace DiplomskiAPI.Interfaces
{
    public interface IAiRecommendationService
    {
        Task<AiRecommendationResponseDto?> RecommendAsync(int workOrderId);
    }
}
