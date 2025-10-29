namespace schand.DTOS
{
    public class ProductDTO
    {
        public int Id { get; set; }
        public string Title { get; set; } = null!;
        public string? Descriptions { get; set; }
        public decimal Price { get; set; }
        public string? Condition { get; set; }
        public string? Locations { get; set; }
        public string? CategoryName { get; set; }
        public bool? IsActive { get; set; }
        public DateTime? CreatedAt { get; set; }

        public List<string>? ImageUrls { get; set; }  // Thêm danh sách ảnh
        public string? SellerName { get; set; }  // 👈 thêm tên người bán

    }

}
