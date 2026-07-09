using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace DiplomskiAPI.Models
{
    [Table("work_order_services")]
    public class WorkOrderServiceItem
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Column("work_order_id")]
        public int WorkOrderId { get; set; }

        [Column("service_id")]
        public int ServiceId { get; set; }

        [Column("hours")]
        public decimal Hours { get; set; }

        [Column("hourly_rate")]
        public decimal HourlyRate { get; set; }

        [Column("total_price")]
        public decimal TotalPrice { get; set; }

        [ForeignKey(nameof(WorkOrderId))]
        public WorkOrder WorkOrder { get; set; } = null!;

        [ForeignKey(nameof(ServiceId))]
        public Service Service { get; set; } = null!;
    }
}