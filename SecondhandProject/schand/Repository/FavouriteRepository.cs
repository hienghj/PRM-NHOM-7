using Microsoft.EntityFrameworkCore;
using schand.IRepository;
using schand.Models;
using System;

namespace schand.Repository
{
    // /Repositories/Implementations/FavoriteRepository.cs
    public class FavoriteRepository : IFavouriteRepository
    {
        private readonly SchuserContext _context;

        public FavoriteRepository(SchuserContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<Product>> GetFavoritesByUserIdAsync(int userId)
        {
            return await _context.Favorites
                .Where(f => f.UserId == userId)
                .Include(f => f.Product)
                .Select(f => f.Product)
                .ToListAsync();
        }

        public async Task AddFavoriteAsync(int userId, int productId)
        {
            if (!await IsFavoriteExistsAsync(userId, productId))
            {
                var favorite = new Favorite
                {
                    UserId = userId,
                    ProductId = productId,
                    CreatedAt = DateTime.UtcNow
                };

                _context.Favorites.Add(favorite);
                await _context.SaveChangesAsync();
            }
        }

        public async Task RemoveFavoriteAsync(int userId, int productId)
        {
            var favorite = await _context.Favorites
                .FirstOrDefaultAsync(f => f.UserId == userId && f.ProductId == productId);

            if (favorite != null)
            {
                _context.Favorites.Remove(favorite);
                await _context.SaveChangesAsync();
            }
        }

        public async Task<bool> IsFavoriteExistsAsync(int userId, int productId)
        {
            return await _context.Favorites
                .AnyAsync(f => f.UserId == userId && f.ProductId == productId);
        }
    }

}
