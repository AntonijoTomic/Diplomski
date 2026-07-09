namespace DiplomskiAPI.DTOs
{
    public class WorkOrderPartCreateDto
    {
        public int WorkOrderId { get; set; }
        public int PartId { get; set; }
        public int Quantity { get; set; }
    }
}