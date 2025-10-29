namespace schand.Models
{
    public class GeminiResponse
    {
        public string query_type { get; set; }
        public string product_name { get; set; }
        public string category { get; set; }
        public int? max_price { get; set; }
        public int? min_price { get; set; }
        public string other_attributes { get; set; }
        public string response_text { get; set; }
    }


}
