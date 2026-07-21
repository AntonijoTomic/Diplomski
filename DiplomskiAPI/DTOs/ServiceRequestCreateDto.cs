namespace DiplomskiAPI.DTOs
{
    public class ServiceRequestCreateDto
    {
        public int UserId { get; set; }

        public int VehicleId { get; set; }

        public string ProblemDescription { get; set; } = string.Empty;

        public string? ServiceType { get; set; }

        public DateTime? DesiredDate { get; set; }

        public string? Urgency { get; set; }

        public string? Note { get; set; }

        public int CurrentMileage { get; set; }

    }
}