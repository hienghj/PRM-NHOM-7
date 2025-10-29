using Microsoft.EntityFrameworkCore;
using schand.IRepository;
using schand.Models;

namespace schand.Repository
{
    public class ProductReviewRepository : IProductReviewRepository
    {
        private readonly SchuserContext _context;

        public ProductReviewRepository(SchuserContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<ProductReview>> GetReviewsByProductIdAsync(int productId)
        {
            // Chỉ lấy các review chưa bị xóa mềm
            return await _context.ProductReviews
                             .Where(r => r.ProductId == productId && r.IsDeleted == false)
                                 .ToListAsync();
        }

        public async Task<ProductReview?> GetReviewByIdAsync(int id)
        {
            return await _context.ProductReviews
                                 .FirstOrDefaultAsync(r => r.Id == id && r.IsDeleted == false);
        }

        public async Task AddReviewAsync(ProductReview review)
        {
            _context.ProductReviews.Add(review);
            await _context.SaveChangesAsync();
        }

        public async Task UpdateReviewAsync(ProductReview review)
        {
            _context.Entry(review).State = EntityState.Modified;
            await _context.SaveChangesAsync();
        }

        public async Task SoftDeleteReviewAsync(int id)
        {
            var review = await _context.ProductReviews.FindAsync(id);
            if (review != null)
            {
                review.IsDeleted = true; // Thực hiện xóa mềm
                _context.Entry(review).State = EntityState.Modified;
                await _context.SaveChangesAsync();
            }
        }
    }
}
