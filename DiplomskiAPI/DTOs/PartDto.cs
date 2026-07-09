namespace DiplomskiAPI.DTOs
{
    public class PartDto
    {
        public int Id { get; set; }
        public string Name { get; set; } = string.Empty;
        public string? Manufacturer { get; set; }
        public decimal Price { get; set; }
        public int StockQuantity { get; set; }
        public int MinimumStock { get; set; }
    }
}