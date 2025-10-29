using schand.DTOS.Message;

namespace schand.IService
{
    public interface IMessageService

    {
        Task SendMessageAsync(int senderId, MessageCreateDTO dto);
        Task<IEnumerable<MessageReadDTO>> GetConversationAsync(int userId, int otherUserId, int productId);
        Task<IEnumerable<string>> GetContactNamesAsync(int userId);
        Task<bool> DeleteAsync(int messageId, int userId);
    }
}
