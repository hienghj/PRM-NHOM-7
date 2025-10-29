using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using schand.DTOS.Auth;
using schand.Models;
using schand.Service;

namespace schand.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AuthController : ControllerBase
    {
        private readonly SchuserContext _context;
        private readonly IConfiguration _config;
        private readonly EmailService _emailService;

        public AuthController(SchuserContext context, IConfiguration config, EmailService emailService)
        {
            _context = context;
            _config = config;
            _emailService = emailService;
        }
        [HttpPost("register")]
        [AllowAnonymous]
        public async Task<IActionResult> Register([FromBody] RegisterDTO dto)
        {
            Console.WriteLine($"\n🔵 REGISTRATION REQUEST RECEIVED");
            Console.WriteLine($"   Email: {dto.Email}");
            Console.WriteLine($"   Full Name: {dto.FullName}");
            
            if (_context.Users.Any(u => u.Email == dto.Email))
            {
                Console.WriteLine($"❌ Registration failed: Email already exists");
                return BadRequest("Email đã được sử dụng.");
            }

            var passwordHash = BCrypt.Net.BCrypt.HashPassword(dto.Password);
            var otp = new Random().Next(100000, 999999).ToString();
            
            Console.WriteLine($"✓  Generated OTP: {otp}");
            Console.WriteLine($"✓  Password hashed successfully");

            var user = new User
            {
                Email = dto.Email,
                FullName = dto.FullName,
                PasswordHash = passwordHash,
                Role = "User",
                IsEmailConfirmed = false, // User is NOT confirmed yet
                EmailOtp = otp,
                EmailOtpExpiration = DateTime.UtcNow.AddMinutes(5),
                PhoneNumber = dto.PhoneNumber,
                AvatarUrl = "https://cdn-icons-png.flaticon.com/512/7747/7747940.png",
                Gender = dto.Gender,
                DateOfBirth = dto.DateOfBirth,
                Address = dto.Address,
                TrustScore = 0,
                IsSellerVerified = false,
                IsLocked = false // User can login but needs OTP verification
            };

            try
            {
                _context.Users.Add(user);
                await _context.SaveChangesAsync();
                Console.WriteLine($"✓  User created in database (ID: {user.Id})");
                Console.WriteLine($"✓  User status: IsLocked=false, IsEmailConfirmed=false");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"❌ Database error: {ex.Message}");
                return StatusCode(500, "Lỗi khi tạo tài khoản. Vui lòng thử lại.");
            }

            // Send OTP email (non-blocking, won't fail registration)
            Console.WriteLine($"\n📧 Sending OTP email...");
            try
            {
                await _emailService.SendOtpEmail(user.Email, otp);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"⚠️  Email service error (non-critical): {ex.Message}");
                Console.WriteLine($"⚠️  User can still use OTP from console log");
            }

            Console.WriteLine($"\n✅ REGISTRATION SUCCESSFUL");
            Console.WriteLine($"   User ID: {user.Id}");
            Console.WriteLine($"   Email: {user.Email}");
            Console.WriteLine($"   Next step: OTP verification\n");

            return Ok(new
            {
                message = "Đăng ký thành công. Vui lòng kiểm tra email để lấy mã xác thực.",
                userId = user.Id,
                email = user.Email,
                isEmailConfirmed = false // Explicitly indicate email is not confirmed
            });
        }
        [HttpPost("verify-email")]
        public async Task<IActionResult> VerifyEmail([FromBody] VerifyEmailOtpDTO dto)
        {
            Console.WriteLine($"\n🔵 OTP VERIFICATION REQUEST");
            Console.WriteLine($"   Email: {dto.Email}");
            Console.WriteLine($"   OTP: {dto.Otp}");
            
            var user = _context.Users.FirstOrDefault(u => u.Email == dto.Email);
            if (user == null)
            {
                Console.WriteLine($"❌ User not found");
                return NotFound("Không tìm thấy người dùng.");
            }
            
            if (user.IsEmailConfirmed)
            {
                Console.WriteLine($"⚠️  Email already confirmed");
                return BadRequest("Email đã được xác nhận.");
            }

            // Check OTP validity
            if (user.EmailOtp != dto.Otp)
            {
                Console.WriteLine($"❌ OTP mismatch - Expected: {user.EmailOtp}, Got: {dto.Otp}");
                return BadRequest("Mã OTP không đúng.");
            }
            
            if (user.EmailOtpExpiration < DateTime.UtcNow)
            {
                Console.WriteLine($"❌ OTP expired at {user.EmailOtpExpiration}");
                return BadRequest("Mã OTP đã hết hạn.");
            }

            // OTP verification successful - activate user account
            user.IsEmailConfirmed = true;
            user.IsLocked = false; // Unlock user account
            user.EmailOtp = null;
            user.EmailOtpExpiration = null;

            await _context.SaveChangesAsync();
            
            Console.WriteLine($"✅ OTP VERIFICATION SUCCESSFUL");
            Console.WriteLine($"   User unlocked and email confirmed");
            Console.WriteLine($"   User can now login\n");

            return Ok(new
            {
                message = "Xác thực email thành công.",
                isEmailConfirmed = true,
                userId = user.Id,
                email = user.Email
            });
        }

        [HttpPost("login")]
        [AllowAnonymous]
        public async Task<IActionResult> Login([FromBody] LoginDTO dto)
        {
            Console.WriteLine($"\n🔵 LOGIN REQUEST RECEIVED");
            Console.WriteLine($"   Email: {dto.Email}");
            
            var user = _context.Users.FirstOrDefault(u => u.Email == dto.Email);
            if (user == null || !BCrypt.Net.BCrypt.Verify(dto.Password, user.PasswordHash))
            {
                Console.WriteLine($"❌ Login failed: Invalid credentials");
                return Unauthorized("Email hoặc mật khẩu không đúng.");
            }

            Console.WriteLine($"✓  User found: {user.Email} (ID: {user.Id})");
            Console.WriteLine($"✓  Email confirmed: {user.IsEmailConfirmed}");

            if (user.IsLocked == true)
            {
                Console.WriteLine($"❌ Account locked");
                return Unauthorized("Tài khoản đã bị khóa.");
            }

            // If email is not confirmed, generate new OTP and require verification
            if (!user.IsEmailConfirmed)
            {
                Console.WriteLine($"⚠️  Email not confirmed, generating new OTP for login verification");
                
                var otp = new Random().Next(100000, 999999).ToString();
                user.EmailOtp = otp;
                user.EmailOtpExpiration = DateTime.UtcNow.AddMinutes(5);
                
                await _context.SaveChangesAsync();
                Console.WriteLine($"✓  New OTP generated: {otp}");
                
                // Send OTP email
                Console.WriteLine($"\n📧 Sending OTP email for login verification...");
                try
                {
                    await _emailService.SendOtpEmail(user.Email, otp);
                }
                catch (Exception ex)
                {
                    Console.WriteLine($"⚠️  Email service error (non-critical): {ex.Message}");
                    Console.WriteLine($"⚠️  User can still use OTP from console log");
                }
                
                Console.WriteLine($"✅ OTP sent for login verification\n");
                
                return Ok(new
                {
                    message = "Vui lòng nhập mã OTP để hoàn tất đăng nhập.",
                    requiresOtp = true,
                    email = user.Email,
                    userId = user.Id,
                    isEmailConfirmed = false
                });
            }

            // Email is confirmed, proceed with normal login
            Console.WriteLine($"✓  Email confirmed, proceeding with login");

            var claims = new List<Claim>
    {
       new Claim("UserId", user.Id.ToString()),
            new Claim(ClaimTypes.Name, user.FullName ?? string.Empty),
            new Claim(ClaimTypes.Email, user.Email),
            new Claim(ClaimTypes.Role, user.Role ?? "User"),
            new Claim("email_confirmed", user.IsEmailConfirmed ? "true" : "false"),
            new Claim("is_seller_verified", (user.IsSellerVerified ?? false) ? "true" : "false"),
            new Claim("phone_number", user.PhoneNumber ?? string.Empty),
            new Claim("avatar_url", user.AvatarUrl ?? string.Empty),
            new Claim("gender", user.Gender ?? string.Empty),
            new Claim("date_of_birth", user.DateOfBirth.HasValue ? user.DateOfBirth.Value.ToString("yyyy-MM-dd") : string.Empty),
            new Claim("address", user.Address ?? string.Empty)
    };

            var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_config["Jwt:Key"]));
            var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

            var token = new JwtSecurityToken(
                issuer: _config["Jwt:Issuer"],
                audience: _config["Jwt:Audience"],
                claims: claims,
                expires: DateTime.UtcNow.AddDays(7),
                signingCredentials: creds
            );

            var tokenString = new JwtSecurityTokenHandler().WriteToken(token);

            Console.WriteLine($"✅ LOGIN SUCCESSFUL - Token generated\n");

            return Ok(new
            {
                token = tokenString,
                user = new
                {
                    user.Id,
                    user.FullName,
                    user.Email,
                    user.Role,
                    user.IsEmailConfirmed,
                    user.IsLocked,
                    user.PhoneNumber,
                    user.AvatarUrl,
                    user.Gender,
                    user.DateOfBirth,
                    user.Address,
                    user.TrustScore,
                    user.IsSellerVerified
                }
            });
        }
        [HttpPost("verify-login-otp")]
        [AllowAnonymous]
        public async Task<IActionResult> VerifyLoginOtp([FromBody] VerifyEmailOtpDTO dto)
        {
            Console.WriteLine($"\n🔵 LOGIN OTP VERIFICATION REQUEST");
            Console.WriteLine($"   Email: {dto.Email}");
            Console.WriteLine($"   OTP: {dto.Otp}");
            
            var user = _context.Users.FirstOrDefault(u => u.Email == dto.Email);
            if (user == null)
            {
                Console.WriteLine($"❌ User not found");
                return NotFound("Không tìm thấy người dùng.");
            }
            
            if (user.IsEmailConfirmed)
            {
                Console.WriteLine($"⚠️  Email already confirmed");
                return BadRequest("Email đã được xác nhận.");
            }

            // Check OTP validity
            if (user.EmailOtp != dto.Otp)
            {
                Console.WriteLine($"❌ OTP mismatch - Expected: {user.EmailOtp}, Got: {dto.Otp}");
                return BadRequest("Mã OTP không đúng.");
            }
            
            if (user.EmailOtpExpiration < DateTime.UtcNow)
            {
                Console.WriteLine($"❌ OTP expired at {user.EmailOtpExpiration}");
                return BadRequest("Mã OTP đã hết hạn.");
            }

            // OTP verification successful - activate user account and generate login token
            user.IsEmailConfirmed = true;
            user.EmailOtp = null;
            user.EmailOtpExpiration = null;

            await _context.SaveChangesAsync();
            
            Console.WriteLine($"✅ LOGIN OTP VERIFICATION SUCCESSFUL");
            Console.WriteLine($"   User unlocked and email confirmed");
            
            // Generate JWT token for immediate login
            var claims = new List<Claim>
            {
                new Claim("UserId", user.Id.ToString()),
                new Claim(ClaimTypes.Name, user.FullName ?? string.Empty),
                new Claim(ClaimTypes.Email, user.Email),
                new Claim(ClaimTypes.Role, user.Role ?? "User"),
                new Claim("email_confirmed", "true"),
                new Claim("is_seller_verified", (user.IsSellerVerified ?? false) ? "true" : "false"),
                new Claim("phone_number", user.PhoneNumber ?? string.Empty),
                new Claim("avatar_url", user.AvatarUrl ?? string.Empty),
                new Claim("gender", user.Gender ?? string.Empty),
                new Claim("date_of_birth", user.DateOfBirth.HasValue ? user.DateOfBirth.Value.ToString("yyyy-MM-dd") : string.Empty),
                new Claim("address", user.Address ?? string.Empty)
            };

            var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_config["Jwt:Key"]));
            var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

            var token = new JwtSecurityToken(
                issuer: _config["Jwt:Issuer"],
                audience: _config["Jwt:Audience"],
                claims: claims,
                expires: DateTime.UtcNow.AddDays(7),
                signingCredentials: creds
            );

            var tokenString = new JwtSecurityTokenHandler().WriteToken(token);
            
            Console.WriteLine($"✅ LOGIN TOKEN GENERATED\n");

            return Ok(new
            {
                message = "Xác thực thành công. Đăng nhập hoàn tất.",
                token = tokenString,
                user = new
                {
                    user.Id,
                    user.FullName,
                    user.Email,
                    user.Role,
                    user.IsEmailConfirmed,
                    user.IsLocked,
                    user.PhoneNumber,
                    user.AvatarUrl,
                    user.Gender,
                    user.DateOfBirth,
                    user.Address,
                    user.TrustScore,
                    user.IsSellerVerified
                }
            });
        }

        [HttpPost("resend-otp")]
        public async Task<IActionResult> ResendOtp([FromBody] ResendOtpDTO dto)
        {
            Console.WriteLine($"\n🔵 RESEND OTP REQUEST");
            Console.WriteLine($"   Email: {dto.Email}");
            
            var user = _context.Users.FirstOrDefault(u => u.Email == dto.Email);
            if (user == null)
            {
                Console.WriteLine($"❌ User not found");
                return NotFound("Không tìm thấy người dùng.");
            }
            
            if (user.IsEmailConfirmed)
            {
                Console.WriteLine($"❌ Email already confirmed");
                return BadRequest("Email đã được xác nhận.");
            }

            var otp = new Random().Next(100000, 999999).ToString();
            user.EmailOtp = otp;
            user.EmailOtpExpiration = DateTime.UtcNow.AddMinutes(5);

            await _context.SaveChangesAsync();
            Console.WriteLine($"✓  New OTP generated: {otp}");
            
            // Send OTP email
            Console.WriteLine($"\n📧 Sending OTP email...");
            try
            {
                await _emailService.SendOtpEmail(user.Email, otp);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"⚠️  Email service error (non-critical): {ex.Message}");
            }

            Console.WriteLine($"✅ OTP RESENT SUCCESSFULLY\n");
            return Ok("Đã gửi lại mã xác thực email.");
        }

        [HttpGet("profile")]
        [Authorize]
        public async Task<IActionResult> GetProfile()
        {
            var userIdClaim = User.Claims.FirstOrDefault(c => c.Type == "UserId");
            if (userIdClaim == null) return Unauthorized();

            var userId = int.Parse(userIdClaim.Value);
            var user = await _context.Users.FindAsync(userId);
            if (user == null) return NotFound("Không tìm thấy người dùng.");

            return Ok(new
            {
                user.Id,
                user.FullName,
                user.Email,
                user.Address,
                user.DateOfBirth,
                user.Gender

            });
        }
        [HttpPut("profile")]
        [Authorize]
        public async Task<IActionResult> UpdateProfile([FromBody] UpdateProfileDto model)
        {
            var userIdClaim = User.Claims.FirstOrDefault(c => c.Type == "UserId");
            if (userIdClaim == null) return Unauthorized();

            var userId = int.Parse(userIdClaim.Value);
            var user = await _context.Users.FindAsync(userId);
            if (user == null) return NotFound("Không tìm thấy người dùng.");

            // Cập nhật thông tin
            user.FullName = model.FullName ?? user.FullName;
      
            user.Address = model.Address ?? user.Address;
            user.DateOfBirth = model.DateOfBirth ?? user.DateOfBirth;
            user.Gender = model.Gender ?? user.Gender;

            await _context.SaveChangesAsync();

            return Ok(new
            {
                message = "Cập nhật hồ sơ thành công.",
                user.Id,
                user.FullName,
    
                user.Address,
                user.DateOfBirth,
                user.Gender
            });
        }
        [HttpPost("change-password")]
        [Authorize]
        public async Task<IActionResult> ChangePassword([FromBody] ChangePasswordDTO dto)
        {
            var userIdClaim = User.Claims.FirstOrDefault(c => c.Type == "UserId");
            if (userIdClaim == null) return Unauthorized();

            var userId = int.Parse(userIdClaim.Value);
            var user = await _context.Users.FindAsync(userId);
            if (user == null) return NotFound("Không tìm thấy người dùng.");

            // Kiểm tra mật khẩu cũ
            if (!BCrypt.Net.BCrypt.Verify(dto.OldPassword, user.PasswordHash))
            {
                return BadRequest("Mật khẩu cũ không đúng.");
            }

            user.PasswordHash = BCrypt.Net.BCrypt.HashPassword(dto.NewPassword);
            await _context.SaveChangesAsync();

            return Ok("Đổi mật khẩu thành công.");
        }

        // 🚨 DEVELOPMENT ONLY: Auto-verify email for testing
        [HttpPost("auto-verify-email")]
        [AllowAnonymous]
        public async Task<IActionResult> AutoVerifyEmail([FromBody] string email)
        {
            var user = _context.Users.FirstOrDefault(u => u.Email == email);
            if (user == null) return NotFound("Không tìm thấy người dùng.");
            
            user.IsEmailConfirmed = true;
            user.EmailOtp = null;
            user.EmailOtpExpiration = null;
            
            await _context.SaveChangesAsync();
            
            return Ok("Email đã được xác thực tự động (Development mode).");
        }

        // 🚨 DEVELOPMENT ONLY: Test endpoint
        [HttpGet("test")]
        [AllowAnonymous]
        public IActionResult Test()
        {
            return Ok(new { message = "Backend is working!", timestamp = DateTime.UtcNow });
        }

        // 🚨 DEVELOPMENT ONLY: Test database connection
        [HttpGet("test-db")]
        [AllowAnonymous]
        public async Task<IActionResult> TestDatabase()
        {
            try
            {
                var userCount = await _context.Users.CountAsync();
                return Ok(new { message = "Database connection successful!", userCount = userCount });
            }
            catch (Exception ex)
            {
                return BadRequest(new { message = "Database connection failed!", error = ex.Message });
            }
        }

        // 🚨 DEVELOPMENT ONLY: Test email service
        [HttpPost("test-email")]
        [AllowAnonymous]
        public async Task<IActionResult> TestEmail([FromBody] string email)
        {
            try
            {
                await _emailService.SendOtpEmail(email, "123456");
                return Ok(new { message = "Email sent successfully!" });
            }
            catch (Exception ex)
            {
                return BadRequest(new { message = "Email sending failed!", error = ex.Message });
            }
        }

    }
}
