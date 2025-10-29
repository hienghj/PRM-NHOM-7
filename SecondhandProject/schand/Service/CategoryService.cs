using schand.IRepository.ADMIN;
using schand.IService;
using schand.Models;

namespace schand.Service
{
    public class CategoryService : ICategoryService
    {
        private readonly ICategoryRepository _repo;

        public CategoryService(ICategoryRepository repo)
        {
            _repo = repo;
        }

        public Task<List<Category>> GetAllAsync() => _repo.GetAllAsync();
        public Task<Category?> GetByIdAsync(int id) => _repo.GetByIdAsync(id);
        public Task<Category> CreateAsync(string name) => _repo.CreateAsync(name);
        public Task<bool> UpdateAsync(int id, string name) => _repo.UpdateAsync(id, name);
        public Task<bool> DeleteAsync(int id) => _repo.DeleteAsync(id);
    }
}