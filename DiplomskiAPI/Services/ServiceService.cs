using DiplomskiAPI.Data;
using DiplomskiAPI.DTOs;
using DiplomskiAPI.Interfaces;
using DiplomskiAPI.Models;

namespace DiplomskiAPI.Services
{
    public class ServiceService : IServiceService
    {
        private readonly ApplicationDbContext _context;

        public ServiceService(ApplicationDbContext context)
        {
            _context = context;
        }

        public List<Service> GetAll()
        {
            return _context.Services.ToList();
        }

        public Service? GetById(int id)
        {
            return _context.Services.FirstOrDefault(s => s.Id == id);
        }

        public Service Create(ServiceCreateDto request)
        {
            var service = new Service
            {
                Name = request.Name,
                Description = request.Description,
                Price = request.Price,
                CreatedAt = DateTime.UtcNow
            };

            _context.Services.Add(service);
            _context.SaveChanges();

            return service;
        }

        public Service? Update(int id, ServiceCreateDto request)
        {
            var service = _context.Services.FirstOrDefault(s => s.Id == id);

            if (service == null)
            {
                return null;
            }

            service.Name = request.Name;
            service.Description = request.Description;
            service.Price = request.Price;

            _context.SaveChanges();

            return service;
        }

        public bool Delete(int id)
        {
            var service = _context.Services.FirstOrDefault(s => s.Id == id);

            if (service == null)
            {
                return false;
            }

            _context.Services.Remove(service);
            _context.SaveChanges();

            return true;
        }
    }
}