using DiplomskiAPI.Data;
using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;

namespace DiplomskiAPI.Services
{
    public class UserService : IUser
    {
        private readonly ApplicationDbContext _context;

        public UserService(ApplicationDbContext context)
        {
            _context = context;
        }

        public void ChangePassword(int userId, ChangePasswordDto request)
        {
            var user = _context.Users.Find(userId);

            if (user == null)
            {
                throw new Exception("Korisnik nije pronađen.");
            }

            bool isPasswordCorrect = BCrypt.Net.BCrypt.Verify(
                request.CurrentPassword,
                user.PasswordHash
            );

            if (!isPasswordCorrect)
            {
                throw new Exception("Trenutna lozinka nije ispravna.");
            }

            if (request.NewPassword != request.ConfirmNewPassword)
            {
                throw new Exception("Nova lozinka i potvrda lozinke nisu jednake.");
            }

            user.PasswordHash = BCrypt.Net.BCrypt.HashPassword(
                request.NewPassword
            );

            _context.SaveChanges();
        }

        public void DeactivateAccount(int userId)
        {
            var user = _context.Users.Find(userId);

            if (user == null)
            {
                throw new Exception("Korisnik nije pronađen.");
            }

            user.IsActive = false;

            _context.SaveChanges();
        }
    }
}