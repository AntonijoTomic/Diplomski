using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace DiplomskiAPI.Models
{
    [Table("work_order_parts")]
    public class WorkOrderPartItem
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Column("work_order_id")]
        public int WorkOrderId { get; set; }

        [Column("part_id")]
        public int PartId { get; set; }

        [Column("quantity")]
        public int Quantity { get; set; }

        [Column("unit_price")]
        public decimal UnitPrice { get; set; }

        [Column("total_price")]
        public decimal TotalPrice { get; set; }
    }
}