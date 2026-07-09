namespace DiplomskiAPI.DTOs
{
    public class WorkOrderPartItemDto
    {
        public int Id { get; set; }
        public int Quantity { get; set; }
        public decimal UnitPrice { get; set; }
        public decimal TotalPrice { get; set; }
        public PartDto Part { get; set; } = new PartDto();
    }
}