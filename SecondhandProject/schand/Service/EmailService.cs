using MailKit.Net.Smtp;
using MailKit.Security;
using MimeKit;

namespace schand.Service
{
    public class EmailService
    {
        private readonly IConfiguration _config;
        private readonly ILogger<EmailService> _logger;

        public EmailService(IConfiguration config, ILogger<EmailService> logger)
        {
            _config = config;
            _logger = logger;
        }

        public async Task SendOtpEmail(string toEmail, string otp)
        {
            var subject = "Mã xác thực đăng ký tài khoản";
            var body = $@"
                <html>
                <body style='font-family: Arial, sans-serif; background-color: #f5f5f5;'>
                    <div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>
                        <h2 style='color: #4CAF50; text-align: center;'>RecloopMart - Xác thực tài khoản</h2>
                        <hr style='border: none; border-top: 2px solid #4CAF50;'/>
                        
                        <p>Xin chào,</p>
                        
                        <p>Cảm ơn bạn đã đăng ký tài khoản RecloopMart. Để hoàn thành quá trình đăng ký, vui lòng sử dụng mã xác thực bên dưới:</p>
                        
                        <div style='text-align: center; margin: 30px 0;'>
                            <div style='background-color: #f9f9f9; padding: 20px; border-radius: 8px; border: 2px dashed #4CAF50;'>
                                <h1 style='color: #2196F3; letter-spacing: 5px; margin: 0;'>{otp}</h1>
                            </div>
                        </div>
                        
                        <p style='color: #666;'><strong>Lưu ý:</strong></p>
                        <ul style='color: #666;'>
                            <li>Mã xác thực sẽ hết hạn trong <strong>5 phút</strong></li>
                            <li>Không chia sẻ mã này cho ai khác</li>
                            <li>RecloopMart sẽ không bao giờ yêu cầu mã này qua email hoặc tin nhắn</li>
                        </ul>
                        
                        <p style='color: #666;'>Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này. Tài khoản của bạn sẽ vẫn an toàn.</p>
                        
                        <hr style='border: none; border-top: 1px solid #ddd; margin: 30px 0;'/>
                        
                        <p style='text-align: center; color: #999; font-size: 12px;'>
                            Trân trọng,<br/>
                            <strong>Đội ngũ RecloopMart</strong><br/>
                            <a href='#' style='color: #4CAF50; text-decoration: none;'>www.recloopmart.com</a>
                        </p>
                    </div>
                </body>
                </html>
            ";

            // Always log OTP for development/testing
            Console.WriteLine($"\n╔══════════════════════════════════════════════════════════╗");
            Console.WriteLine($"║            OTP EMAIL DEBUG - RECLOOPMART                 ║");
            Console.WriteLine($"╠══════════════════════════════════════════════════════════╣");
            Console.WriteLine($"║  To Email:  {toEmail,-44} ║");
            Console.WriteLine($"║  OTP Code:  {otp,-44} ║");
            Console.WriteLine($"║  Expires:   5 minutes from now                           ║");
            Console.WriteLine($"╚══════════════════════════════════════════════════════════╝\n");

            try
            {
                var emailUsername = _config["Email:Username"];
                var emailPassword = _config["Email:Password"];

                // Validate email configuration
                if (string.IsNullOrEmpty(emailUsername) || string.IsNullOrEmpty(emailPassword))
                {
                    Console.WriteLine("⚠️  WARNING: Gmail credentials not configured in appsettings.json");
                    Console.WriteLine("⚠️  Email will NOT be sent. Use OTP from console log above.");
                    _logger.LogWarning("Gmail credentials not configured");
                    return;
                }

                Console.WriteLine($"📧 Attempting to send email via Gmail SMTP...");
                Console.WriteLine($"   SMTP Server: smtp.gmail.com:587");
                Console.WriteLine($"   From: {emailUsername}");
                Console.WriteLine($"   To: {toEmail}");

                using var client = new SmtpClient();
                
                // Connect to Gmail SMTP server
                await client.ConnectAsync("smtp.gmail.com", 587, SecureSocketOptions.StartTls);
                Console.WriteLine("✓  Connected to Gmail SMTP server");
                
                // Authenticate with Gmail
                await client.AuthenticateAsync(emailUsername, emailPassword);
                Console.WriteLine("✓  Gmail authentication successful");
                
                // Create MIME message
                var message = new MimeMessage();
                message.From.Add(new MailboxAddress("RecloopMart", emailUsername));
                message.To.Add(new MailboxAddress("", toEmail));
                message.Subject = subject;
                message.Body = new TextPart("html") { Text = body };
                
                // Send email
                await client.SendAsync(message);
                Console.WriteLine($"✓  Email sent successfully to {toEmail}");
                
                // Disconnect from server
                await client.DisconnectAsync(true);
                Console.WriteLine("✓  Disconnected from Gmail SMTP server\n");
                
                _logger.LogInformation($"OTP email sent successfully to {toEmail}");
            }
            catch (MailKit.Security.AuthenticationException authEx)
            {
                Console.WriteLine($"\n❌ GMAIL AUTHENTICATION FAILED:");
                Console.WriteLine($"   Error: {authEx.Message}");
                Console.WriteLine($"   Solution: Check Email:Username and Email:Password in appsettings.json");
                Console.WriteLine($"   Note: Gmail requires App Password (not regular password)");
                Console.WriteLine($"   How to setup:");
                Console.WriteLine($"     1. Enable 2-Factor Authentication: https://myaccount.google.com/security");
                Console.WriteLine($"     2. Create App Password: https://myaccount.google.com/apppasswords");
                Console.WriteLine($"     3. Select 'Mail' and 'Windows Computer'");
                Console.WriteLine($"     4. Copy the 16-character password to appsettings.json");
                Console.WriteLine($"   ⚠️  Registration will continue - Use OTP from console log above.\n");
                _logger.LogError(authEx, "Gmail authentication failed");
            }
            catch (System.Net.Sockets.SocketException socketEx)
            {
                Console.WriteLine($"\n❌ NETWORK ERROR:");
                Console.WriteLine($"   Error: {socketEx.Message}");
                Console.WriteLine($"   Solution: Check internet connection and firewall settings");
                Console.WriteLine($"   Note: Gmail SMTP port 587 must be accessible");
                Console.WriteLine($"   ⚠️  Registration will continue - Use OTP from console log above.\n");
                _logger.LogError(socketEx, "Network error connecting to Gmail");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"\n❌ EMAIL SENDING FAILED:");
                Console.WriteLine($"   Error Type: {ex.GetType().Name}");
                Console.WriteLine($"   Message: {ex.Message}");
                if (ex.InnerException != null)
                {
                    Console.WriteLine($"   Inner Error: {ex.InnerException.Message}");
                }
                Console.WriteLine($"   ⚠️  Registration will continue - Use OTP from console log above.\n");
                _logger.LogError(ex, "Failed to send OTP email via Gmail");
            }
        }
    }

}
