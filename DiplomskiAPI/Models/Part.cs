using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace DiplomskiAPI.Models
{
    [Table("parts")]
    public class Part
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Column("name")]
        public string Name { get; set; } = string.Empty;

        [Column("manufacturer")]
        public string? Manufacturer { get; set; }

        [Column("price")]
        public decimal Price { get; set; }

        [Column("stock_quantity")]
        public int StockQuantity { get; set; }

        [Column("minimum_stock")]
        public int MinimumStock { get; set; }

        [Column("created_at")]
        public DateTime CreatedAt { get; set; }
        public ICollection<WorkOrderPartItem> WorkOrderParts { get; set; } = new List<WorkOrderPartItem>();
    }
}