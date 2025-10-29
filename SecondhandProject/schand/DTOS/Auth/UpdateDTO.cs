namespace schand.DTOS.Auth
{
    public class UpdateProfileDto
    {
        public string? FullName { get; set; }
    
        public string? Address { get; set; }
        public DateOnly? DateOfBirth { get; set; }   // dùng DateOnly? thay vì DateTime?
        public string? Gender { get; set; }
    }
}
