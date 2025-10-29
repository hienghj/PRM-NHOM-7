using System;
using System.Collections.Generic;

namespace schand.Models;

public partial class ProductComment
{
    public int Id { get; set; }

    public int ProductId { get; set; }

    public int UserId { get; set; }

    public string Content { get; set; } = null!;

    public int? ParentId { get; set; }

    public DateTime CreatedAt { get; set; }

    public bool? IsDeleted { get; set; }

    public virtual ICollection<ProductComment> InverseParent { get; set; } = new List<ProductComment>();

    public virtual ProductComment? Parent { get; set; }

    public virtual Product Product { get; set; } = null!;

    public virtual User User { get; set; } = null!;
}
