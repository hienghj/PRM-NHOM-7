using schand.IRepository;
using schand.IService;
using schand.Models;

namespace schand.Service
{
  
        public class ProductReviewService : IProductReviewService
        {
            private readonly IProductReviewRepository _repository;

            public ProductReviewService(IProductReviewRepository repository)
            {
                _repository = repository;
            }

            public async Task<IEnumerable<ProductReview>> GetReviewsByProductIdAsync(int productId)
            {
                return await _repository.GetReviewsByProductIdAsync(productId);
            }

            public async Task<ProductReview?> GetReviewByIdAsync(int id)
            {
                return await _repository.GetReviewByIdAsync(id);
            }

            public async Task AddReviewAsync(ProductReview review)
            {
                await _repository.AddReviewAsync(review);
            }

            public async Task UpdateReviewAsync(ProductReview review)
            {
                await _repository.UpdateReviewAsync(review);
            }

            public async Task SoftDeleteReviewAsync(int id)
            {
                await _repository.SoftDeleteReviewAsync(id);
            }
        }
    }

