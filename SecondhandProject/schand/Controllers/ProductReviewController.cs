using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using schand.DTOS.Review;
using schand.IService;
using schand.Models;

namespace schand.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize]

    public class ProductReviewController : ControllerBase
    {
        private readonly IProductReviewService _reviewService;

        public ProductReviewController(IProductReviewService reviewService)
        {
            _reviewService = reviewService;
        }

        [HttpGet("{productId}")]
        public async Task<ActionResult<IEnumerable<ProductReviewDto>>> GetReviewsByProductId(int productId)
        {
            var reviews = await _reviewService.GetReviewsByProductIdAsync(productId);

            if (reviews == null || !reviews.Any())
            {
                return NotFound();
            }

            // Tự ánh xạ từ Entity sang DTO
            var reviewDtos = reviews.Select(r => new ProductReviewDto
            {
                Id = r.Id,
                ProductId = r.ProductId,
                UserId = r.UserId,
                Rating = r.Rating,
                ReviewContent = r.ReviewContent,
                CreatedAt = r.CreatedAt,
                IsVerifiedPurchase = r.IsVerifiedPurchase
            }).ToList();

            return Ok(reviewDtos);
        }

        [HttpGet("details/{id}")]
        public async Task<ActionResult<ProductReviewDto>> GetReviewById(int id)
        {
            var review = await _reviewService.GetReviewByIdAsync(id);

            if (review == null)
            {
                return NotFound();
            }

            // Tự ánh xạ từ Entity sang DTO
            var reviewDto = new ProductReviewDto
            {
                Id = review.Id,
                ProductId = review.ProductId,
                UserId = review.UserId,
                Rating = review.Rating,
                ReviewContent = review.ReviewContent,
                CreatedAt = review.CreatedAt,
                IsVerifiedPurchase = review.IsVerifiedPurchase
            };

            return Ok(reviewDto);
        }

        [HttpPost]
        public async Task<ActionResult<ProductReviewDto>> AddReview(ReviewCreateDTO createreviewdto)
        {
            var userIdClaim = User.FindFirst("UserId")?.Value;
            if (userIdClaim == null) return Unauthorized("Không tìm thấy thông tin người dùng.");
            int userId = int.Parse(userIdClaim);
            // Tự ánh xạ từ DTO sang Entity
            var review = new ProductReview
            {
                ProductId = createreviewdto.ProductId,
                 UserId= userId,
                Rating = createreviewdto.Rating,
                ReviewContent = createreviewdto.ReviewContent,
                CreatedAt = DateTime.UtcNow, // Nên gán giá trị ở đây
                IsVerifiedPurchase = createreviewdto.IsVerifiedPurchase
            };

            await _reviewService.AddReviewAsync(review);

            // Cập nhật lại Id từ entity sau khi đã lưu
         

            return CreatedAtAction(nameof(GetReviewById), new { id = review.Id }, review);
        }

        [HttpPut("{id}")]
        public async Task<ActionResult> UpdateReview(int id, ReviewUpdateDTO updateDto)
        {
            // Bước 1: Lấy thông tin người dùng từ token (Claim)
            var userIdClaim = User.FindFirst("UserId")?.Value;
            if (string.IsNullOrEmpty(userIdClaim))
            {
                // Trả về 401 Unauthorized nếu không tìm thấy thông tin người dùng
                return Unauthorized("Không tìm thấy thông tin người dùng. Vui lòng đăng nhập.");
            }
            int userId = int.Parse(userIdClaim);

            // Bước 2: Lấy bài đánh giá hiện có từ database
            var existingReview = await _reviewService.GetReviewByIdAsync(id);
            if (existingReview == null)
            {
                // Trả về 404 Not Found nếu không tìm thấy bài đánh giá
                return NotFound("Không tìm thấy bài đánh giá để cập nhật.");
            }

            // Bước 3: Kiểm tra quyền sở hữu
            if (existingReview.UserId != userId)
            {
                // Trả về 403 Forbidden nếu người dùng không phải là chủ sở hữu
                return Forbid("Bạn không có quyền chỉnh sửa bài đánh giá này.");
            }

            // Bước 4: Cập nhật các trường được phép từ DTO
            existingReview.Rating = updateDto.Rating;
            existingReview.ReviewContent = updateDto.ReviewContent;

            // Bước 5: Lưu thay đổi vào cơ sở dữ liệu
            try
            {
                await _reviewService.UpdateReviewAsync(existingReview);
            }
            catch (DbUpdateConcurrencyException)
            {
                // Xử lý lỗi trùng lặp khi cập nhật
                if (await _reviewService.GetReviewByIdAsync(id) == null)
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            // Trả về 204 No Content khi cập nhật thành công
            return NoContent();
        }

        [HttpDelete("{id}")]
        public async Task<ActionResult> SoftDeleteReview(int id)
        {
            var review = await _reviewService.GetReviewByIdAsync(id);
            if (review == null)
            {
                return NotFound();
            }

            await _reviewService.SoftDeleteReviewAsync(id);
            return NoContent();
        }
    }
}
