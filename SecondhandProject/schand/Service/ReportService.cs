using schand.DTOS.Report;
using schand.IRepository.ADMIN;
using schand.IService;
using schand.Models;

namespace schand.Service
{
    public class ReportService : IReportService
    {
        private readonly IReportRepository _reportRepo;

        public ReportService(IReportRepository reportRepo)
        {
            _reportRepo = reportRepo;
        }

        public async Task CreateAsync(int userId, ReportCreateDTO dto)
        {
            var report = new Report
            {
                ReporterId = userId,
                ProductId = dto.ProductId,
                Reason = dto.Reason
            };

            await _reportRepo.CreateAsync(report);
        }

        public async Task<IEnumerable<ReportReadDTO>> GetByUserAsync(int userId)
        {
            var reports = await _reportRepo.GetByUserAsync(userId);
            return reports.Select(r => new ReportReadDTO
            {
                Id = r.Id,
                ProductTitle = r.Product?.Title ?? "",
                Reason = r.Reason,
                CreatedAt = r.CreatedAt ?? DateTime.MinValue,
                ReporterName = r.Reporter?.FullName ?? "",
                IsResolved = r.IsResolved // giả định có
            });
        }

        public async Task<IEnumerable<ReportReadDTO>> GetAllAsync()
        {
            var reports = await _reportRepo.GetAllAsync();
            return reports.Select(r => new ReportReadDTO
            {
                Id = r.Id,
                ProductTitle = r.Product?.Title ?? "",
                Reason = r.Reason,
                CreatedAt = r.CreatedAt ?? DateTime.MinValue,
                ReporterName = r.Reporter?.FullName ?? "",
                IsResolved = r.IsResolved
            });
        }

        public async Task<ReportReadDTO?> GetByIdAsync(int id)
        {
            var r = await _reportRepo.GetByIdAsync(id);
            if (r == null) return null;

            return new ReportReadDTO
            {
                Id = r.Id,
                ProductTitle = r.Product?.Title ?? "",
                Reason = r.Reason,
                CreatedAt = r.CreatedAt ?? DateTime.MinValue,
                ReporterName = r.Reporter?.FullName ?? "",
                IsResolved = r.IsResolved
            };
        }

        public async Task<bool> RevokeAsync(int userId, int reportId)
        {
            return await _reportRepo.RevokeAsync(userId, reportId);
        }

        public async Task<bool> MarkResolvedAsync(int reportId)
        {
            return await _reportRepo.MarkResolvedAsync(reportId);
        }

        public async Task<bool> DeleteAsync(int reportId)
        {
            return await _reportRepo.DeleteAsync(reportId);
        }
    }

}
