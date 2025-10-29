using System;
using System.Collections.Generic;

namespace schand.Models;

public partial class Product
{
    public int Id { get; set; }

    public string Title { get; set; } = null!;

    public string? Descriptions { get; set; }

    public decimal Price { get; set; }

    public string? Condition { get; set; }

    public string? Locations { get; set; }

    public int SellerId { get; set; }

    public int? CategoryId { get; set; }

    public bool? IsActive { get; set; }

    public DateTime? CreatedAt { get; set; }

    public bool IsApproved { get; set; }

    public DateTime? ApprovedAt { get; set; }

    public string? RejectedReason { get; set; }

    public bool? IsSold { get; set; }

    public DateTime? SoldAt { get; set; }

    public DateTime? ExpireAt { get; set; }

    public DateTime? BoostedUntil { get; set; }

    public virtual Category? Category { get; set; }

    public virtual ICollection<Favorite> Favorites { get; set; } = new List<Favorite>();

    public virtual ICollection<Messager> Messagers { get; set; } = new List<Messager>();

    public virtual ICollection<ProductComment> ProductComments { get; set; } = new List<ProductComment>();

    public virtual ICollection<ProductImage> ProductImages { get; set; } = new List<ProductImage>();

    public virtual ICollection<ProductReview> ProductReviews { get; set; } = new List<ProductReview>();

    public virtual ICollection<Report> Reports { get; set; } = new List<Report>();

    public virtual User Seller { get; set; } = null!;
}
