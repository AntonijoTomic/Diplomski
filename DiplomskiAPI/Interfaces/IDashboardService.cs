using DiplomskiAPI.DTOs;

namespace DiplomskiAPI.Interfaces
{
    public interface IDashboardService
    {
        DashboardSummaryDto GetAdminSummary();
        DashboardSummaryDto GetUserSummary(int userId);
    }
}