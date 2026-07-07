namespace DiplomskiAPI.DTOs
{
    public class WorkOrderDto
    {
        public int Id { get; set; }

        public string OrderNumber { get; set; } = string.Empty;

        public int ServiceRequestId { get; set; }

        public int? AdminId { get; set; }

        public string? Diagnosis { get; set; }

        public string Status { get; set; } = string.Empty;

        public decimal EstimatedCost { get; set; }

        public decimal FinalCost { get; set; }

        public DateTime OpenedAt { get; set; }

        public DateTime? ClosedAt { get; set; }

        public string? FinalReport { get; set; }

        public ServiceRequestDto ServiceRequest { get; set; } = new();
    }
}