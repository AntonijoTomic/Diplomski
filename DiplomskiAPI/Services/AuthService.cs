using DiplomskiAPI.Data;
using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using DiplomskiAPI.Models;

namespace DiplomskiAPI.Services
{
    public class AuthService : IAuthService
    {
        private readonly ApplicationDbContext _context;

        public AuthService(ApplicationDbContext context)
        {
            _context = context;
        }

        public RegisterResponseDto Register(RegisterDto request)
        {

            var existingUser = _context.Users
                .FirstOrDefault(u => u.Email == request.Email);

            if (existingUser != null)
            {
                throw new Exception("Korisnik s ovom email adresom već postoji.");
            }
         

            var passwordHash = BCrypt.Net.BCrypt.HashPassword(request.Password);

            var user = new User
            {
                FirstName = request.FirstName,
                LastName = request.LastName,
                Email = request.Email,
                PasswordHash = passwordHash,
                Phone = request.Phone,
                Address = request.Address,
                Role = "USER",
                IsActive = true,
                CreatedAt = DateTime.UtcNow
            };

            _context.Users.Add(user);
            _context.SaveChanges();

            return new RegisterResponseDto
            {
                Message = "Registracija uspješna."
            };
        }

        public LoginResponseDto Login(LoginDto request)
        {
            var user = _context.Users.FirstOrDefault(u => u.Email == request.Email);

            if (user == null)
            {
                throw new Exception("Neispravan email ili lozinka.");
            }

            if (!user.IsActive)
            {
                throw new Exception("Korisnički račun je deaktiviran.");
            }

            var isPasswordValid = BCrypt.Net.BCrypt.Verify(request.Password, user.PasswordHash);

            if (!isPasswordValid)
            {
                throw new Exception("Neispravan email ili lozinka.");
            }

            return new LoginResponseDto
            {
                Id = user.Id,
                FirstName = user.FirstName,
                LastName = user.LastName,
                FullName = user.FirstName + " " + user.LastName,
                Email = user.Email,
                Role = user.Role
            };
        }
    }
}