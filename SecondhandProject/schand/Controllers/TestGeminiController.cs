using Microsoft.AspNetCore.Mvc;
using schand.Models;
using System.Text;
using System.Text.Json;

namespace schand.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class TestGeminiController : ControllerBase
    {
        private readonly string _apiKey = "AIzaSyA0C0cnoBvcP7bfuAV3wQErta6hXeJB-oE"; // Thay bằng key thật

        [HttpPost("vision")]
        [Consumes("multipart/form-data")]
        public async Task<IActionResult> Vision([FromForm] ImageUploadRequest request)
        {
            if (request.Image == null || request.Image.Length == 0)
                return BadRequest("Vui lòng upload 1 ảnh.");

            // Chuyển ảnh sang base64
            byte[] imageBytes;
            using (var ms = new MemoryStream())
            {
                await request.Image.CopyToAsync(ms);
                imageBytes = ms.ToArray();
            }

            string base64Image = Convert.ToBase64String(imageBytes);

            // Tạo request body cho Gemini API
            var body = new
            {
                contents = new[]
                {
                    new
                    {
                        parts = new object[]
                        {
                            new { text = request.Prompt },
                            new
                            {
                                inline_data = new
                                {
                                    mime_type = request.Image.ContentType,
                                    data = base64Image
                                }
                            }
                        }
                    }
                }
            };

            var url = $"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key={_apiKey}";

            using var httpClient = new HttpClient();
            var json = JsonSerializer.Serialize(body);
            var content = new StringContent(json, Encoding.UTF8, "application/json");

            var response = await httpClient.PostAsync(url, content);
            var responseBody = await response.Content.ReadAsStringAsync();

            if (!response.IsSuccessStatusCode)
                return StatusCode((int)response.StatusCode, responseBody);

            return Ok(responseBody);
        }
    }
}
