using schand.IRepository;
using schand.IService;
using schand.Models;

namespace schand.Service
{
    // /Services/Implementations/FavoriteService.cs
    public class FavoriteService : IFavoriteService
    {
        private readonly IFavouriteRepository _favoriteRepo;

        public FavoriteService(IFavouriteRepository favoriteRepo)
        {
            _favoriteRepo = favoriteRepo;
        }

        public async Task<IEnumerable<Product>> GetFavoritesAsync(int userId)
        {
            return await _favoriteRepo.GetFavoritesByUserIdAsync(userId);
        }

        public async Task AddFavoriteAsync(int userId, int productId)
        {
            await _favoriteRepo.AddFavoriteAsync(userId, productId);
        }

        public async Task RemoveFavoriteAsync(int userId, int productId)
        {
            await _favoriteRepo.RemoveFavoriteAsync(userId, productId);
        }
    }

}
