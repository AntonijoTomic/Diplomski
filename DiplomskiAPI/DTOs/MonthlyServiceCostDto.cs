namespace DiplomskiAPI.DTOs
{
    public class MonthlyServiceCostDto
    {
        public string Month { get; set; } = string.Empty;

        public decimal TotalCost { get; set; }
    }
}