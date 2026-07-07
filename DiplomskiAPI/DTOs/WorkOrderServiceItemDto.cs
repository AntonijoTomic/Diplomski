namespace DiplomskiAPI.DTOs
{
    public class WorkOrderServiceItemDto
    {
        public int Id { get; set; }

        public decimal Hours { get; set; }

        public decimal HourlyRate { get; set; }

        public decimal TotalPrice { get; set; }

        public ServiceDto Service { get; set; } = new();
    }
}