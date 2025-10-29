using System.Linq;
using System.Net.Http;
using System.Text;
using System.Text.Json;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using schand.Models; // Đảm bảo bạn có đúng namespace chứa Product

namespace schand.Service
{
    public class GeminiService
    {
        private readonly HttpClient _httpClient;
        private readonly string _apiKey = "AIzaSyA0C0cnoBvcP7bfuAV3wQErta6hXeJB-oE"; // ← thay bằng khóa thật
        private readonly SchuserContext _context;
        private readonly string _apiKey2 = "AIzaSyDEJJ3gmu8GMe-uT3M8TjqYdjC7esu4wTw"; // ← thay bằng khóa thật

        private static readonly JsonSerializerOptions _jsonOptions = new()
        {
            PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
            WriteIndented = false
        };
        public GeminiService(SchuserContext context)
        {
            _httpClient = new HttpClient();
            _context = context;

        }

        public async Task<string> GenerateTextAsync(string prompt)
        {
            var url = $"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key={_apiKey}";

            var body = new
            {
                contents = new[]
                {
                    new
                    {
                        parts = new[]
                        {
                            new { text = prompt }
                        }
                    }
                }
            };

            var jsonContent = new StringContent(JsonSerializer.Serialize(body), Encoding.UTF8, "application/json");
            var response = await _httpClient.PostAsync(url, jsonContent);

            if (!response.IsSuccessStatusCode)
                return $"❌ API lỗi: {response.StatusCode}";

            var jsonResponse = await response.Content.ReadAsStringAsync();
            return jsonResponse;
        }

        public async Task<string> CompareProductsAsync(Product p1, Product p2)
        {
            var prompt = $@"
So sánh hai sản phẩm dưới đây và đưa ra đánh giá chi tiết:

Sản phẩm A:
- Tên: {p1.Title}
- Giá: {p1.Price} VNĐ
- Tình trạng: {p1.Condition}
- Thông số: {p1.Descriptions}

Sản phẩm B:
- Tên: {p2.Title}
- Giá: {p2.Price} VNĐ
- Tình trạng: {p2.Condition}
- Thông số: {p2.Descriptions}

So sánh chi tiết về cấu hình, giá cả, tình trạng, và đề xuất sản phẩm phù hợp cho từng nhu cầu.";

            return await GenerateTextAsync(prompt);
        }




        public async Task<string> GenerateTextAsync3(string prompt)
        {
            var url = $"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key={_apiKey2}";

            var body = new
            {
                contents = new[]
                {
                    new
                    {
                        parts = new[]
                        {
                            new { text = prompt }
                        }
                    }
                }
            };

            var jsonContent = new StringContent(JsonSerializer.Serialize(body), Encoding.UTF8, "application/json");
            var response = await _httpClient.PostAsync(url, jsonContent);

            if (!response.IsSuccessStatusCode)
                return $"❌ API lỗi: {response.StatusCode}";

            var jsonResponse = await response.Content.ReadAsStringAsync();
            return jsonResponse;
        }







