using DiplomskiAPI.DTOs;

namespace DiplomskiAPI.Interfaces
{
    public interface IAiRecommendationService
    {
        Task<AiPartRecommendationResponseDto?> RecommendPartsAsync(int workOrderId);
    }
}
