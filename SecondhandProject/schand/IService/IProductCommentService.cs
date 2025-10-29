using schand.Models;

namespace schand.IService
{
    public interface IProductCommentService
    {
        // Lấy tất cả comment của một sản phẩm
        Task<IEnumerable<ProductComment>> GetCommentsByProductIdAsync(int productId);

        // Lấy một comment cụ thể
        Task<ProductComment> GetCommentByIdAsync(int id);

        // Thêm một comment mới
        Task AddCommentAsync(ProductComment comment);

        // Cập nhật một comment
        Task<bool> UpdateCommentAsync(ProductComment comment);

        // Xóa một comment
        Task<bool> DeleteCommentAsync(int id, int userId);
    }
}
