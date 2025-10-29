namespace schand.DTOS.Comment
{
    public class CreateProductCommentDTO
    {
        public int ProductId { get; set; }
        public string Content { get; set; }
        public int? ParentId { get; set; } // Có thể null nếu là comment gốc
    }
}
