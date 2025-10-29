using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using schand.DTOS;
using schand.IService;
using schand.Models;
using schand.Service;

namespace schand.Controllers
{
    [Authorize(Roles = "User")]
    [Route("api/[controller]")]
   [ApiController]
     public class ProductController : ControllerBase
    {
        private readonly IProductService _service;
        private readonly GeminiService _gemini;

        public ProductController(IProductService service, GeminiService gemini)
        {
            _service = service;
            _gemini = gemini;
        }


        

        [HttpGet]
        [AllowAnonymous]
        public async Task<IActionResult> GetAll()
        {
            var products = await _service.GetAllAsync();
            var result = products.Select(p => new ProductDTO
            {
                Id = p.Id,
                Title = p.Title,
                Descriptions = p.Descriptions,
                Price = p.Price,
                Condition = p.Condition,
                Locations = p.Locations,
                CreatedAt = p.CreatedAt,
                IsActive = p.IsActive,
                CategoryName = p.Category?.Name,
                ImageUrls = p.ProductImages?.Select(img => img.ImageUrl).ToList() ?? new List<string>()

            }).ToList();

            return Ok(result);
        }

        [HttpGet("{id}")]
        [AllowAnonymous]
        public async Task<IActionResult> GetById(int id)
        {
            var p = await _service.GetByIdAsync(id);
            if (p == null) return NotFound();

            return Ok(new ProductDTO
            {
                Id = p.Id,
                Title = p.Title,
                Descriptions = p.Descriptions,
                Price = p.Price,
                Condition = p.Condition,
                Locations = p.Locations,
                CreatedAt = p.CreatedAt,
                IsActive = p.IsActive,
                CategoryName = p.Category?.Name,
                ImageUrls = p.ProductImages?.Select(img => img.ImageUrl).ToList() ?? new List<string>(),
                SellerName = p.Seller?.FullName                 // 👈 thêm dòng này


            });
        }

        [HttpPost]
        public async Task<IActionResult> Create([FromBody] ProductCreateDTO dto)
        {
            var userIdClaim = User.FindFirst("UserId")?.Value;
            if (userIdClaim == null) return Unauthorized("Không tìm thấy thông tin người dùng.");
            int userId = int.Parse(userIdClaim);
            var product = new Product
            {
                Title = dto.Title,
                Descriptions = dto.Descriptions,
                Price = dto.Price,
                Condition = dto.Condition,
                Locations = dto.Locations,
                CategoryId = dto.CategoryId,
                SellerId = userId // 
            };

            var created = await _service.CreateAsync(product);
            return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> Update(int id, [FromBody] ProductUpdateDTO dto)
        {
            var userIdClaim = User.FindFirst("UserId")?.Value;
            if (userIdClaim == null) return Unauthorized("Không tìm thấy thông tin người dùng.");
            int userId = int.Parse(userIdClaim);

            dto.Id = id;
            var sellerId = await _service.GetSellerIdAsync(id); // gọi qua service
            if (sellerId == null) return NotFound();
            if (sellerId != userId) return Forbid();
            var product = new Product
            {
                Id = dto.Id,
                Title = dto.Title,
                Descriptions = dto.Descriptions,
                Price = dto.Price,
                Condition = dto.Condition,
                Locations = dto.Locations,
                CategoryId = dto.CategoryId
            };

            await _service.UpdateAsync(product);
            return NoContent();
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> SoftDelete(int id)
        {
            var userIdClaim = User.FindFirst("UserId")?.Value;
            if (userIdClaim == null) return Unauthorized("Không tìm thấy thông tin người dùng.");
            int userId = int.Parse(userIdClaim);
            var sellerId = await _service.GetSellerIdAsync(id); // gọi qua service
            if (sellerId == null) return NotFound();
            if (sellerId != userId) return Forbid();
            await _service.SoftDeleteAsync(id);
            return NoContent();
        }
        [HttpGet("compare/{id1}/{id2}")]
        [AllowAnonymous]

        public async Task<IActionResult> CompareProducts(int id1, int id2)
        {
            var p1 = await _service.GetByIdAsync(id1);
            var p2 = await _service.GetByIdAsync(id2);

            if (p1 == null || p2 == null) return NotFound("Một trong hai sản phẩm không tồn tại.");

            var result = await _gemini.CompareProductsAsync(p1, p2);

            return Ok(new { result });
        }
        [HttpGet("category/{categoryId}")]
        [AllowAnonymous]
        public async Task<IActionResult> GetByCategory(int categoryId)
        {
            var products = await _service.GetByCategoryAsync(categoryId);
            if (products == null || !products.Any())
                return NotFound("Không có sản phẩm nào trong danh mục này.");

            var result = products.Select(p => new ProductDTO
            {
                Id = p.Id,
                Title = p.Title,
                Descriptions = p.Descriptions,
                Price = p.Price,
                Condition = p.Condition,
                Locations = p.Locations,
                CreatedAt = p.CreatedAt,
                IsActive = p.IsActive,
                CategoryName = p.Category?.Name,
                ImageUrls = p.ProductImages?.Select(img => img.ImageUrl).ToList() ?? new List<string>()
            }).ToList();

            return Ok(result);
        }


    }


}
