using System;
using System.Collections.Generic;

namespace schand.Models;

public partial class WalletTransaction
{
    public int Id { get; set; }

    public int UserId { get; set; }

    public string Type { get; set; } = null!;

    public long Amount { get; set; }

    public string Currency { get; set; } = null!;

    public string Status { get; set; } = null!;

    public string Provider { get; set; } = null!;

    public string? ProviderRef { get; set; }

    public string? Description { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime? CompletedAt { get; set; }

    public virtual User User { get; set; } = null!;
}
