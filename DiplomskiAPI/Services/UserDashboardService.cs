using DiplomskiAPI.Data;
using DiplomskiAPI.DTOs;
using DiplomskiAPI.Services.Interfaces;
using System.Globalization;
namespace DiplomskiAPI.Services
{
    public class UserDashboardService : IUserDashboardService
    {
        private readonly ApplicationDbContext _context;

        public UserDashboardService(ApplicationDbContext context)
        {
            _context = context;
        }

        public UserDashboardDto GetDashboard(int userId)
        {
            var maintenanceServices = new[]
                {
                    "Mali servis",
                    "Veliki servis"
                };

            var vehicles = _context.Vehicles
                .Where(v => v.UserId == userId && v.IsActive)
                .Select(v => new VehicleDto
                {
                    Id = v.Id,
                    Brand = v.Brand,
                    Model = v.Model,
                    Year = v.Year,
                    LicensePlate = v.LicensePlate,
                    Vin = v.Vin,
                    FuelType = v.FuelType,
                    Mileage = v.Mileage,
                    RegistrationDate = v.RegistrationDate,
                    Note = v.Note,

                        LastServiceDate = _context.WorkOrderServices.Where(wos =>
                        wos.WorkOrder.ServiceRequest.VehicleId == v.Id &&
                        wos.WorkOrder.ClosedAt != null &&
                        maintenanceServices.Contains(wos.Service.Name))
                    .OrderByDescending(wos => wos.WorkOrder.ClosedAt)
                    .Select(wos => wos.WorkOrder.ClosedAt)
                    .FirstOrDefault()
                    })
               .ToList();
                 
              

            var activeServiceRequestCount = _context.ServiceRequests
                .Count(sr =>
                    sr.UserId == userId &&
                    sr.Status != "COMPLETED" &&
                    sr.Status != "CANCELLED");

            var totalServiceCost = _context.WorkOrders
                .Where(wo =>
                    wo.ServiceRequest.UserId == userId &&
                    wo.Status == "COMPLETED")
                .Sum(wo => (decimal?)wo.FinalCost) ?? 0;

            var monthlyServiceCosts = Enumerable
                .Range(0, 6)
                .Select(i =>
                {
                    var date = DateTime.Today.AddMonths(-(5 - i));

                    var total = _context.WorkOrders
                        .Where(wo =>
                            wo.ServiceRequest.UserId == userId &&
                            wo.Status == "COMPLETED" &&
                            wo.ClosedAt != null &&
                            wo.ClosedAt.Value.Year == date.Year &&
                            wo.ClosedAt.Value.Month == date.Month)
                        .Sum(wo => (decimal?)wo.FinalCost) ?? 0;

                    return new MonthlyServiceCostDto
                    {
                        Month = date.ToString("MMM", new CultureInfo("hr-HR")),
                        TotalCost = total
                    };
                })
                .ToList();

            return new UserDashboardDto
            {
                VehicleCount = vehicles.Count,
                ActiveServiceRequestCount = activeServiceRequestCount,
                TotalServiceCost = totalServiceCost,
                Vehicles = vehicles,
                MonthlyServiceCosts = monthlyServiceCosts
            };
        }
    }
}