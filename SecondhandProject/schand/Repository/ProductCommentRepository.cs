using Microsoft.EntityFrameworkCore;
using schand.IRepository;
using schand.Models;
using static schand.Repository.ProductCommentRepository;

namespace schand.Repository
{
    public class ProductCommentRepository : IProductCommentRepository
    {
        
            private readonly SchuserContext _context;

            public ProductCommentRepository(SchuserContext context)
            {
                _context = context;
            }

            public async Task<IEnumerable<ProductComment>> GetCommentsByProductIdAsync(int productId)
            {
            return await _context.ProductComments
                    .Include(c => c.User) // Bao gồm thông tin người dùng
                    .Include(c => c.InverseParent) // Bao gồm các comment con
                    .Where(c => c.ProductId == productId && c.IsDeleted == false) // Lọc theo ProductId và trạng thái xóa
                    .ToListAsync();
        }

            public async Task<ProductComment> GetCommentByIdAsync(int id)
            {
                 return await _context.ProductComments
                         .Include(c => c.User) // Bao gồm thông tin người dùng
                         .FirstOrDefaultAsync(c => c.Id == id && c.IsDeleted == false); // Lấy comment theo Id và trạng thái xóa
}
            

            public async Task AddCommentAsync(ProductComment comment)
            {
                _context.ProductComments.Add(comment);
                await _context.SaveChangesAsync();
            }

            public async Task UpdateCommentAsync(ProductComment comment)
            {
                _context.Entry(comment).State = EntityState.Modified;
                await _context.SaveChangesAsync();
            }

            public async Task DeleteCommentAsync(ProductComment comment)
            {
                _context.ProductComments.Remove(comment);
                await _context.SaveChangesAsync();
            }

            public async Task<bool> CommentExistsAsync(int id)
            {
                return await _context.ProductComments.AnyAsync(e => e.Id == id);
            }
        }
    }

