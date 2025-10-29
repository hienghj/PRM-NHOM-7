using System;
using System.Collections.Generic;

namespace schand.Models;

public partial class Report
{
    public int Id { get; set; }

    public int ReporterId { get; set; }

    public int ProductId { get; set; }

    public string? Reason { get; set; }

    public DateTime? CreatedAt { get; set; }

    public bool IsResolved { get; set; }

    public virtual Product Product { get; set; } = null!;

    public virtual User Reporter { get; set; } = null!;
}
