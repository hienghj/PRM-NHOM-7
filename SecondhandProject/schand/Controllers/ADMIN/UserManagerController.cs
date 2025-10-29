using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using schand.DTOS.ADMIN;
using schand.IService.ADMIN;

namespace schand.Controllers.ADMIN
{
    [Authorize(Roles = "Admin")]

    [Route("api/[controller]")]
    [ApiController]
    public class UserManagerController : ControllerBase
    {
        private readonly IUserManagerService _service;

        public UserManagerController(IUserManagerService service)
        {
            _service = service;
        }

        [HttpGet]
        public async Task<IActionResult> GetAll() => Ok(await _service.GetAllAsync());

        [HttpGet("{id}")]
        public async Task<IActionResult> GetById(int id)
        {
            var user = await _service.GetByIdAsync(id);
            return user == null ? NotFound() : Ok(user);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> Update(int id, [FromBody] UserUpdateDTO dto)
        {
            var success = await _service.UpdateAsync(id, dto);
            return success ? NoContent() : NotFound();
        }

        [HttpPatch("{id}/toggle-lock")]
        public async Task<IActionResult> ToggleLock(int id)
        {
            var success = await _service.ToggleLockAsync(id);
            return success ? Ok("Cập nhật trạng thái khóa thành công") : NotFound();
        }
    }
}
