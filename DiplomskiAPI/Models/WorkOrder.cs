using DiplomskiAPI.Models;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

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

    [Column("admin_id")]
    public int? AdminId { get; set; }

    [Column("diagnosis")]
    public string? Diagnosis { get; set; }

    [Column("status")]
    public string Status { get; set; } = "OPEN";

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

    [ForeignKey("ServiceRequestId")]

    public ServiceRequest ServiceRequest { get; set; } = null!;

    [ForeignKey("AdminId")]
    public User? Admin { get; set; }
}