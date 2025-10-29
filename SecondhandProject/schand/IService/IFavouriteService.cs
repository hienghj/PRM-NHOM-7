using schand.Models;

namespace schand.IService
{
    // /Services/Interfaces/IFavoriteService.cs
    public interface IFavoriteService
    {
        Task<IEnumerable<Product>> GetFavoritesAsync(int userId);
        Task AddFavoriteAsync(int userId, int productId);
        Task RemoveFavoriteAsync(int userId, int productId);
    }

}
