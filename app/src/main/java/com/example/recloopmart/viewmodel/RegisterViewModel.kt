package com.example.recloopmart.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.recloopmart.data.local.AppDatabase
import com.example.recloopmart.data.network.Resource
import com.example.recloopmart.data.repository.AuthRepository
import com.example.recloopmart.data.repository.AuthApiRepository
import com.example.recloopmart.util.PasswordValidator
import kotlinx.coroutines.launch

/**
 * ViewModel for Register screen - Use Room DB + API for email
 */
class RegisterViewModel(application: Application) : ViewModel() {
    
    // Use Room DB for local data storage
    private val authRepository = AuthRepository(
        userDao = AppDatabase.getDatabase(application).userDao()
    )
    
    // Use API for sending real OTP emails
    private val authApiRepository = AuthApiRepository()
    
    private val _registerResult = MutableLiveData<Resource<Any>>()
    val registerResult: LiveData<Resource<Any>> = _registerResult
    
    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading
    
    private val _autoVerifyResult = MutableLiveData<Resource<String>>()
    val autoVerifyResult: LiveData<Resource<String>> = _autoVerifyResult
    
    /**
     * Register new user
     */
    fun register(email: String, password: String, fullName: String, phoneNumber: String?) {
        android.util.Log.d("RegisterViewModel", "Register called with email: $email, fullName: $fullName")
        
        // Validate input
        if (email.isBlank() || password.isBlank() || fullName.isBlank()) {
            android.util.Log.d("RegisterViewModel", "Validation failed: All fields are required")
            _registerResult.value = Resource.Error("Vui lòng điền đầy đủ thông tin")
            return
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            android.util.Log.d("RegisterViewModel", "Validation failed: Invalid email format")
            _registerResult.value = Resource.Error("Email không hợp lệ")
            return
        }
        
        // Validate password using PasswordValidator
        val passwordValidation = PasswordValidator.validatePassword(password)
        if (!passwordValidation.isValid) {
            android.util.Log.d("RegisterViewModel", "Validation failed: Password validation failed")
            _registerResult.value = Resource.Error(passwordValidation.errors.firstOrNull() ?: "Mật khẩu không hợp lệ")
            return
        }
        
        viewModelScope.launch {
            try {
                android.util.Log.d("RegisterViewModel", "🔄 Calling API to register and send OTP email...")
                _loading.value = true
                _registerResult.value = Resource.Loading()
                
                // Call API to register and send OTP via email
                val apiResult = authApiRepository.register(email, password, fullName, phoneNumber)
                
                when (apiResult) {
                    is Resource.Success -> {
                        // Save to Room DB for local storage
                        authRepository.register(email, password, fullName, phoneNumber)
                        _registerResult.value = apiResult as Resource<Any>
                    }
                    else -> _registerResult.value = apiResult as Resource<Any>
                }
            } catch (e: Exception) {
                android.util.Log.e("RegisterViewModel", "Exception during registration", e)
                _registerResult.value = Resource.Error("Lỗi kết nối. Vui lòng kiểm tra mạng và thử lại.")
            } finally {
                _loading.value = false
                android.util.Log.d("RegisterViewModel", "Registration process completed")
            }
        }
    }
    
    
    /**
     * Factory class for RegisterViewModel
     */
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RegisterViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
