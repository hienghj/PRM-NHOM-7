using System.Text.Json.Serialization;

namespace schand.DTOS.Category
{
    public class CategoryUpdateDTO
    {
        // ⛔ Không cho serialize/deserizlize khi binding JSON
        [JsonIgnore] // ⛔ Không cho serialize/deserizlize khi binding JSON

        public int Id { get; set; }
        public string Name { get; set; } = null!;
    }
}
