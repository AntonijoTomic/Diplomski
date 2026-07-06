namespace DiplomskiAPI.DTOs
{
    public class ServiceRequestDto
    {
        public int Id { get; set; }

        public string ProblemDescription { get; set; } = string.Empty;

        public string? ServiceType { get; set; }

        public string? Urgency { get; set; }

        public string Status { get; set; } = string.Empty;
        public string? Note { get; set; }

        public DateTime CreatedAt { get; set; }
        public DateTime? DesiredDate { get; set; }
        public UserDto User { get; set; } = new();
        public VehicleDto Vehicle { get; set; } = new();
    }
}