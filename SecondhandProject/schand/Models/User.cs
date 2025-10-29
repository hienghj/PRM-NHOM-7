using System;
using System.Collections.Generic;

namespace schand.Models;

public partial class User
{
    public int Id { get; set; }

    public string Email { get; set; } = null!;

    public string PasswordHash { get; set; } = null!;

    public string FullName { get; set; } = null!;

    public string Role { get; set; } = null!;

    public DateTime CreatedAt { get; set; }

    public bool? IsLocked { get; set; }

    public string? EmailOtp { get; set; }

    public DateTime? EmailOtpExpiration { get; set; }

    public bool IsEmailConfirmed { get; set; }

    public string? PhoneNumber { get; set; }

    public string? AvatarUrl { get; set; }

    public string? Gender { get; set; }

    public DateOnly? DateOfBirth { get; set; }

    public string? Address { get; set; }

    public double? TrustScore { get; set; }

    public bool? IsSellerVerified { get; set; }

    public long RecloopPoint { get; set; }

    public virtual ICollection<Favorite> Favorites { get; set; } = new List<Favorite>();

    public virtual ICollection<Messager> MessagerReceivers { get; set; } = new List<Messager>();

    public virtual ICollection<Messager> MessagerSenders { get; set; } = new List<Messager>();

    public virtual ICollection<ProductComment> ProductComments { get; set; } = new List<ProductComment>();

    public virtual ICollection<ProductReview> ProductReviews { get; set; } = new List<ProductReview>();

    public virtual ICollection<Product> Products { get; set; } = new List<Product>();

    public virtual ICollection<Report> Reports { get; set; } = new List<Report>();

    public virtual ICollection<WalletTransaction> WalletTransactions { get; set; } = new List<WalletTransaction>();
}
