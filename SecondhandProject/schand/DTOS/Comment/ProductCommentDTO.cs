namespace schand.DTOS.Comment
{
    public class ProductCommentDTO
    {
        public int Id { get; set; }
        public int ProductId { get; set; }
        public int UserId { get; set; }
        public string UserName { get; set; } // Giả sử bạn muốn hiển thị tên người dùng
        public string Content { get; set; }
        public int? ParentId { get; set; }
        public DateTime CreatedAt { get; set; }
    }
}
