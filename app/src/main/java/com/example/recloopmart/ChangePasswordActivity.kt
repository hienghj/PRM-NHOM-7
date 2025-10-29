package com.example.recloopmart

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.appcompat.app.AppCompatActivity
import com.example.recloopmart.databinding.ActivityChangePasswordBinding
import com.example.recloopmart.data.network.Resource
import com.example.recloopmart.util.PasswordValidator
import com.example.recloopmart.util.SharedPrefsManager
import com.example.recloopmart.util.toast
import com.example.recloopmart.viewmodel.ChangePasswordViewModel

/**
 * ChangePasswordActivity - Đổi mật khẩu với xác thực OTP
 */
class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private val viewModel: ChangePasswordViewModel by lazy {
        ViewModelProvider(this, ChangePasswordViewModel.Factory(application))[ChangePasswordViewModel::class.java]
    }
    private lateinit var prefsManager: SharedPrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = SharedPrefsManager(this)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Change password button
        binding.btnChangePassword.setOnClickListener {
            handleChangePassword()
        }

        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Clear errors on focus
        binding.etOldPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilOldPassword.error = null
        }
        binding.etNewPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilNewPassword.error = null
        }
        binding.etConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilConfirmPassword.error = null
        }
    }

    private fun observeViewModel() {
        viewModel.changePasswordResult.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    showLoading(false)
                    toast("Mật khẩu đã được thay đổi! Vui lòng kiểm tra email để lấy mã OTP.")
                    
                    // Navigate to OTP verification
                    val email = prefsManager.getUserEmail() ?: ""
                    val intent = Intent(this, OtpVerifyActivity::class.java)
                    intent.putExtra("email", email)
                    intent.putExtra("action", "change_password")
                    startActivity(intent)
                    finish()
                }
                is Resource.Error -> {
                    showLoading(false)
                    toast(resource.message ?: "Đổi mật khẩu thất bại")
                }
                is Resource.Loading -> {
                    showLoading(true)
                }
                null -> {}
            }
        }

        viewModel.loading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun handleChangePassword() {
        val oldPassword = binding.etOldPassword.text.toString().trim()
        val newPassword = binding.etNewPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // Validate input
        if (oldPassword.isEmpty()) {
            binding.tilOldPassword.error = "Vui lòng nhập mật khẩu cũ"
            return
        }

        if (newPassword.isEmpty()) {
            binding.tilNewPassword.error = "Vui lòng nhập mật khẩu mới"
            return
        }

        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.error = "Vui lòng xác nhận mật khẩu mới"
            return
        }

        if (newPassword != confirmPassword) {
            binding.tilConfirmPassword.error = "Mật khẩu xác nhận không khớp"
            return
        }

        // Validate new password using PasswordValidator
        val passwordValidation = PasswordValidator.validatePassword(newPassword)
        if (!passwordValidation.isValid) {
            binding.tilNewPassword.error = passwordValidation.errors.firstOrNull()
            return
        }

        // Call ViewModel to change password
        viewModel.changePassword(oldPassword, newPassword)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnChangePassword.isEnabled = !isLoading
    }
}
