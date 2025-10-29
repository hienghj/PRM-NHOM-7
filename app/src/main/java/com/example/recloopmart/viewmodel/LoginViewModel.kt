package com.example.recloopmart.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recloopmart.data.api.LoginResponseDTO
import com.example.recloopmart.data.local.AppDatabase
import com.example.recloopmart.data.network.Resource
import com.example.recloopmart.data.repository.AuthRepository
import com.example.recloopmart.data.repository.AuthApiRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for Login screen - Updated to use REAL API
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {
    
    // Use Room DB for local data storage
    private val authRepository = AuthRepository(
        AppDatabase.getDatabase(application).userDao()
    )
    
    // Use API for sending real OTP emails
    private val authApiRepository = AuthApiRepository()
    
    private val _loginResult = MutableLiveData<Resource<Any>>()
    val loginResult: LiveData<Resource<Any>> = _loginResult
    
    private val _otpVerificationResult = MutableLiveData<Resource<Any>>()
    val otpVerificationResult: LiveData<Resource<Any>> = _otpVerificationResult
    
    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading
    
    private var _pendingEmail: String? = null
    val pendingEmail: String? get() = _pendingEmail
    
    /**
     * Login user - UPDATED to use REAL API backend
     */
    fun login(email: String, password: String) {
        // Validate input
        if (email.isBlank() || password.isBlank()) {
            _loginResult.value = Resource.Error("Email and password are required")
            return
        }
        
        viewModelScope.launch {
            _loading.value = true
            
            android.util.Log.d("LoginViewModel", "🔄 Calling API to login and send OTP if needed...")
            
            // Call API for login (this will send OTP via email if needed)
            val result = authApiRepository.login(email, password)
            
            _loginResult.value = result as Resource<Any>
            _loading.value = false
        }
    }
    
    /**
     * Verify OTP for login
     */
    fun verifyLoginOtp(email: String, otp: String) {
        if (email.isBlank() || otp.isBlank()) {
            _otpVerificationResult.value = Resource.Error("Email and OTP are required")
            return
        }
        
        viewModelScope.launch {
            _loading.value = true
            
            val result = authRepository.verifyLoginOtp(email, otp)
            _otpVerificationResult.value = result
            
            _loading.value = false
        }
    }
    
    /**
     * Clear login result
     */
    fun clearLoginResult() {
        _loginResult.value = null
    }
    
    /**
     * Clear OTP verification result
     */
    fun clearOtpVerificationResult() {
        _otpVerificationResult.value = null
    }
    
    /**
     * Clear pending email
     */
    fun clearPendingEmail() {
        _pendingEmail = null
    }
}
