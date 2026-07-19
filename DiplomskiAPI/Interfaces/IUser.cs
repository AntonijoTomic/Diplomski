using DiplomskiAPI.DTOs;

namespace DiplomskiAPI.Interfaces
{
    public interface IUser
    {
        void ChangePassword(int userId, ChangePasswordDto request);

        void DeactivateAccount(int userId);
    }
}
