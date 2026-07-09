using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace DiplomskiAPI.Models
{
    [Table("service_requests")]
    public class ServiceRequest
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Column("user_id")]
        public int UserId { get; set; }

        [Column("vehicle_id")]
        public int VehicleId { get; set; }

        [Column("problem_description")]
        public string ProblemDescription { get; set; } = string.Empty;

        [Column("service_type")]
        public string? ServiceType { get; set; }

        [Column("desired_date")]
        public DateTime? DesiredDate { get; set; }

        [Column("urgency")]
        public string? Urgency { get; set; }

        [Column("status")]
        public string Status { get; set; } = "PENDING";

        [Column("note")]
        public string? Note { get; set; }

        [Column("created_at")]
        public DateTime CreatedAt { get; set; }

        [ForeignKey(nameof(UserId))]
        public User User { get; set; } = null!;

        [ForeignKey(nameof(VehicleId))]
        public Vehicle Vehicle { get; set; } = null!;

        public WorkOrder? WorkOrder { get; set; }
    }
}