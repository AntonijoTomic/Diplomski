namespace DiplomskiAPI.DTOs
{
    public class ServiceRequestDto
    {
        public int Id { get; set; }

        public int VehicleId { get; set; }

        public string VehicleName { get; set; } = string.Empty;

        public string LicensePlate { get; set; } = string.Empty;

        public string ProblemDescription { get; set; } = string.Empty;

        public string? ServiceType { get; set; }

        public string? Urgency { get; set; }

        public string Status { get; set; } = string.Empty;

        public DateTime CreatedAt { get; set; }
    }
}