namespace DiplomskiAPI.DTOs
{
    public class UpdateProfileDto
    {
        public string FirstName { get; set; } = string.Empty;

        public string LastName { get; set; } = string.Empty;

        public string? PhoneNumber { get; set; }

        public string? Address { get; set; }
    }
}