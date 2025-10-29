using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using schand.IRepository;
using schand.IService;
using schand.Models;

namespace schand.Service
{
 
    public class ProductService : IProductService
    {
        private readonly IProductRepository _repo;
        private readonly SchuserContext _context;


        public ProductService(IProductRepository repo, SchuserContext context)
        {
            _repo = repo;
            _context = context;
        }

        public Task<List<Product>> GetAllAsync() => _repo.GetAllAsync();
        public Task<Product?> GetByIdAsync(int id) => _repo.GetByIdAsync(id);
        public Task<Product> CreateAsync(Product product) => _repo.CreateAsync(product);
        public Task UpdateAsync(Product product) => _repo.UpdateAsync(product);
        public Task SoftDeleteAsync(int id) => _repo.SoftDeleteAsync(id);
        public async Task<bool> ApproveProductAsync(int productId)
        {
            return await _repo.UpdateApprovalStatusAsync(productId, true, null);
        }

        public async Task<bool> RejectProductAsync(int productId, string reason)
        {
            return await _repo.UpdateApprovalStatusAsync(productId, false, reason);
        }
        public Task<List<Product>> GetAllForAdminAsync()
        {
            return _repo.GetAllForAdminAsync();
        }

        public Task<Product?> GetByIdForAdminAsync(int id)
        {
            return _repo.GetByIdForAdminAsync(id);
        }
        public async Task<List<Product>> GetByCategoryAsync(int categoryId)
        {
            return await _context.Products
                .Include(p => p.Category)
                .Include(p => p.ProductImages)
                .Where(p => p.CategoryId == categoryId
                            && p.IsApproved == true
                            && p.IsActive == true)
                .OrderByDescending(p => p.CreatedAt)
                .ToListAsync();
        }
        public async Task<int?> GetSellerIdAsync(int productId)
        {
            return await _context.Products
                .Where(p => p.Id == productId)
                .Select(p => (int?)p.SellerId)
                .FirstOrDefaultAsync();
        }





    }

}
