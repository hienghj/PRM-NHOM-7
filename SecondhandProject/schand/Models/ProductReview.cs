using System;
using System.Collections.Generic;

namespace schand.Models;

public partial class ProductReview
{
    public int Id { get; set; }

    public int ProductId { get; set; }

    public int UserId { get; set; }

    public int? Rating { get; set; }

    public string? ReviewContent { get; set; }

    public DateTime CreatedAt { get; set; }

    public bool? IsVerifiedPurchase { get; set; }

    public bool? IsDeleted { get; set; }

    public virtual Product Product { get; set; } = null!;

    public virtual User User { get; set; } = null!;
}
