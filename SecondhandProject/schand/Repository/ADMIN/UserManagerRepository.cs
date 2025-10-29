using Microsoft.EntityFrameworkCore;
using schand.IRepository.ADMIN;
using schand.Models;

namespace schand.Repository.ADMIN
{
    public class UserManagerRepository : IUserManagerRepository
    {
        private readonly SchuserContext _context;

        public UserManagerRepository(SchuserContext context)
        {
            _context = context;
        }

        public async Task<List<User>> GetAllAsync() =>
            await _context.Users.ToListAsync();

        public async Task<User?> GetByIdAsync(int id) =>
            await _context.Users.FirstOrDefaultAsync(u => u.Id == id);

        public async Task<bool> ToggleLockAsync(int id)
        {
            var user = await _context.Users.FindAsync(id);
            if (user == null) return false;

            user.IsLocked = !user.IsLocked;
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<bool> UpdateAsync(User user)
        {
            _context.Users.Update(user);
            await _context.SaveChangesAsync();
            return true;
        }

        
    }
}
