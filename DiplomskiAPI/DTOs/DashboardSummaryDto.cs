namespace DiplomskiAPI.DTOs
{
    public class DashboardSummaryDto
    {
        public int VehicleCount { get; set; }
        public int ServiceRequestCount { get; set; }
        public int WorkOrderCount { get; set; }
        public int UserCount { get; set; }

        public int PendingServiceRequestCount { get; set; }
        public int ApprovedServiceRequestCount { get; set; }

        public decimal TotalRevenue { get; set; }
        public decimal CurrentMonthRevenue { get; set; }

        public int LowStockPartCount { get; set; }
        public int OutOfStockPartCount { get; set; }
        public List<MonthlyServiceCostDto> MonthlyRevenue { get; set; } = new();
        public List<PartDto> LowStockParts { get; set; } = new();

    }
}