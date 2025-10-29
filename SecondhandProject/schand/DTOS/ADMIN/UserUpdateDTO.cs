using System.Text.Json.Serialization;

namespace schand.DTOS.ADMIN
{
    public class UserUpdateDTO
    {
        [JsonPropertyName("fullName")]

        public string FullName { get; set; } = null!;
        [JsonPropertyName("role")]

        public string Role { get; set; } = null!;
      
    }
}
