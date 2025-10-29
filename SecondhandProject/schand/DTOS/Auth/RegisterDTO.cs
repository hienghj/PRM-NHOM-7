namespace schand.DTOS.Auth
{
    public class RegisterDTO
    {
        public string Email { get; set; }
        public string Password { get; set; }
        public string FullName { get; set; }
        public string? PhoneNumber { get; set; }
        public string? Gender { get; set; }
        public DateOnly? DateOfBirth { get; set; }
        public string? Address { get; set; }
    }
}
