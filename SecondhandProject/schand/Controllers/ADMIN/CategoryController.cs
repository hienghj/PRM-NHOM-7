using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using schand.DTOS.Category;
using schand.IService;
using schand.Models;

namespace schand.Controllers.ADMIN
{
    [Authorize(Roles = "Admin")]

    [Route("api/[controller]")]
    [ApiController]
    public class CategoryController : ControllerBase
    {
        private readonly ICategoryService _service;

        public CategoryController(ICategoryService service)
        {
            _service = service;
        }

        [HttpGet]
        [AllowAnonymous]

        public async Task<IActionResult> GetAll()
        {
            var categories = await _service.GetAllAsync();
            var result = categories.Select(c => new CategoryDTO
            {
                Id = c.Id,
                Name = c.Name,
                ProductCount = c.Products?.Count ?? 0
            }).ToList();

            return Ok(result);
        }

        [HttpGet("{id}")]
        public async Task<IActionResult> GetById(int id)
        {
            var category = await _service.GetByIdAsync(id);
            if (category == null) return NotFound();

            var dto = new CategoryDTO
            {
                Id = category.Id,
                Name = category.Name,
                ProductCount = category.Products?.Count ?? 0
            };

            return Ok(dto);
        }

        [HttpPost]
        public async Task<IActionResult> Create([FromBody] CategoryCreateDTO dto)
        {
            var newCategory = await _service.CreateAsync(dto.Name);
            return CreatedAtAction(nameof(GetById), new { id = newCategory.Id }, new CategoryDTO
            {
                Id = newCategory.Id,
                Name = newCategory.Name,
                ProductCount = 0
            });
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> Update(int id, [FromBody] CategoryUpdateDTO dto)
        {
            if (id != dto.Id) return BadRequest("Id mismatch");
            var success = await _service.UpdateAsync(dto.Id, dto.Name);
            return success ? NoContent() : NotFound();
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var success = await _service.DeleteAsync(id);
            return success ? NoContent() : NotFound();
        }
    }

}

