using schand.Models;

namespace schand.IRepository
{
    public interface IProductRepository
    {
        Task<List<Product>> GetAllAsync();
        Task<Product?> GetByIdAsync(int id);
        Task<Product> CreateAsync(Product product);
        Task UpdateAsync(Product product);
        Task SoftDeleteAsync(int id);
        Task<bool> UpdateApprovalStatusAsync(int productId, bool isApproved, string? reason);
        Task<List<Product>> GetAllForAdminAsync();
        Task<Product?> GetByIdForAdminAsync(int id);

    }

}
