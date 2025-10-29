using Microsoft.EntityFrameworkCore;
using schand.IRepository.ADMIN;
using schand.Models;
using System;

namespace schand.Repository
{
    public class ReportRepository : IReportRepository
    {
        private readonly SchuserContext _context;

        public ReportRepository(SchuserContext context)
        {
            _context = context;
        }

        public async Task CreateAsync(Report report)
        {
            report.CreatedAt = DateTime.UtcNow;
            _context.Reports.Add(report);
            await _context.SaveChangesAsync();
        }

        public async Task<IEnumerable<Report>> GetByUserAsync(int userId)
        {
            return await _context.Reports
                .Where(r => r.ReporterId == userId)
                .Include(r => r.Product)
                .Include(r => r.Reporter)
                .ToListAsync();
        }

        public async Task<IEnumerable<Report>> GetAllAsync()
        {
            return await _context.Reports
                .Include(r => r.Product)
                .Include(r => r.Reporter)
                .ToListAsync();
        }

        public async Task<Report?> GetByIdAsync(int id)
        {
            return await _context.Reports
                .Include(r => r.Product)
                .Include(r => r.Reporter)
                .FirstOrDefaultAsync(r => r.Id == id);
        }

        public async Task<bool> RevokeAsync(int userId, int reportId)
        {
            var report = await _context.Reports.FirstOrDefaultAsync(r => r.Id == reportId && r.ReporterId == userId);
            if (report == null)
                return false;

            _context.Reports.Remove(report);
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<bool> MarkResolvedAsync(int reportId)
        {
            var report = await _context.Reports.FindAsync(reportId);
            if (report == null)
                return false;

            // Giả định bạn có cột IsResolved
            report.IsResolved = true;
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<bool> DeleteAsync(int reportId)
        {
            var report = await _context.Reports.FindAsync(reportId);
            if (report == null)
                return false;

            _context.Reports.Remove(report);
            await _context.SaveChangesAsync();
            return true;
        }
    }

}
