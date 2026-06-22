using DiplomskiAPI.Models;
using Microsoft.EntityFrameworkCore;

namespace DiplomskiAPI.Data
{
    public class ApplicationDbContext : DbContext
    {
        public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options)
            : base(options)
        {
        }

        public DbSet<User> Users { get; set; }
        public DbSet<Vehicle> Vehicles { get; set; }
        public DbSet<ServiceRequest> ServiceRequests { get; set; }
        public DbSet<WorkOrder> WorkOrders { get; set; }
    }
}