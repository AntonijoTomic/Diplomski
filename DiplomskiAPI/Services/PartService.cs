using DiplomskiAPI.Data;
using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using DiplomskiAPI.Models;

namespace DiplomskiAPI.Services
{
    public class PartService : IPartService
    {
        private readonly ApplicationDbContext _context;

        public PartService(ApplicationDbContext context)
        {
            _context = context;
        }

        public Part Create(PartDto dto)
        {
            var part = new Part
            {
                Name = dto.Name,
                Manufacturer = dto.Manufacturer,
                Price = dto.Price,
                StockQuantity = dto.StockQuantity,
                MinimumStock = dto.MinimumStock
            };

            _context.Parts.Add(part);
            _context.SaveChanges();

            return part;
        }

        public List<PartDto> GetAll()
        {
            return _context.Parts
                .Select(p => new PartDto
                {
                    Id = p.Id,
                    Name = p.Name,
                    Manufacturer = p.Manufacturer,
                    Price = p.Price,
                    StockQuantity = p.StockQuantity,
                    MinimumStock = p.MinimumStock
                })
                .ToList();
        }
        public Part? Update(int id, PartDto dto)
        {
            var part = _context.Parts
                .FirstOrDefault(p => p.Id == id);

            if (part == null)
            {
                return null;
            }

            part.Name = dto.Name;
            part.Manufacturer = dto.Manufacturer;
            part.Price = dto.Price;
            part.StockQuantity = dto.StockQuantity;
            part.MinimumStock = dto.MinimumStock;

            _context.SaveChanges();

            return part;
        }

    }
}