namespace schand.DTOS.Review
{
    public class ReviewCreateDTO
    {
        public int ProductId { get; set; }

        public int Rating { get; set; }
        public string ReviewContent { get; set; }
        public bool IsVerifiedPurchase { get; set; }
    }
}

