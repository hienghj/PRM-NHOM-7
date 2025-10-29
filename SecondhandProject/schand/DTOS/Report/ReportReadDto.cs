namespace schand.DTOS.Report
{
    public class ReportReadDTO
    {
        public int Id { get; set; }
        public string ProductTitle { get; set; } = string.Empty;
        public string Reason { get; set; } = string.Empty;
        public string ReporterName { get; set; } = string.Empty;
        public DateTime CreatedAt { get; set; }
        public bool IsResolved { get; set; }
    }
}
