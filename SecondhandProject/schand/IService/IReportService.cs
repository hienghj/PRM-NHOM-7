using schand.DTOS.Report;

namespace schand.IService
{
    public interface IReportService
    {
        Task CreateAsync(int userId, ReportCreateDTO dto);
        Task<IEnumerable<ReportReadDTO>> GetByUserAsync(int userId);
        Task<IEnumerable<ReportReadDTO>> GetAllAsync();
        Task<ReportReadDTO?> GetByIdAsync(int id);
        Task<bool> RevokeAsync(int userId, int reportId);
        Task<bool> MarkResolvedAsync(int reportId);
        Task<bool> DeleteAsync(int reportId);
    }


}
