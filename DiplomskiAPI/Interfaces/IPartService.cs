using DiplomskiAPI.DTOs;
using DiplomskiAPI.Models;

namespace DiplomskiAPI.Interfaces
{
    public interface IPartService
    {
        Part Create(PartDto dto);
        List<PartDto> GetAll();
        Part? Update(int id, PartDto dto);
    }
}
