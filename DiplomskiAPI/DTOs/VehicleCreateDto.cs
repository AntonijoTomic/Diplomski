namespace DiplomskiAPI.DTOs
{
    public class VehicleCreateDto
    {
        public int UserId { get; set; }

        public string Brand { get; set; } = string.Empty;

        public string Model { get; set; } = string.Empty;

        public int Year { get; set; }

        public string? LicensePlate { get; set; }

        public string? Vin { get; set; }

        public string? FuelType { get; set; }

        public int Mileage { get; set; }

        public DateTime? RegistrationDate { get; set; }

        public string? Note { get; set; }
    }
}