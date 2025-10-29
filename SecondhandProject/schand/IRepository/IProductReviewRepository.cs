using schand.Models;
namespace schand.IRepository
{
    public interface IProductReviewRepository
    {
        Task<IEnumerable<ProductReview>> GetReviewsByProductIdAsync(int productId);
        Task<ProductReview?> GetReviewByIdAsync(int id);
        Task AddReviewAsync(ProductReview review);
        Task UpdateReviewAsync(ProductReview review);
        Task SoftDeleteReviewAsync(int id);
    }
}
