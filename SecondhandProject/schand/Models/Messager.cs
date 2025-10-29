using System;
using System.Collections.Generic;

namespace schand.Models;

public partial class Messager
{
    public int Id { get; set; }

    public int SenderId { get; set; }

    public int ReceiverId { get; set; }

    public int? ProductId { get; set; }

    public string? Content { get; set; }

    public DateTime? SentAt { get; set; }

    public virtual Product? Product { get; set; }

    public virtual User Receiver { get; set; } = null!;

    public virtual User Sender { get; set; } = null!;
}
