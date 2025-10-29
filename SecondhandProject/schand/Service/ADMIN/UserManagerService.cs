using schand.DTOS.ADMIN;
using schand.IRepository.ADMIN;
using schand.IService.ADMIN;

namespace schand.Service.ADMIN
{
    public class UserManagerService : IUserManagerService
    {
        private readonly IUserManagerRepository _repo;

        public UserManagerService(IUserManagerRepository repo)
        {
            _repo = repo;
        }

        public async Task<List<UserDTO>> GetAllAsync()
        {
            var users = await _repo.GetAllAsync();
            return users.Select(u => new UserDTO
            {
                Id = u.Id,
                Email = u.Email,
                FullName = u.FullName,
                Role = u.Role,
               
            }).ToList();
        }

        public async Task<UserDTO?> GetByIdAsync(int id)
        {
            var u = await _repo.GetByIdAsync(id);
            return u == null ? null : new UserDTO
            {
                Id = u.Id,
                Email = u.Email,
                FullName = u.FullName,
                Role = u.Role,
                
            };
        }

        public async Task<bool> ToggleLockAsync(int id)
        {
            return await _repo.ToggleLockAsync(id);
        }

        public async Task<bool> UpdateAsync(int id, UserUpdateDTO dto)
        {
            var user = await _repo.GetByIdAsync(id);
            if (user == null) return false;

            user.FullName = dto.FullName;
            user.Role = dto.Role;
            return await _repo.UpdateAsync(user);
        }
    }
}

