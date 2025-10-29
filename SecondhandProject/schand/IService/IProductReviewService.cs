using schand.DTOS.Review;
using schand.Models;

namespace schand.IService
{
    
        public interface IProductReviewService
        {
            Task<IEnumerable<ProductReview>> GetReviewsByProductIdAsync(int productId);
            Task<ProductReview?> GetReviewByIdAsync(int id);
            Task AddReviewAsync(ProductReview review);
            Task UpdateReviewAsync(ProductReview review);
            Task SoftDeleteReviewAsync(int id);
        }
    }


