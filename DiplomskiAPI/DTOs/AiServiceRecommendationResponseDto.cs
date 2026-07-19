using DiplomskiAPI.DTOs;

public class AiRecommendationResponseDto
{
    public List<AiRecommendedPartDto> Parts { get; set; } = new();

    public List<AiRecommendedServiceDto> Services { get; set; } = new();
}