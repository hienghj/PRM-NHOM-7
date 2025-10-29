package com.example.recloopmart

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.recloopmart.databinding.ActivityOtpVerifyBinding
import com.example.recloopmart.data.network.Resource
import com.example.recloopmart.util.toast
import com.example.recloopmart.util.SharedPrefsManager
import com.example.recloopmart.viewmodel.OtpViewModel
import com.example.recloopmart.data.api.LoginResponseDTO

/**
 * OtpVerifyActivity - Xác thực OTP
 */
class OtpVerifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpVerifyBinding
    private val viewModel: OtpViewModel by lazy {
        ViewModelProvider(this)[OtpViewModel::class.java]
    }
    private lateinit var email: String
    private var action: String = "register" // Default action
    private lateinit var prefsManager: SharedPrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        prefsManager = SharedPrefsManager(this)
        
        binding = ActivityOtpVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = intent.getStringExtra("email") ?: ""
        action = intent.getStringExtra("action") ?: "register"
        
        if (email.isEmpty()) {
            toast("Email không hợp lệ")
            finish()
            return
        }

        // Update email message based on action
        val emailMessage = when (action) {
            "login" -> "Email của bạn chưa được xác thực.\nVui lòng nhập mã OTP để hoàn tất đăng nhập:\n$email.\nKiểm tra hộp thư đến và thư mục spam."
            else -> "Chúng tôi đã gửi mã OTP gồm 6 chữ số đến\nemail của bạn: $email.\nVui lòng kiểm tra hộp thư đến và cả thư mục spam."
        }
        binding.tvEmailMessage.text = emailMessage

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Verify button
        binding.btnVerify.setOnClickListener {
            handleVerify()
        }

        // Resend OTP
        binding.btnResend.setOnClickListener {
            viewModel.resendOtp(email)
        }

        // Back to login button - ONLY show for registration, hide for login
        if (action == "register") {
            binding.btnBackToLogin.visibility = android.view.View.VISIBLE
            binding.btnBackToLogin.setOnClickListener {
                handleBackToLogin()
            }
        } else {
            // Hide button for login action - user MUST enter OTP
            binding.btnBackToLogin.visibility = android.view.View.GONE
        }
    }

    private fun observeViewModel() {
        // Observe verify result
        viewModel.verifyResult.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    showLoading(false)
                    toast("Xác thực thành công!")
                    
                    // Navigate based on action
                    when (action) {
                        "register" -> {
                            // Navigate to login after successful OTP verification
                            android.util.Log.d("OtpVerifyActivity", "Registration OTP verified, navigating to login")
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        "login" -> {
                            // Navigate to home (login completed)
                            android.util.Log.d("OtpVerifyActivity", "Login OTP verified, navigating to home")
                            
                            // Save login data if it's a LoginResponseDTO
                            if (resource.data is LoginResponseDTO) {
                                val loginResponse = resource.data as LoginResponseDTO
                                if (loginResponse.token != null && loginResponse.userId != null && loginResponse.email != null) {
                                    prefsManager.saveLoginData(loginResponse)
                                    android.util.Log.d("OtpVerifyActivity", "Login data saved successfully")
                                }
                            }
                            
                            val intent = Intent(this, HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        "change_password" -> {
                            // Navigate back to profile or login
                            android.util.Log.d("OtpVerifyActivity", "Change password OTP verified, navigating to login")
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }
                    finish()
                }
                is Resource.Error -> {
                    showLoading(false)
                    toast(resource.message ?: "OTP không hợp lệ")
                }
                is Resource.Loading -> {
                    showLoading(true)
                }
                null -> {}
            }
        }

        // Observe resend OTP result
        viewModel.resendResult.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    toast("OTP đã được gửi lại!")
                }
                is Resource.Error -> {
                    toast(resource.message ?: "Không thể gửi lại OTP")
                }
                else -> {}
            }
        }

        viewModel.loading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun handleVerify() {
        val otp1 = binding.et1.text.toString()
        val otp2 = binding.et2.text.toString()
        val otp3 = binding.et3.text.toString()
        val otp4 = binding.et4.text.toString()
        val otp5 = binding.et5.text.toString()
        val otp6 = binding.et6.text.toString()

        val otp = otp1 + otp2 + otp3 + otp4 + otp5 + otp6

        if (otp.length != 6) {
            toast("Vui lòng nhập đầy đủ 6 số OTP")
            return
        }

        // Use appropriate verification method based on action
        when (action) {
            "login" -> {
                viewModel.verifyLoginOtp(email, otp)
            }
            else -> {
                viewModel.verifyOtp(email, otp)
            }
        }
    }

    private fun handleBackToLogin() {
        android.util.Log.d("OtpVerifyActivity", "User chose to go back to login")
        
        // Navigate back to login screen
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        // Progress bar not available in current layout
        // binding.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnVerify.isEnabled = !isLoading
    }
}



