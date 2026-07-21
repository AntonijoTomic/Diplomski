namespace DiplomskiAPI.DTOs
{
    public class UserDashboardDto
    {
        public int VehicleCount { get; set; }

        public int ActiveServiceRequestCount { get; set; }

        public decimal TotalServiceCost { get; set; }

        public List<VehicleDto> Vehicles { get; set; } = new();
        public List<MonthlyServiceCostDto> MonthlyServiceCosts { get; set; } = new();
    }
}