using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using schand.DTOS.Report;
using schand.IService;

namespace schand.Controllers
{
    [Authorize(Roles = "User")]
    [Route("api/[controller]")]
    [ApiController]
    public class ReportController : ControllerBase
    {
        private readonly IReportService _reportService;

        public ReportController(IReportService reportService)
        {
            _reportService = reportService;
        }

        [HttpPost]
        public async Task<IActionResult> Create([FromBody] ReportCreateDTO dto)
        {
            var userIdClaim = User.FindFirst("UserId")?.Value;
            if (userIdClaim == null) return Unauthorized("Không tìm thấy thông tin người dùng.");
            int userId = int.Parse(userIdClaim);
            await _reportService.CreateAsync(userId, dto);
            return Ok();
        }

        [HttpGet("mine")]
        public async Task<IActionResult> GetMine()
        {
            var userIdClaim = User.FindFirst("UserId")?.Value;
            if (userIdClaim == null) return Unauthorized("Không tìm thấy thông tin người dùng.");
            int userId = int.Parse(userIdClaim);
            var reports = await _reportService.GetByUserAsync(userId);
            return Ok(reports);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Revoke(int id)
        {
            var userIdClaim = User.FindFirst("UserId")?.Value;
            if (userIdClaim == null) return Unauthorized("Không tìm thấy thông tin người dùng.");
            int userId = int.Parse(userIdClaim);
            var success = await _reportService.RevokeAsync(userId, id);
            return success ? NoContent() : Forbid();
        }
    }

}
