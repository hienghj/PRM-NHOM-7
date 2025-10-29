using schand.DTOS.ADMIN;

namespace schand.IService.ADMIN
{
    public interface IUserManagerService
    {
        Task<List<UserDTO>> GetAllAsync();
        Task<UserDTO?> GetByIdAsync(int id);
        Task<bool> ToggleLockAsync(int id);
        Task<bool> UpdateAsync(int id, UserUpdateDTO dto);

    }
}
