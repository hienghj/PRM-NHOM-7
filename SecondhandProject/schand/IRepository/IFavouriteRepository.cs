using schand.Models;

namespace schand.IRepository
{
    public interface IFavouriteRepository
    {
        Task<IEnumerable<Product>> GetFavoritesByUserIdAsync(int userId);
        Task AddFavoriteAsync(int userId, int productId);
        Task RemoveFavoriteAsync(int userId, int productId);
        Task<bool> IsFavoriteExistsAsync(int userId, int productId);
    }
}
