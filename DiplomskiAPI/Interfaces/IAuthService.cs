using DiplomskiAPI.DTOs;
namespace DiplomskiAPI.Interfaces
{
    public interface IAuthService
    {
        RegisterResponseDto Register(RegisterDto request);
        LoginResponseDto Login(LoginDto request);
    }
}
