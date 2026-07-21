using DiplomskiAPI.DTOs;

namespace DiplomskiAPI.Services.Interfaces
{
    public interface IUserDashboardService
    {
        UserDashboardDto GetDashboard(int userId);
    }
}