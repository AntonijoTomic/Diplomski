using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace DiplomskiAPI.Models
{
    [Table("vehicles")]
    public class Vehicle
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Column("user_id")]
        public int UserId { get; set; }

        [Column("brand")]
        public string Brand { get; set; } = string.Empty;

        [Column("model")]
        public string Model { get; set; } = string.Empty;

        [Column("year")]
        public int Year { get; set; }

        [Column("license_plate")]
        public string? LicensePlate { get; set; }

        [Column("vin")] //šasija
        public string? Vin { get; set; }

        [Column("fuel_type")]
        public string? FuelType { get; set; }

        [Column("mileage")]
        public int Mileage { get; set; }

        [Column("registration_date")]
        public DateTime? RegistrationDate { get; set; }

        [Column("note")]
        public string? Note { get; set; }

        [Column("created_at")]
        public DateTime CreatedAt { get; set; }

        [ForeignKey(nameof(UserId))]
        public User User { get; set; } = null!;
        [Column("is_active")]
        public bool IsActive { get; set; } = true;

        public ICollection<ServiceRequest> ServiceRequests { get; set; } = new List<ServiceRequest>();
    }
}