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
        public DbSet<Service> Services { get; set; }
        public DbSet<WorkOrderServiceItem> WorkOrderServices { get; set; }
        public DbSet<Part> Parts { get; set; }
        public DbSet<WorkOrderPartItem> WorkOrderParts { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            modelBuilder.Entity<Vehicle>()
                .HasOne(v => v.User)
                .WithMany(u => u.Vehicles)
                .HasForeignKey(v => v.UserId)
                .OnDelete(DeleteBehavior.Restrict);

            modelBuilder.Entity<ServiceRequest>()
                .HasOne(sr => sr.User)
                .WithMany(u => u.ServiceRequests)
                .HasForeignKey(sr => sr.UserId)
                .OnDelete(DeleteBehavior.Restrict);

            modelBuilder.Entity<ServiceRequest>()
                .HasOne(sr => sr.Vehicle)
                .WithMany(v => v.ServiceRequests)
                .HasForeignKey(sr => sr.VehicleId)
                .OnDelete(DeleteBehavior.Restrict);

            modelBuilder.Entity<WorkOrder>()
                .HasOne(wo => wo.ServiceRequest)
                .WithOne(sr => sr.WorkOrder)
                .HasForeignKey<WorkOrder>(wo => wo.ServiceRequestId)
                .OnDelete(DeleteBehavior.Restrict);

            modelBuilder.Entity<WorkOrderPartItem>()
                .HasOne(wop => wop.WorkOrder)
                .WithMany()
                .HasForeignKey(wop => wop.WorkOrderId)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<WorkOrderPartItem>()
                .HasOne(wop => wop.Part)
                .WithMany(p => p.WorkOrderParts)
                .HasForeignKey(wop => wop.PartId)
                .OnDelete(DeleteBehavior.Restrict);

            modelBuilder.Entity<WorkOrderServiceItem>()
                .HasOne(wos => wos.WorkOrder)
                .WithMany()
                .HasForeignKey(wos => wos.WorkOrderId)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<WorkOrderServiceItem>()
                .HasOne(wos => wos.Service)
                .WithMany(s => s.WorkOrderServices)
                .HasForeignKey(wos => wos.ServiceId)
                .OnDelete(DeleteBehavior.Restrict);
        }
    }
}