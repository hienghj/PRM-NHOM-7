package com.example.recloopmart

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.appcompat.app.AppCompatActivity
import com.example.recloopmart.databinding.ActivityRegisterBinding
import com.example.recloopmart.data.network.Resource
import com.example.recloopmart.util.toast
import com.example.recloopmart.util.PasswordValidator
import com.example.recloopmart.viewmodel.RegisterViewModel
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import java.util.regex.Pattern

/**
 * RegisterActivity - Đăng ký tài khoản mới
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by lazy {
        ViewModelProvider(this, RegisterViewModel.Factory(application))[RegisterViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Register button
        binding.btnRegister.setOnClickListener {
            handleRegister()
        }

        // Back to login
        binding.tvLoginLink?.setOnClickListener {
            finish()
        }

        // Clear errors on focus
        binding.etFullName?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilFullName?.error = null
        }
        binding.etEmail?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilEmail?.error = null
        }
        binding.etPassword?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilPassword?.error = null
        }
        binding.etConfirmPassword?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilConfirmPassword?.error = null
        }
        
        // Password strength monitoring
        binding.etPassword?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updatePasswordStrength(s?.toString() ?: "")
            }
        })
    }

    private fun observeViewModel() {
        viewModel.registerResult.observe(this) { resource ->
            android.util.Log.d("RegisterActivity", "Received register result: $resource")
            when (resource) {
                is Resource.Success -> {
                    showLoading(false)
                    android.util.Log.d("RegisterActivity", "Registration successful, navigating to OTP verification")
                    
                    // Show success message
                    toast("Đăng ký thành công! Vui lòng kiểm tra email để lấy mã OTP.")
                    
                    // Navigate to OTP verification with delay to ensure UI updates
                    val email = binding.etEmail?.text.toString().trim()
                    android.util.Log.d("RegisterActivity", "Email for OTP: $email")
                    
                    // Use postDelayed to ensure UI thread is ready
                    binding.root.postDelayed({
                        try {
                            val intent = Intent(this, OtpVerifyActivity::class.java)
                            intent.putExtra("email", email)
                            intent.putExtra("action", "register")
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                            android.util.Log.d("RegisterActivity", "Successfully navigated to OTP verification")
                        } catch (e: Exception) {
                            android.util.Log.e("RegisterActivity", "Error navigating to OTP verification", e)
                            toast("Đăng ký thành công nhưng có lỗi khi chuyển trang. Vui lòng thử lại.")
                        }
                    }, 500) // 500ms delay
                }
                is Resource.Error -> {
                    showLoading(false)
                    android.util.Log.e("RegisterActivity", "Registration failed: ${resource.message}")
                    toast(resource.message ?: "Đăng ký thất bại. Vui lòng kiểm tra kết nối mạng và thử lại.")
                }
                is Resource.Loading -> {
                    showLoading(true)
                    android.util.Log.d("RegisterActivity", "Registration in progress...")
                }
                null -> {
                    android.util.Log.d("RegisterActivity", "Register result is null")
                }
            }
        }

        viewModel.loading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun handleRegister() {
        val fullName = binding.etFullName?.text.toString().trim()
        val email = binding.etEmail?.text.toString().trim()
        // TODO: Add etPhone to layout
        val phone = "" // binding.etPhone.text.toString().trim()
        val password = binding.etPassword?.text.toString()
        val confirmPassword = binding.etConfirmPassword?.text.toString()

        // Validation
        when {
            fullName.isEmpty() -> {
                binding.tilFullName?.error = "Tên không được để trống"
                return
            }
            email.isEmpty() -> {
                binding.tilEmail?.error = "Email không được để trống"
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.tilEmail?.error = "Email không hợp lệ"
                return
            }
            password.isEmpty() -> {
                binding.tilPassword?.error = "Mật khẩu không được để trống"
                return
            }
            password != confirmPassword -> {
                binding.tilConfirmPassword?.error = "Mật khẩu không khớp"
                return
            }
        }
        
        // Validate password using PasswordValidator
        val passwordValidation = PasswordValidator.validatePassword(password)
        if (!passwordValidation.isValid) {
            binding.tilPassword?.error = passwordValidation.errors.firstOrNull()
            return
        }

        // Call ViewModel
        android.util.Log.d("RegisterActivity", "Calling viewModel.register with email: $email")
        viewModel.register(email, password, fullName, phone.ifEmpty { null })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
    }
    
    /**
     * Update password strength indicator
     */
    private fun updatePasswordStrength(password: String) {
        if (password.isEmpty()) {
            binding.llPasswordStrength?.visibility = View.GONE
            return
        }
        
        binding.llPasswordStrength?.visibility = View.VISIBLE
        
        val validation = PasswordValidator.validatePassword(password)
        
        // Update strength bars
        updateStrengthBars(validation.strength)
        
        // Update strength text
        binding.tvStrengthText?.text = validation.strength.displayName
        binding.tvStrengthText?.setTextColor(Color.parseColor(validation.strength.color))
        
        // Update requirements
        updateRequirements(password)
    }
    
    /**
     * Update password strength bars
     */
    private fun updateStrengthBars(strength: PasswordValidator.PasswordStrength) {
        val bars = listOf(
            binding.strengthBar1,
            binding.strengthBar2,
            binding.strengthBar3,
            binding.strengthBar4,
            binding.strengthBar5
        )
        
        val colors = listOf(
            Color.parseColor("#FF5252"), // Red
            Color.parseColor("#FF9800"), // Orange
            Color.parseColor("#FFC107"), // Yellow
            Color.parseColor("#4CAF50"), // Green
            Color.parseColor("#2E7D32")  // Dark Green
        )
        
        val activeBars = when (strength) {
            PasswordValidator.PasswordStrength.WEAK -> 1
            PasswordValidator.PasswordStrength.FAIR -> 2
            PasswordValidator.PasswordStrength.GOOD -> 3
            PasswordValidator.PasswordStrength.STRONG -> 4
            PasswordValidator.PasswordStrength.VERY_STRONG -> 5
        }
        
        bars.forEachIndexed { index, bar ->
            if (index < activeBars) {
                bar?.setBackgroundColor(colors[index])
            } else {
                bar?.setBackgroundColor(Color.parseColor("#E5E5EA"))
            }
        }
    }
    
    /**
     * Update password requirements status
     */
    private fun updateRequirements(password: String) {
        val uppercasePattern = Pattern.compile("[A-Z]")
        val lowercasePattern = Pattern.compile("[a-z]")
        val digitPattern = Pattern.compile("[0-9]")
        val specialPattern = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]")
        
        // Update requirement indicators
        updateRequirementIndicator(
            binding.tvReqLength,
            password.length >= 8
        )
        
        updateRequirementIndicator(
            binding.tvReqUppercase,
            uppercasePattern.matcher(password).find()
        )
        
        updateRequirementIndicator(
            binding.tvReqLowercase,
            lowercasePattern.matcher(password).find()
        )
        
        updateRequirementIndicator(
            binding.tvReqDigit,
            digitPattern.matcher(password).find()
        )
        
        updateRequirementIndicator(
            binding.tvReqSpecial,
            specialPattern.matcher(password).find()
        )
    }
    
    /**
     * Update individual requirement indicator
     */
    private fun updateRequirementIndicator(textView: android.widget.TextView?, isValid: Boolean) {
        textView?.let { tv ->
            if (isValid) {
                tv.setTextColor(Color.parseColor("#4CAF50"))
                tv.setCompoundDrawablesWithIntrinsicBounds(
                    getDrawable(com.example.recloopmart.R.drawable.ic_success),
                    null, null, null
                )
            } else {
                tv.setTextColor(Color.parseColor("#FF5252"))
                tv.setCompoundDrawablesWithIntrinsicBounds(
                    getDrawable(com.example.recloopmart.R.drawable.ic_error),
                    null, null, null
                )
            }
        }
    }
}
