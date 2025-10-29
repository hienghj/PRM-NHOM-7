using schand.Models;

namespace schand.IRepository
{
    public interface IMessageRepository
    {
        Task CreateAsync(Messager message);
        Task<IEnumerable<Messager>> GetMessagesAsync(int user1Id, int user2Id, int productId);
        Task<IEnumerable<User>> GetContactsAsync(int userId);
        Task<bool> DeleteAsync(int messageId, int userId);
    }
}
