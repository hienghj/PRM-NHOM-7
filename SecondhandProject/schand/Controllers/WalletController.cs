using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System.Text.Json;
using Microsoft.AspNetCore.Authorization;
using System.Net.Http.Json;
using schand.Helper;
using schand.Models;


namespace schand.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class WalletController : ControllerBase
    {
        private readonly SchuserContext _db;
        private readonly IConfiguration _config;

        public WalletController(SchuserContext db, IConfiguration config)
        {
            _db = db;
            _config = config;
        }

        // ========= DTOs =========
        public sealed class CreateDepositDto { public long Amount { get; set; } }
        public sealed class PayOSWebhookDto
        {
            public string code { get; set; } = "";
            public string desc { get; set; } = "";
            public bool success { get; set; }
            public JsonElement data { get; set; }
            public string signature { get; set; } = "";
        }

        // ========= 1) Tạo deposit: gọi PayOS để lấy checkoutUrl =========
        [HttpPost("deposits")]
        [Authorize]
        public async Task<IActionResult> CreateDeposit([FromBody] CreateDepositDto dto)
        {
            if (dto.Amount <= 0) return BadRequest("Số tiền không hợp lệ.");

            var userId = int.Parse(User.Claims.First(c => c.Type == "UserId")!.Value);

            // PayOS yêu cầu orderCode là INT
            var orderCode = PayOSHelpers.NewOrderCode();
            var amount = dto.Amount;
            var description = "DEPOSIT"; // <= 9 ký tự để an toàn

            var returnUrl = _config["PayOS:ReturnUrl"];
            var cancelUrl = _config["PayOS:CancelUrl"];
            var checksum = _config["PayOS:ChecksumKey"]!;

            // Chuỗi ký: sort alphabet các key
            var toSign = $"amount={amount}&cancelUrl={cancelUrl}&description={description}&orderCode={orderCode}&returnUrl={returnUrl}";
            var signature = PayOSHelpers.HmacSha256Hex(checksum, toSign);

            var body = new
            {
                orderCode = orderCode,
                amount = amount,
                description = description,
                cancelUrl = cancelUrl,
                returnUrl = returnUrl,
                signature = signature
            };

            using var http = new HttpClient
            {
                BaseAddress = new Uri(_config["PayOS:BaseUrl"]!),
                Timeout = TimeSpan.FromSeconds(15) // <= thêm timeout để tránh treo
            };

            // dùng TryAddWithoutValidation để tránh lỗi format header lặt vặt
            http.DefaultRequestHeaders.TryAddWithoutValidation("x-client-id", _config["PayOS:ClientId"]!);
            http.DefaultRequestHeaders.TryAddWithoutValidation("x-api-key", _config["PayOS:ApiKey"]!);

            HttpResponseMessage res;
            JsonElement json;

            try
            {
                res = await http.PostAsJsonAsync("/v2/payment-requests", body);
                json = await res.Content.ReadFromJsonAsync<JsonElement>();
            }
            catch (Exception)
            {
                // Test nhanh: trả về 502 để biết là upstream (PayOS) có sự cố/timeout
                return StatusCode(502, new { error = "PayOS upstream unavailable/timeout" });
            }

            if (!res.IsSuccessStatusCode)
                return StatusCode((int)res.StatusCode, json);

            // --- giữ nguyên phần xử lý success bên dưới ---
            var data = json.GetProperty("data");
            var checkoutUrl = data.GetProperty("checkoutUrl").GetString();
            var paymentLinkId = data.GetProperty("paymentLinkId").GetString();
            // Lưu bản ghi Pending
            var tx = new WalletTransaction
            {
                UserId = userId,
                Type = "Deposit",
                Amount = amount,
                Currency = "VND",
                Status = "Pending",
                Provider = "PayOS",
                ProviderRef = paymentLinkId,              // id của PayOS
                Description = $"orderCode={orderCode}",   // để tra soát phụ
                CreatedAt = DateTime.UtcNow
            };
            _db.WalletTransactions.Add(tx);
            await _db.SaveChangesAsync();

            return Ok(new
            {
                transactionId = tx.Id,
                orderCode,
                amount,
                checkoutUrl
            });
        }
        // chỉ để test/debug route, không ảnh hưởng logic
        [HttpGet("webhook/payos")]
        [AllowAnonymous]
        public IActionResult PayOSWebhookProbe()
        {
            return Ok("Webhook endpoint is alive. Use POST for real requests.");
        }


        // ========= 2) Webhook PayOS: verify chữ ký → auto Completed + cộng điểm =========
        [HttpPost("webhook/payos")]
        [AllowAnonymous] // xác thực bằng signature
        public async Task<IActionResult> PayOSWebhook([FromBody] PayOSWebhookDto payload)
        {
            // (a) Verify signature
            var checksumKey = _config["PayOS:ChecksumKey"]!;
            var dataStr = PayOSHelpers.BuildSignatureStringFromData(payload.data);
            var expectedSig = PayOSHelpers.HmacSha256Hex(checksumKey, dataStr);
            if (!string.Equals(expectedSig, payload.signature, StringComparison.OrdinalIgnoreCase))
                return Unauthorized();

            // (b) Chỉ xử lý khi thành công
            if (!payload.success || payload.code != "00")
                return Ok();

            // (c) Lấy thông tin từ data
            var orderCode = payload.data.GetProperty("orderCode").GetInt32();
            var amount = payload.data.GetProperty("amount").GetInt64();
            var paymentLinkId = payload.data.GetProperty("paymentLinkId").GetString();

            // Tìm transaction Pending theo ProviderRef hoặc orderCode
            var tx = _db.WalletTransactions.FirstOrDefault(x =>
                x.Status == "Pending" &&
                x.Provider == "PayOS" &&
                (x.ProviderRef == paymentLinkId || (x.Description ?? "").Contains($"orderCode={orderCode}")));

            if (tx == null) return Ok();          // idempotent
            if (tx.Amount != amount) return Ok(); // lệch tiền → bỏ qua (log nếu cần)

            // (d) Cộng ví & chốt giao dịch (atomic)
            var user = await _db.Users.FindAsync(tx.UserId);
            if (user == null) return Ok();

            using var dbtx = await _db.Database.BeginTransactionAsync();
            tx.Status = "Completed";
            tx.CompletedAt = DateTime.UtcNow;
            await _db.SaveChangesAsync();

            user.RecloopPoint += tx.Amount;
            await _db.SaveChangesAsync();
            await dbtx.CommitAsync();

            return Ok();
        }

        // ====== (tuỳ chọn) API xem số dư ======
        [HttpGet("balance")]
        [Authorize]
        public async Task<IActionResult> Balance()
        {
            var userId = int.Parse(User.Claims.First(c => c.Type == "UserId")!.Value);
            var u = await _db.Users.FindAsync(userId);
            return Ok(new { balance = u?.RecloopPoint ?? 0L });
        }
    }
}
