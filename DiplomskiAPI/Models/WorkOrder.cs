using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace DiplomskiAPI.Models
{
    [Table("work_orders")]
    public class WorkOrder
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Column("order_number")]
        public string OrderNumber { get; set; } = string.Empty;

        [Column("service_request_id")]
        public int ServiceRequestId { get; set; }

        [Column("user_id")]
        public int UserId { get; set; }

        [Column("vehicle_id")]
        public int VehicleId { get; set; }

        [Column("admin_id")]
        public int? AdminId { get; set; }

        [Column("problem_description")]
        public string ProblemDescription { get; set; } = string.Empty;

        [Column("diagnosis")]
        public string? Diagnosis { get; set; }

        [Column("status")]
        public string Status { get; set; } = "RECEIVED";

        [Column("estimated_cost")]
        public decimal EstimatedCost { get; set; }

        [Column("final_cost")]
        public decimal FinalCost { get; set; }

        [Column("opened_at")]
        public DateTime OpenedAt { get; set; }

        [Column("closed_at")]
        public DateTime? ClosedAt { get; set; }

        [Column("final_report")]
        public string? FinalReport { get; set; }
    }
}