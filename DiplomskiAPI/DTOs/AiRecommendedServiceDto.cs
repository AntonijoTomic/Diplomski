namespace DiplomskiAPI.DTOs
{
    public class AiRecommendedServiceDto
    {
        public int Id { get; set; }

        public string Name { get; set; } = string.Empty;

        public decimal Hours { get; set; }
        public double HourlyRate { get; set; }

    }
}