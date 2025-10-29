namespace schand.DTOS
{
    public class ProductCreateDTO
    {
        public string Title { get; set; } = null!;
        public string? Descriptions { get; set; }
        public decimal Price { get; set; }
        public string? Condition { get; set; }
        public string? Locations { get; set; }
        public int? CategoryId { get; set; }
    }
}
