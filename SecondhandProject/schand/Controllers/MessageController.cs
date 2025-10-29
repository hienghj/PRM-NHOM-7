using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.SignalR;
using schand.DTOS.Message;
using schand.Hubs;
using schand.IService;

namespace schand.Controllers
{
    
        [Authorize(Roles = "User")]
        [Route("api/[controller]")]
        [ApiController]
        public class MessengerController : ControllerBase
        {
            private readonly IMessageService _messengerService;
        private readonly IHubContext<ChatHub> _hubContext;

        public MessengerController(IMessageService messengerService, IHubContext<ChatHub> hubContext)
        {
            _messengerService = messengerService;
            _hubContext = hubContext;
        }

        // 1. Gửi tin nhắn
        [HttpPost]
        public async Task<IActionResult> SendMessage([FromBody] MessageCreateDTO dto)
        {
            var userIdClaim = User.FindFirst("UserId")?.Value;
            if (userIdClaim == null)
                return Unauthorized("Không tìm thấy thông tin người dùng.");

            int senderId = int.Parse(userIdClaim);

            // Gửi và lưu tin nhắn vào database
            await _messengerService.SendMessageAsync(senderId, dto);

            // Gửi tin nhắn realtime tới người nhận
            await _hubContext.Clients.User(dto.ReceiverId.ToString())
                .SendAsync("ReceiveMessage", new
                {
                    SenderId = senderId,
                    ReceiverId = dto.ReceiverId,
                    ProductId = dto.ProductId,
                    Content = dto.Content,
                    SentAt = DateTime.UtcNow
                });

            return Ok();
        }


        // 2. Lấy hội thoại giữa 2 người theo sản phẩm
        [HttpGet]
            public async Task<IActionResult> GetConversation([FromQuery] int userId, [FromQuery] int productId)
            {
                var userIdClaim = User.FindFirst("UserId")?.Value;
                if (userIdClaim == null) return Unauthorized("Không tìm thấy thông tin người dùng.");
                int currentUserId = int.Parse(userIdClaim);
                var messages = await _messengerService.GetConversationAsync(currentUserId, userId, productId);
                return Ok(messages);
            }

            // 3. Lấy danh sách liên hệ đã từng nhắn tin
            [HttpGet("contacts")]
            public async Task<IActionResult> GetContacts()
            {
                var userIdClaim = User.FindFirst("UserId")?.Value;
                if (userIdClaim == null) return Unauthorized("Không tìm thấy thông tin người dùng.");
                int userId = int.Parse(userIdClaim);
                var contacts = await _messengerService.GetContactNamesAsync(userId);
                return Ok(contacts);
            }

            // 4. Xóa tin nhắn nếu là người gửi
            [HttpDelete("{messageId}")]
            public async Task<IActionResult> Delete(int messageId)
            {
                var userIdClaim = User.FindFirst("UserId")?.Value;
                if (userIdClaim == null) return Unauthorized("Không tìm thấy thông tin người dùng.");
                int userId = int.Parse(userIdClaim);
                var success = await _messengerService.DeleteAsync(messageId, userId);
                return success ? NoContent() : Forbid();
            }
        }
    }

