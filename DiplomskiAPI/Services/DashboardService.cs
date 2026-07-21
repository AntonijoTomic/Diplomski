using DiplomskiAPI.Data;
using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace DiplomskiAPI.Services
{
    public class DashboardService : IDashboardService
    {
        private readonly ApplicationDbContext _context;

        public DashboardService(ApplicationDbContext context)
        {
            _context = context;
        }

        public DashboardSummaryDto GetAdminSummary()
        {
            var currentDate = DateTime.UtcNow;

            var firstDayOfCurrentMonth = new DateTime(
                currentDate.Year,
                currentDate.Month,
                1,
                0,
                0,
                0,
                DateTimeKind.Utc
            );


            return new DashboardSummaryDto
            {
                VehicleCount = _context.Vehicles.Count(),

                ServiceRequestCount =
                    _context.ServiceRequests.Count(),

                WorkOrderCount =
                    _context.WorkOrders.Count(),

                UserCount =
                    _context.Users.Count(),

                PendingServiceRequestCount =
                    _context.ServiceRequests.Count(
                        sr => sr.Status == "PENDING"
                    ),

                ApprovedServiceRequestCount =
                    _context.ServiceRequests.Count(
                        sr => sr.Status == "IN_PROGRESS"
                    ),

                TotalRevenue = _context.WorkOrders
                    .Where(wo => wo.Status == "COMPLETED")
                    .Sum(wo => wo.FinalCost),

                CurrentMonthRevenue = _context.WorkOrders
                    .Where(wo =>
                        wo.Status == "COMPLETED" &&
                        wo.ClosedAt.HasValue &&
                        wo.ClosedAt.Value >= firstDayOfCurrentMonth)
                    .Sum(wo => wo.FinalCost),

                LowStockPartCount = _context.Parts
                    .Count(p =>
                        p.StockQuantity <= p.MinimumStock),

                OutOfStockPartCount = _context.Parts
                    .Count(p => p.StockQuantity == 0),
                MonthlyRevenue = GetMonthlyRevenue(),
                LowStockParts = GetLowStockParts()
            };

        }

        public DashboardSummaryDto GetUserSummary(int userId)
        {
            return new DashboardSummaryDto
            {
                VehicleCount = _context.Vehicles
                    .Count(v => v.UserId == userId),

                ServiceRequestCount = _context.ServiceRequests
                    .Count(sr => sr.UserId == userId),

                WorkOrderCount = _context.WorkOrders
                    .Count(w =>
                        w.ServiceRequest.UserId == userId),

                UserCount = 0
            };
        }
        private List<MonthlyServiceCostDto> GetMonthlyRevenue()
        {
            var currentMonth = new DateTime(
                    DateTime.UtcNow.Year,
                    DateTime.UtcNow.Month,
                    1,
                    0,
                    0,
                    0,
                    DateTimeKind.Utc
                );
            var firstMonth = currentMonth.AddMonths(-5);

            var revenueByMonth = _context.WorkOrders
                .Where(wo =>
                    wo.Status == "COMPLETED" &&
                    wo.ClosedAt.HasValue &&
                    wo.ClosedAt.Value >= firstMonth)
                .GroupBy(wo => new
                {
                    wo.ClosedAt!.Value.Year,
                    wo.ClosedAt.Value.Month
                })
                .Select(group => new
                {
                    group.Key.Year,
                    group.Key.Month,
                    TotalCost = group.Sum(wo => wo.FinalCost)
                })
                .ToList();

            var result = new List<MonthlyServiceCostDto>();

            for (int i = 0; i < 6; i++)
            {
                var month = firstMonth.AddMonths(i);

                var revenue = revenueByMonth.FirstOrDefault(r =>
                    r.Year == month.Year &&
                    r.Month == month.Month);

                result.Add(new MonthlyServiceCostDto
                {
                    Month = month.ToString("MMM"),
                    TotalCost = revenue?.TotalCost ?? 0
                });
            }

            return result;
        }
        private List<PartDto> GetLowStockParts()
        {
            return _context.Parts
                .Where(p => p.StockQuantity <= p.MinimumStock)
                .OrderBy(p => p.StockQuantity)
                .Select(p => new PartDto
                {
                    Id = p.Id,
                    Name = p.Name,
                    Manufacturer = p.Manufacturer,
                    Price = p.Price,
                    StockQuantity = p.StockQuantity,
                    MinimumStock = p.MinimumStock
                })
                .Take(5)
                .ToList();
        }

        
    }
}