        // ==== MỚI: ép Gemini trả JSON 4 trường ====
        private static string BuildExtractionPrompt(string userUtterance) => $@"
Bạn là trợ lý chuẩn hoá truy vấn sản phẩm. CHỈ trả về JSON hợp lệ (không thêm chữ) theo schema:

{{
  ""title"": ""string|null"",
  ""description"": ""string|null"",
  ""condition"": ""string|null"",
  ""minPrice"": number|null,
  ""maxPrice"": number|null
}}

Quy tắc:
- Tên/mẫu hàng → ""title""
- Đặc tả/đặc điểm → ""description""
- Tình trạng (mới/cũ/99%/refurbished/...) → ""condition""
- Giá (VND): ""dưới X"" → maxPrice=X; ""trên X"" → minPrice=X; ""A-B"" → minPrice=A, maxPrice=B
- Không có thông tin thì để null.
Đầu vào: ""{userUtterance}""";

        public async Task<string> GenerateMinimalSearchJsonAsync(string userUtterance, CancellationToken ct = default)
        {
            var url = $"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key={_apiKey}";
            var body = new
            {
                contents = new[] { new { parts = new[] { new { text = BuildExtractionPrompt(userUtterance) } } } },
                generationConfig = new { responseMimeType = "application/json" }
            };

            using var req = new HttpRequestMessage(HttpMethod.Post, url)
            { Content = new StringContent(JsonSerializer.Serialize(body), Encoding.UTF8, "application/json") };

            using var res = await _httpClient.SendAsync(req, ct);
            if (!res.IsSuccessStatusCode) return $"{{\"error\":\"API {res.StatusCode}\"}}";

            var raw = await res.Content.ReadAsStringAsync(ct);
            try
            {
                using var doc = JsonDocument.Parse(raw);
                var text = doc.RootElement.GetProperty("candidates")[0]
                                          .GetProperty("content").GetProperty("parts")[0]
                                          .GetProperty("text").GetString();
                var jsonOnly = TryExtractFirstJsonObject(text);
                using var _ = JsonDocument.Parse(jsonOnly); // validate
                return jsonOnly;
            }
            catch { return "{\"error\":\"Parse model response failed\"}"; }
        }

        private static string TryExtractFirstJsonObject(string input)
        {
            if (string.IsNullOrWhiteSpace(input)) return null;
            int start = input.IndexOf('{'); if (start < 0) return null;
            int depth = 0;
            for (int i = start; i < input.Length; i++)
            {
                if (input[i] == '{') depth++;
                else if (input[i] == '}') { depth--; if (depth == 0) return input[start..(i + 1)].Trim(); }
            }
            return null;
        }


        // ==== MỚI: tìm “kết quả khớp” theo ưu tiên title/description ====
        private IQueryable<Product> ApplyPrimaryMatch(IQueryable<Product> src, ProductSearchRequest q)
        {
            var hasTitle = !string.IsNullOrWhiteSpace(q.Title);
            var hasDesc = !string.IsNullOrWhiteSpace(q.Description);

            if (hasTitle && hasDesc)
            {
                var t = q.Title.Trim();
                var d = q.Description.Trim();
                // Cả title và description đều phải khớp (AND)
                src = src.Where(p =>
                    (p.Title != null && EF.Functions.Like(p.Title, $"%{t}%")) &&
                    (
                        (p.Descriptions != null && EF.Functions.Like(p.Descriptions, $"%{d}%")) ||
                        (p.Title != null && EF.Functions.Like(p.Title, $"%{d}%"))
                    )
                );
            }
            else if (hasTitle)
            {
                var t = q.Title.Trim();
                src = src.Where(p => p.Title != null && EF.Functions.Like(p.Title, $"%{t}%"));
            }
            else if (hasDesc)
            {
                var d = q.Description.Trim();
                src = src.Where(p =>
                    (p.Descriptions != null && EF.Functions.Like(p.Descriptions, $"%{d}%")) ||
                    (p.Title != null && EF.Functions.Like(p.Title, $"%{d}%"))
                );
            }
            else
            {
                // Không có title/description → không coi là “khớp chính”
                src = src.Where(p => false);
            }

            return src;
        }

        // ==== MỚI: áp bộ lọc thứ yếu (condition/price) ====
        private IQueryable<Product> ApplySecondaryFilters(IQueryable<Product> src, ProductSearchRequest q)
        {
            if (!string.IsNullOrWhiteSpace(q.Condition))
            {
                var c = q.Condition.Trim().ToLower();
                src = src.Where(p => p.Condition != null && p.Condition.ToLower().Contains(c));
            }
            if (q.MinPrice.HasValue) src = src.Where(p => p.Price >= q.MinPrice.Value);
            if (q.MaxPrice.HasValue) src = src.Where(p => p.Price <= q.MaxPrice.Value);

            // có thể giữ tin hợp lệ
            src = src.Where(p => p.IsApproved == true && (p.IsActive == true || p.IsActive == null)
                                && (!p.ExpireAt.HasValue || p.ExpireAt > DateTime.UtcNow));

            return src;
        }

        // ==== MỚI: gợi ý tương tự khi không có kết quả khớp ====
        private IQueryable<Product> BuildSuggestionQuery(ProductSearchRequest q)
        {
            var products = _context.Products.AsQueryable();

            // Ưu tiên nới lỏng theo “gốc từ khoá”
            if (!string.IsNullOrWhiteSpace(q.Title))
            {
                // Ví dụ "iphone 20" => tìm theo “iphone”
                var tokens = Tokenize(q.Title);
                var roots = tokens.Where(t => !Regex.IsMatch(t, @"^\d+$")).ToList();
                if (roots.Count == 0) roots = tokens; // fallback

                foreach (var r in roots)
                    products = products.Where(p => p.Title != null && EF.Functions.Like(p.Title, $"%{r}%"));
            }
            else if (!string.IsNullOrWhiteSpace(q.Description))
            {
                // Ví dụ "rtx3060" => tìm các biến thể: "rtx", "3060", "rtx 3060"
                var vars = BuildGpuLikeVariants(q.Description);
                products = products.Where(p =>
                    (p.Title != null && vars.Any(v => EF.Functions.Like(p.Title, $"%{v}%"))) ||
                    (p.Descriptions != null && vars.Any(v => EF.Functions.Like(p.Descriptions, $"%{v}%")))
                );
            }
            else
            {
                // Không có gì để gợi ý
                products = products.Where(p => false);
            }

            // Có thể giữ bộ lọc giá/condition nhẹ nếu muốn, nhưng thường bỏ qua để tăng số gợi ý
            products = products.Where(p => p.IsApproved == true && (p.IsActive == true || p.IsActive == null)
                                && (!p.ExpireAt.HasValue || p.ExpireAt > DateTime.UtcNow));

            return products;
        }

        private static List<string> Tokenize(string s) =>
            Regex.Split(s.ToLower().Trim(), @"\s+").Where(x => !string.IsNullOrWhiteSpace(x)).ToList();

        private static List<string> BuildGpuLikeVariants(string s)
        {
            var raw = s.ToLower().Trim();
            var variants = new HashSet<string> { raw };
            variants.Add(raw.Replace(" ", ""));
            // chèn khoảng giữa chữ & số: "rtx3060" -> "rtx 3060"
            variants.Add(Regex.Replace(raw, @"([a-zA-Z]+)(\d+)", "$1 $2"));
            // tách số riêng: "3060"
            var digits = Regex.Match(raw, @"\d+").Value;
            if (!string.IsNullOrEmpty(digits)) variants.Add(digits);
            // chữ riêng: "rtx"
            var letters = Regex.Match(raw, @"[a-zA-Z]+").Value;
            if (!string.IsNullOrEmpty(letters)) variants.Add(letters);
            return variants.ToList();
        }

        // ==== MỚI: API one-stop — trả danh sách sản phẩm + gợi ý khi cần ====
        public async Task<(IList<Product> items, IList<Product> suggestions, string message, ProductSearchRequest parsed, string rawJson)>
            HandleQueryReturnProductsAsync(string userMessage, CancellationToken ct = default)
        {
            // 1) Chuẩn hoá JSON
            var rawJson = await GenerateMinimalSearchJsonAsync(userMessage, ct);

            ProductSearchRequest q;
            try { q = JsonSerializer.Deserialize<ProductSearchRequest>(rawJson, _jsonOptions); }
            catch { return (new List<Product>(), new List<Product>(), "Không hiểu yêu cầu. Vui lòng mô tả rõ hơn.", null, rawJson); }

            // 2) Tìm “khớp chính” (title/description là bắt buộc theo luật đã nêu)
            var baseQuery = _context.Products.AsQueryable();
            var primary = ApplyPrimaryMatch(baseQuery, q);
            primary = ApplySecondaryFilters(primary, q);

            var items = await primary
                .OrderByDescending(p => p.BoostedUntil ?? p.CreatedAt ?? DateTime.MinValue)
                .ThenByDescending(p => p.Id)
                .Take(50).ToListAsync(ct);

            if (items.Count > 0)
                return (items, new List<Product>(), null, q, rawJson);

            // 3) Không có kết quả khớp → gợi ý tương tự
            var suggestQuery = BuildSuggestionQuery(q);
            var suggestions = await suggestQuery
                .OrderByDescending(p => p.BoostedUntil ?? p.CreatedAt ?? DateTime.MinValue)
                .ThenByDescending(p => p.Id)
                .Take(20).ToListAsync(ct);

            string message;
            if (!string.IsNullOrWhiteSpace(q.Title))
                message = "Không tìm thấy sản phẩm đúng yêu cầu theo tên. Dưới đây là các gợi ý tương tự theo title.";
            else if (!string.IsNullOrWhiteSpace(q.Description))
                message = "Không tìm thấy sản phẩm đúng yêu cầu theo mô tả. Dưới đây là các gợi ý tương tự theo description.";
            else
                message = "Không tìm thấy sản phẩm phù hợp.";

            return (new List<Product>(), suggestions, message, q, rawJson);
        }
    }
}
