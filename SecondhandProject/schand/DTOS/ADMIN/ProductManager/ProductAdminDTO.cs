namespace schand.DTOS.ADMIN.ProductManager
{
    public class ProductAdminDTO
    {
        public int Id { get; set; }
        public string Title { get; set; } = null!;
        public string? Descriptions { get; set; }
        public decimal Price { get; set; }
        public string? Condition { get; set; }
        public string? Locations { get; set; }
        public string? CategoryName { get; set; }
        public bool? IsActive { get; set; }
        public bool IsApproved { get; set; }
        public DateTime? ApprovedAt { get; set; }
        public string? RejectedReason { get; set; }
        public DateTime? CreatedAt { get; set; }

        // ✅ Thêm danh sách ảnh vào đây
        public List<string> ImageUrls { get; set; } = new();
    }
}
