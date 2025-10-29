using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using schand.IService;

namespace schand.Controllers.ADMIN
{
    [Authorize(Roles = "Admin")]
    [Route("api/Admin/[controller]")]
    [ApiController]
    public class ReportManagementController : ControllerBase
    {
        private readonly IReportService _reportService;

        public ReportManagementController(IReportService reportService)
        {
            _reportService = reportService;
        }

        [HttpGet]
        public async Task<IActionResult> GetAll()
        {
            var reports = await _reportService.GetAllAsync();
            return Ok(reports);
        }

        [HttpGet("{id}")]
        public async Task<IActionResult> GetDetail(int id)
        {
            var report = await _reportService.GetByIdAsync(id);
            return report == null ? NotFound() : Ok(report);
        }

        [HttpPut("{id}/resolve")]
        public async Task<IActionResult> Resolve(int id)
        {
            var success = await _reportService.MarkResolvedAsync(id);
            return success ? Ok() : NotFound();
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var success = await _reportService.DeleteAsync(id);
            return success ? NoContent() : NotFound();
        }
    }

}
