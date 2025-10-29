using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using schand.Models;
using System.Text;
using System.Text.Json;

namespace schand.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ProductAnalyzerController : ControllerBase
    {
        private readonly SchuserContext _context;
        private readonly string _apiKey = "AIzaSyA0C0cnoBvcP7bfuAV3wQErta6hXeJB-oE"; // Thay bằng API key thật


        public ProductAnalyzerController(SchuserContext context)
        {
            _context = context;
        }

        [HttpPost("analyze")]
        public async Task<IActionResult> AnalyzeProduct([FromBody] ProductAnalyzeRequest request)
        {
            var product = await _context.Products
                .Include(p => p.ProductImages)
                .FirstOrDefaultAsync(p => p.Id == request.ProductId);

            if (product == null)
                return NotFound("Sản phẩm không tồn tại.");

            if (product.ProductImages == null || !product.ProductImages.Any())
                return BadRequest("Sản phẩm này chưa có ảnh.");

            var imageUrls = product.ProductImages.Select(i => i.ImageUrl).ToList();

            // Prompt cố định
            var prompt = "Hãy phân tích chi tiết tình trạng sản phẩm thông qua hình ảnh. Mô tả độ mới, trầy xước, hư hỏng nếu có.";

            var geminiResponse = await CallGeminiAsync(imageUrls, prompt);

            return Ok(geminiResponse);
        }


        private async Task<string> CallGeminiAsync(List<string> imageUrls, string prompt)
        {
            var url = $"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key={_apiKey}";

            var parts = new List<object> { new { text = prompt } };

            using var httpClient = new HttpClient();

            foreach (var imageUrl in imageUrls)
            {
                var imageBytes = await httpClient.GetByteArrayAsync(imageUrl);

                parts.Add(new
                {
                    inline_data = new
                    {
                        mime_type = "image/jpeg", // Hoặc tự detect nếu muốn
                        data = Convert.ToBase64String(imageBytes)
                    }
                });
            }

            var body = new
            {
                contents = new[]
                {
                    new
                    {
                        parts = parts.ToArray()
                    }
                }
            };

            var json = JsonSerializer.Serialize(body);
            var content = new StringContent(json, Encoding.UTF8, "application/json");

            var response = await httpClient.PostAsync(url, content);
            var responseBody = await response.Content.ReadAsStringAsync();

            return responseBody;
        }
  
    }
}
