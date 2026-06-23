namespace DiplomskiAPI.DTOs
{
    public class WorkOrderServiceCreateDto
    {
        public int WorkOrderId { get; set; }

        public int ServiceId { get; set; }

        public decimal Hours { get; set; }
    }
}