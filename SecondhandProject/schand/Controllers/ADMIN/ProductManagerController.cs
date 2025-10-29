using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using schand.DTOS.ADMIN.ProductManager;
using schand.DTOS.Category;
using schand.IService;

[Authorize(Roles = "Admin")]
[ApiController]
[Route("api/admin/products")]
public class ProductManagerController : ControllerBase
{
    private readonly IProductService _productService;

    public ProductManagerController(IProductService productService)
    {
        _productService = productService;
    }
    [HttpGet]
    public async Task<IActionResult> GetAll()
    {
        var products = await _productService.GetAllForAdminAsync();
        var result = products.Select(p => new ProductAdminDTO
        {
            Id = p.Id,
            Title = p.Title,
            Descriptions = p.Descriptions,
            Price = p.Price,
            Condition = p.Condition,
            Locations = p.Locations,
            CategoryName = p.Category?.Name,
            IsActive = p.IsActive,
            IsApproved = p.IsApproved,
            ApprovedAt = p.ApprovedAt,
            RejectedReason = p.RejectedReason,
            CreatedAt = p.CreatedAt,
            ImageUrls = p.ProductImages?.Select(img => img.ImageUrl).ToList() ?? new List<string>()
        }).ToList();

        return Ok(result);
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetById(int id)
    {
        var p = await _productService.GetByIdForAdminAsync(id);
        if (p == null) return NotFound();

        var result = new ProductAdminDTO
        {
            Id = p.Id,
            Title = p.Title,
            Descriptions = p.Descriptions,
            Price = p.Price,
            Condition = p.Condition,
            Locations = p.Locations,
            CategoryName = p.Category?.Name,
            IsActive = p.IsActive,
            IsApproved = p.IsApproved,
            ApprovedAt = p.ApprovedAt,
            RejectedReason = p.RejectedReason,
            CreatedAt = p.CreatedAt,
            ImageUrls = p.ProductImages?.Select(img => img.ImageUrl).ToList() ?? new List<string>()
        };

        return Ok(result);
    }


    [HttpPut("{id}/approve")]
    public async Task<IActionResult> Approve(int id)
    {
        var success = await _productService.ApproveProductAsync(id);
        if (!success) return NotFound("Không tìm thấy sản phẩm.");
        return Ok("Đã duyệt sản phẩm.");
    }

    [HttpPut("{id}/reject")]
    public async Task<IActionResult> Reject(int id, [FromBody] ProductApprovalDTO dto)
    {
        if (string.IsNullOrWhiteSpace(dto.RejectedReason))
            return BadRequest("Cần cung cấp lý do từ chối.");

        var success = await _productService.RejectProductAsync(id, dto.RejectedReason);
        if (!success) return NotFound("Không tìm thấy sản phẩm.");
        return Ok("Đã từ chối sản phẩm.");
    }
}

