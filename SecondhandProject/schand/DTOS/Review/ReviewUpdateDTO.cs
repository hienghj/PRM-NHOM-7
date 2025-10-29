using System.Text.Json.Serialization;

namespace schand.DTOS.Review
{
    public class ReviewUpdateDTO
    {
        public int Rating { get; set; }
        public string ReviewContent { get; set; }
    }
}
