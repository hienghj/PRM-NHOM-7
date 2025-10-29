namespace schand.DTOS.Review
{
    public class ProductReviewDto
    {
        public int Id { get; set; }
        public int ProductId { get; set; }
        public int UserId { get; set; }
        public int? Rating { get; set; }
        public string? ReviewContent { get; set; }
        public DateTime CreatedAt { get; set; }
        public bool? IsVerifiedPurchase { get; set; }
    }
}
