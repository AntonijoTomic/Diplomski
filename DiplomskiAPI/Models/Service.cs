using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace DiplomskiAPI.Models
{
    [Table("services")]
    public class Service
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Column("name")]
        public string Name { get; set; } = string.Empty;

        [Column("description")]
        public string? Description { get; set; }

        [Column("price")]
        public decimal Price { get; set; }

        [Column("created_at")]
        public DateTime CreatedAt { get; set; }

        public ICollection<WorkOrderServiceItem> WorkOrderServices { get; set; } = new List<WorkOrderServiceItem>();
    }
}