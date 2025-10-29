using schand.Models;

namespace schand.IRepository.ADMIN
{
    public interface IUserManagerRepository
    {
        Task<List<User>> GetAllAsync();
        Task<User?> GetByIdAsync(int id);
        Task<bool> UpdateAsync(User user);
        Task<bool> ToggleLockAsync(int id);

    }
}
