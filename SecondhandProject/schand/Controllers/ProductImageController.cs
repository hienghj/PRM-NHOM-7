using CloudinaryDotNet;
using CloudinaryDotNet.Actions;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;
using schand.Confirguration;
using schand.Models;

namespace schand.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ProductImageController : ControllerBase
    {
        private readonly SchuserContext _context;
        private readonly Cloudinary _cloudinary;

        public ProductImageController(SchuserContext context, IOptions<CloudinarySettings> config)
        {
            _context = context;

            var acc = new Account(
                config.Value.CloudName,
                config.Value.ApiKey,
                config.Value.ApiSecret
            );

            _cloudinary = new Cloudinary(acc);
        }

        [HttpPost("upload-to-cloud/{productId}")]
        public async Task<IActionResult> UploadToCloud(int productId, [FromForm] List<IFormFile> files)
        {
            var product = await _context.Products.FindAsync(productId);
            if (product == null) return NotFound("Không tìm thấy sản phẩm.");

            var uploadedImages = new List<ProductImage>();

            foreach (var file in files)
            {
                if (file.Length > 0)
                {
                    var uploadParams = new ImageUploadParams
                    {
                        File = new FileDescription(file.FileName, file.OpenReadStream()),
                        Folder = "schand_product_images"
                    };

                    var uploadResult = await _cloudinary.UploadAsync(uploadParams);

                    var image = new ProductImage
                    {
                        ProductId = productId,
                        ImageUrl = uploadResult.SecureUrl.ToString(),
                        CreatedAt = DateTime.Now
                    };

                    uploadedImages.Add(image);
                }
            }

            _context.ProductImages.AddRange(uploadedImages);
            await _context.SaveChangesAsync();

            // 🔥 Cắt vòng lặp để tránh lỗi serialization
            foreach (var img in uploadedImages)
            {
                img.Product = null;
            }

            return Ok(uploadedImages);
        }
    }
}
