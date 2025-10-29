using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using schand.DTOS.Favourite;
using schand.IService;

namespace schand.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    [Authorize(Roles = "User")]
    public class FavoriteController : ControllerBase
    {
        private readonly IFavoriteService _favoriteService;

        public FavoriteController(IFavoriteService favoriteService)
        {
            _favoriteService = favoriteService;
        }

        [HttpGet]
        public async Task<IActionResult> GetFavorites()
        {
            var userIdClaim = User.FindFirst("UserId")?.Value;
            if (userIdClaim == null) return Unauthorized("Không tìm thấy thông tin người dùng.");
            int userId = int.Parse(userIdClaim);
            var result = await _favoriteService.GetFavoritesAsync(userId);
            return Ok(result);
        }

        [HttpPost]
        public async Task<IActionResult> AddFavorite([FromBody] FavoriteCreateDTO dto)
        {
            var userIdClaim = User.FindFirst("UserId")?.Value;
            if (userIdClaim == null) return Unauthorized("Không tìm thấy thông tin người dùng.");
            int userId = int.Parse(userIdClaim);
            await _favoriteService.AddFavoriteAsync(userId, dto.ProductId);
            return Ok();
        }

        [HttpDelete("{productId}")]
        public async Task<IActionResult> RemoveFavorite(int productId)
        {
            var userIdClaim = User.FindFirst("UserId")?.Value;
            if (userIdClaim == null) return Unauthorized("Không tìm thấy thông tin người dùng.");
            int userId = int.Parse(userIdClaim);
            await _favoriteService.RemoveFavoriteAsync(userId, productId);
            return NoContent();
        }
    }
}
