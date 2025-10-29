using Microsoft.AspNetCore.Authorization;
using System.Security.Claims;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using schand.DTOS.Comment;
using schand.IService;
using schand.Models;

namespace schand.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize]
    public class ProductCommentController : ControllerBase
    {
        private readonly IProductCommentService _commentService;

        public ProductCommentController(IProductCommentService commentService)
        {
            _commentService = commentService;
        }

        // GET api/productcomments/product/1
        // Lấy danh sách comment của một sản phẩm
        [HttpGet("product/{productId}")]
        [AllowAnonymous] // Cho phép truy cập mà không cần đăng nhập
        public async Task<ActionResult<IEnumerable<ProductCommentDTO>>> GetCommentsByProductId(int productId)
        {
            var comments = await _commentService.GetCommentsByProductIdAsync(productId);
            if (comments == null || !comments.Any())
            {
                return NotFound("Không tìm thấy bình luận nào cho sản phẩm này.");
            }

            // Ánh xạ từ Model sang DTO để trả về cho client
            var commentDTOs = comments.Select(c => new ProductCommentDTO
            {
                Id = c.Id,
                ProductId = c.ProductId,
                UserId = c.UserId,
                UserName = c.User?.FullName, // Lấy tên người dùng, nếu có
                Content = c.Content,
                ParentId = c.ParentId,
                CreatedAt = c.CreatedAt
            }).ToList();

            return Ok(commentDTOs);
        }

        // POST api/productcomments
        // Tạo một comment mới
        [HttpPost]
        public async Task<ActionResult<ProductCommentDTO>> CreateComment([FromBody] CreateProductCommentDTO createDto)
        {
            var userIdClaim = User.FindFirst("UserId")?.Value;
            if (userIdClaim == null) return Unauthorized("Không tìm thấy thông tin người dùng.");
            int userId = int.Parse(userIdClaim);

            // Ánh xạ từ DTO sang Model để truyền xuống Service
            var newComment = new ProductComment
            {
                ProductId = createDto.ProductId,
                UserId = userId,
                Content = createDto.Content,
                ParentId = createDto.ParentId,
                CreatedAt = DateTime.UtcNow,
                IsDeleted = false
            };

            await _commentService.AddCommentAsync(newComment);

            // Ánh xạ lại Model đã tạo sang DTO để trả về
            var newCommentDto = new ProductCommentDTO
            {
                Id = newComment.Id,
                ProductId = newComment.ProductId,
                UserId = newComment.UserId,
                UserName = User.Identity?.Name, // Hoặc lấy từ một nguồn khác nếu User không có tên
                Content = newComment.Content,
                ParentId = newComment.ParentId,
                CreatedAt = newComment.CreatedAt
            };

            return CreatedAtAction(nameof(GetCommentById), new { id = newComment.Id }, newCommentDto);
        }

        // GET api/productcomments/5
        // Lấy một comment cụ thể
        [HttpGet("{id}")]
        [AllowAnonymous]
        public async Task<ActionResult<ProductCommentDTO>> GetCommentById(int id)
        {
            var comment = await _commentService.GetCommentByIdAsync(id);
            if (comment == null)
            {
                return NotFound("Không tìm thấy bình luận.");
            }

            // Ánh xạ từ Model sang DTO
            var commentDto = new ProductCommentDTO
            {
                Id = comment.Id,
                ProductId = comment.ProductId,
                UserId = comment.UserId,
                UserName = comment.User?.FullName,
                Content = comment.Content,
                ParentId = comment.ParentId,
                CreatedAt = comment.CreatedAt
            };

            return Ok(commentDto);
        }

        // PUT api/productcomments/5
        // Cập nhật một comment
        [HttpPut("{id}")]
        public async Task<ActionResult> UpdateComment(int id, [FromBody] UpdateProductCommentDTO updateDto)
        {
            var userIdClaim = User.FindFirst("UserId")?.Value;
            if (userIdClaim == null) return Unauthorized("Không tìm thấy thông tin người dùng.");
            int userId = int.Parse(userIdClaim);

            // Tạo một model để truyền vào service, chỉ với những trường cần thiết
            var commentToUpdate = new ProductComment
            {
                Id = id,
                UserId = userId, // Rất quan trọng để kiểm tra quyền sở hữu
                Content = updateDto.Content
            };

            bool success = await _commentService.UpdateCommentAsync(commentToUpdate);
            if (!success)
            {
                return Forbid("Bạn không có quyền chỉnh sửa bình luận này hoặc bình luận không tồn tại.");
            }
            return NoContent();
        }

        // DELETE api/productcomments/5
        // Xóa một comment
        [HttpDelete("{id}")]
        public async Task<ActionResult> DeleteComment(int id)
        {
            var userIdClaim = User.FindFirst(ClaimTypes.NameIdentifier);
            if (userIdClaim == null)
            {
                return Unauthorized("Không tìm thấy thông tin người dùng.");
            }
            int userId = int.Parse(userIdClaim.Value);

            bool success = await _commentService.DeleteCommentAsync(id, userId);
            if (!success)
            {
                return Forbid("Bạn không có quyền xóa bình luận này hoặc bình luận không tồn tại.");
            }
            return NoContent();
        }
    }
}
