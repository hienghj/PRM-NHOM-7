using schand.Models;

namespace schand.IRepository
{
    public interface IProductCommentRepository
    {
        Task<IEnumerable<ProductComment>> GetCommentsByProductIdAsync(int productId);
        Task<ProductComment> GetCommentByIdAsync(int id);
        Task AddCommentAsync(ProductComment comment);
        Task UpdateCommentAsync(ProductComment comment);
        Task DeleteCommentAsync(ProductComment comment);
        Task<bool> CommentExistsAsync(int id);
    }
}
