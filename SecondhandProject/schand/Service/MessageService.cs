using Microsoft.EntityFrameworkCore;
using schand.DTOS.Message;
using schand.IRepository;
using schand.IService;
using schand.Models;

namespace schand.Service
{
    public class MessageService : IMessageService
    {
        private readonly IMessageRepository _repo;
        private readonly SchuserContext _context;

        public MessageService(IMessageRepository repo, SchuserContext context)
        {
            _repo = repo;
            _context = context;
        }

        public async Task SendMessageAsync(int senderId, MessageCreateDTO dto)
        {
            var product = await _context.Products.FindAsync(dto.ProductId);
            if (product == null)
                throw new Exception("Sản phẩm không tồn tại.");

            if (product.SellerId != senderId && product.SellerId != dto.ReceiverId)
                throw new Exception("Không được phép nhắn về sản phẩm này.");

            var message = new Messager 
            {
                SenderId = senderId,
                ReceiverId = dto.ReceiverId,
                ProductId = dto.ProductId,
                Content = dto.Content
            };

            await _repo.CreateAsync(message);
        }


        public async Task<IEnumerable<MessageReadDTO>> GetConversationAsync(int userId, int otherUserId, int productId)
        {
            var messages = await _repo.GetMessagesAsync(userId, otherUserId, productId);
            return messages.Select(m => new MessageReadDTO
            {
                Id = m.Id,
                SenderId = m.SenderId,
                SenderName = m.Sender?.FullName ?? "",
                Content = m.Content,
                SentAt = m.SentAt ?? DateTime.MinValue,
                IsMine = m.SenderId == userId
            });
        }

        public async Task<IEnumerable<string>> GetContactNamesAsync(int userId)
        {
            var contacts = await _repo.GetContactsAsync(userId);
            return contacts.Select(u => u.FullName);
        }

        public async Task<bool> DeleteAsync(int messageId, int userId)
        {
            return await _repo.DeleteAsync(messageId, userId);
        }

    }
}