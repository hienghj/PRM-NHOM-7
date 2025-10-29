package com.example.recloopmart

import android.os.Bundle
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.recloopmart.databinding.ActivityMainBinding
import com.example.recloopmart.data.network.Resource
import com.example.recloopmart.util.SharedPrefsManager
import com.example.recloopmart.util.toast
import com.example.recloopmart.viewmodel.LoginViewModel

/**
 * MainActivity - Màn hình đăng nhập
 * Implement theo mô hình MVVM với API backend
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }
    private lateinit var prefsManager: SharedPrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        prefsManager = SharedPrefsManager(this)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Login button
        binding.btnLogin.setOnClickListener {
            handleLogin()
        }

        // Register link
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Clear errors on focus
        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilEmail.error = null
        }

        binding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilPassword.error = null
        }
    }

    private fun observeViewModel() {
        // Observe login result from API
        viewModel.loginResult.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    
                    // Handle both LoginResponseDTO and Map from Room DB
                    when {
                        resource.data is com.example.recloopmart.data.api.LoginResponseDTO -> {
                            val loginResponse = resource.data as com.example.recloopmart.data.api.LoginResponseDTO
                            if (loginResponse.requiresOtp == true) {
                                val email = loginResponse.email ?: ""
                                toast("Vui lòng nhập mã OTP để hoàn tất đăng nhập")
                                val intent = Intent(this, OtpVerifyActivity::class.java)
                                intent.putExtra("email", email)
                                intent.putExtra("action", "login")
                                startActivity(intent)
                                return@observe
                            }
                            if (loginResponse.token != null && loginResponse.userId != null && loginResponse.email != null) {
                                prefsManager.saveLoginData(loginResponse)
                                toast("Đăng nhập thành công!")
                                navigateToHome()
                            }
                        }
                        resource.data is Map<*, *> -> {
                            val data = resource.data as Map<String, Any>
                            if (data["requiresOtp"] == true) {
                                val email = data["email"] as? String ?: ""
                                toast("Vui lòng nhập mã OTP để hoàn tất đăng nhập")
                                val intent = Intent(this, OtpVerifyActivity::class.java)
                                intent.putExtra("email", email)
                                intent.putExtra("action", "login")
                                startActivity(intent)
                                return@observe
                            }
                            toast("Đăng nhập thành công!")
                            navigateToHome()
                        }
                        else -> {
                            // Direct success
                            toast("Đăng nhập thành công!")
                            navigateToHome()
                        }
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    toast(resource.message ?: "Đăng nhập thất bại")
                }
                null -> {
                    showLoading(false)
                }
            }
        }
        
        // Observe OTP verification result
        viewModel.otpVerificationResult.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    showLoading(false)
                    toast("Xác thực OTP thành công!")
                    
                    // Save login data
                    resource.data?.let { loginResponse ->
                        if (loginResponse is com.example.recloopmart.data.api.LoginResponseDTO) {
                            prefsManager.saveLoginData(loginResponse)
                        }
                    }
                    
                    navigateToHome()
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
        
        // Observe loading state
        viewModel.loading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun handleLogin() {
        val email = binding.etEmail.text?.toString()?.trim() ?: ""
        val password = binding.etPassword.text?.toString()?.trim() ?: ""

        // Validate
        when {
            email.isEmpty() -> {
                binding.tilEmail.error = "Email không được để trống"
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.tilEmail.error = "Email không hợp lệ"
                return
            }
            password.isEmpty() -> {
                binding.tilPassword.error = "Mật khẩu không được để trống"
                return
            }
            password.length < 6 -> {
                binding.tilPassword.error = "Mật khẩu phải có ít nhất 6 ký tự"
                return
            }
        }

        // Call ViewModel to login via API
        viewModel.login(email, password)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
