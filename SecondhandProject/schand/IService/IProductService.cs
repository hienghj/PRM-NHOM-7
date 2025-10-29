using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using schand.Models;

namespace schand.IService
{

    public interface IProductService
    {
        Task<List<Product>> GetAllAsync();
        Task<Product?> GetByIdAsync(int id);
        Task<Product> CreateAsync(Product product);
        Task UpdateAsync(Product product);
        Task SoftDeleteAsync(int id);
        Task<bool> ApproveProductAsync(int productId);
        Task<bool> RejectProductAsync(int productId, string reason);
        Task<List<Product>> GetAllForAdminAsync();
        Task<Product?> GetByIdForAdminAsync(int id);
        Task<List<Product>> GetByCategoryAsync(int categoryId);
        Task<int?> GetSellerIdAsync(int productId);




    }

}
