using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using schand.IService;
using schand.Service;

namespace schand.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AiController : ControllerBase
    {
        private readonly GeminiService _gemini;

        public AiController(GeminiService gemini)
        {
            _gemini = gemini;
        }

        [HttpPost("ask")]
        public async Task<IActionResult> Ask([FromBody] string prompt)
        {
            var result = await _gemini.GenerateTextAsync(prompt);
            return Ok(result);
        }
        [HttpPost("ask2")]
        public async Task<IActionResult> Ask2([FromBody] string userQuestion, [FromServices] GeminiService gemini)
        {
            var (items, suggestions, message, parsed, rawJson) =
                await gemini.HandleQueryReturnProductsAsync(userQuestion);

            return Ok(new
            {
                query = parsed,          // JSON 4 trường sau khi chuẩn hoá (để debug/hiển thị)
                items,                   // danh sách “khớp” theo rule (ưu tiên title/description)
                suggestions,             // nếu không khớp, trả gợi ý
                message,                 // thông báo “không có” + lý do/gợi ý
                rawJson                  // chuỗi JSON gốc từ Gemini
            });
        }
        [HttpPost("ask3")]
        public async Task<IActionResult> Ask3([FromBody] string prompt)
        {
            var result = await _gemini.GenerateTextAsync3(prompt);
            return Ok(result);
        }
    }
}
