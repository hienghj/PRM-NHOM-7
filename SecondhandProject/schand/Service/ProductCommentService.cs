using schand.IRepository;
using schand.IService;
using schand.Models;

namespace schand.Service
{
    public class ProductCommentService : IProductCommentService
    {
        private readonly IProductCommentRepository _commentRepository;

        public ProductCommentService(IProductCommentRepository commentRepository)
        {
            _commentRepository = commentRepository;
        }

        public async Task<IEnumerable<ProductComment>> GetCommentsByProductIdAsync(int productId)
        {
            return await _commentRepository.GetCommentsByProductIdAsync(productId);
        }

        public async Task<ProductComment> GetCommentByIdAsync(int id)
        {
            return await _commentRepository.GetCommentByIdAsync(id);
        }

        public async Task AddCommentAsync(ProductComment comment)
        {
            // Có thể thêm logic nghiệp vụ trước khi gọi repo, ví dụ: kiểm tra spam
            await _commentRepository.AddCommentAsync(comment);
        }

        public async Task<bool> UpdateCommentAsync(ProductComment comment)
        {
            // Kiểm tra quyền sở hữu ngay tại service
            var existingComment = await _commentRepository.GetCommentByIdAsync(comment.Id);
            if (existingComment == null || existingComment.UserId != comment.UserId)
            {
                return false;
            }

            // Gán các giá trị mới từ đối tượng comment được truyền vào
            existingComment.Content = comment.Content;
            //... có thể cập nhật các trường khác nếu cần

            await _commentRepository.UpdateCommentAsync(existingComment);
            return true;
        }

        public async Task<bool> DeleteCommentAsync(int id, int userId)
        {
            var existingComment = await _commentRepository.GetCommentByIdAsync(id);
            if (existingComment == null || existingComment.UserId != userId)
            {
                return false;
            }

            // Thực hiện xóa mềm
            existingComment.IsDeleted = true;
            await _commentRepository.UpdateCommentAsync(existingComment);
            return true;
        }
    }
}
