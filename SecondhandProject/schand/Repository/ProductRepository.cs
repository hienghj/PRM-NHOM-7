using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using schand.IRepository;
using schand.Models;

namespace schand.Repository
{
   
    public class ProductRepository : IProductRepository
    {
        private readonly SchuserContext _context;

        public ProductRepository(SchuserContext context)
        {
            _context = context;
        }

        public async Task<List<Product>> GetAllAsync()
        {
            return await _context.Products
                .Include(p => p.Category)
                        .Include(p => p.ProductImages)  // Bao gồm ảnh

   .Where(p => p.IsApproved == true && p.IsActive == true)


                .OrderByDescending(p => p.CreatedAt)
                .ToListAsync();
        }




        public async Task<Product?> GetByIdAsync(int id)
        {
            return await _context.Products
                .Include(p => p.Category)
                        .Include(p => p.ProductImages)  // Bao gồm ảnh
                                .Include(p => p.Seller) // load thêm user seller

       .FirstOrDefaultAsync(p => p.Id == id && p.IsApproved == true && p.IsActive == true);

        }
        public async Task<List<Product>> GetAllForAdminAsync()
        {
            return await _context.Products
                .Include(p => p.Category)
                .Include(p => p.ProductImages) // ✅ Thêm để load ảnh cho admin
                .OrderByDescending(p => p.CreatedAt)
                .ToListAsync();
        }

        public async Task<Product?> GetByIdForAdminAsync(int id)
        {
            return await _context.Products
                .Include(p => p.Category)
                .Include(p => p.ProductImages) // ✅ Thêm nếu muốn xem ảnh khi get chi tiết
                .FirstOrDefaultAsync(p => p.Id == id);
        }


        public async Task<Product> CreateAsync(Product product)
        {
            product.CreatedAt = DateTime.Now;
            product.IsActive = true;
            _context.Products.Add(product);
            await _context.SaveChangesAsync();
            return product;
        }

        public async Task UpdateAsync(Product product)
        {
            var existing = await _context.Products.FindAsync(product.Id);
            if (existing == null) return;

            existing.Title = product.Title;
            existing.Descriptions = product.Descriptions;
            existing.Price = product.Price;
            existing.Condition = product.Condition;
            existing.Locations = product.Locations;
            existing.CategoryId = product.CategoryId;

            await _context.SaveChangesAsync();
        }

        public async Task SoftDeleteAsync(int id)
        {
            var product = await _context.Products.FindAsync(id);
            if (product == null) return;

            product.IsActive = false;
            await _context.SaveChangesAsync();
        }
        public async Task<bool> UpdateApprovalStatusAsync(int productId, bool isApproved, string? reason)
        {
            var product = await _context.Products.FindAsync(productId);
            if (product == null) return false;

            product.IsApproved = isApproved;
            product.ApprovedAt = isApproved ? DateTime.Now : null;
            product.RejectedReason = isApproved ? null : reason;

            await _context.SaveChangesAsync();
            return true;
        }

    }

}
