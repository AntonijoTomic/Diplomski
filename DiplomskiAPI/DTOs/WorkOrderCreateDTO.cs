namespace DiplomskiAPI.DTOs
{
    public class WorkOrderCreateDto
    {
        public int ServiceRequestId { get; set; }

        public int UserId { get; set; }

        public int VehicleId { get; set; }

        public int? AdminId { get; set; }

        public string ProblemDescription { get; set; } = string.Empty;

        public string? Diagnosis { get; set; }

        public decimal EstimatedCost { get; set; }
    }
}