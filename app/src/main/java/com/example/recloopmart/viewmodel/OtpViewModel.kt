package com.example.recloopmart.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recloopmart.data.local.AppDatabase
import com.example.recloopmart.data.api.LoginResponseDTO
import com.example.recloopmart.data.network.Resource
import com.example.recloopmart.data.repository.AuthRepository
import com.example.recloopmart.data.repository.AuthApiRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for OTP Verification - Use REAL API backend
 */
class OtpViewModel(application: Application) : AndroidViewModel(application) {
    
    // Use Room DB for local data storage
    private val authRepository = AuthRepository(
        AppDatabase.getDatabase(application).userDao()
    )
    
    // Use API for sending real OTP emails
    private val authApiRepository = AuthApiRepository()
    
    private val _verifyResult = MutableLiveData<Resource<Any>>()
    val verifyResult: LiveData<Resource<Any>> = _verifyResult
    
    private val _resendResult = MutableLiveData<Resource<Any>>()
    val resendResult: LiveData<Resource<Any>> = _resendResult
    
    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading
    
    /**
     * Verify OTP for registration - Use REAL API
     */
    fun verifyOtp(email: String, otp: String) {
        viewModelScope.launch {
            _loading.value = true
            android.util.Log.d("OtpViewModel", "🔄 Calling API to verify OTP...")
            
            val result = authApiRepository.verifyOtp(email, otp)
            
            _verifyResult.value = result as Resource<Any>
            _loading.value = false
        }
    }
    
    /**
     * Verify OTP for login - Use REAL API
     */
    fun verifyLoginOtp(email: String, otp: String) {
        viewModelScope.launch {
            _loading.value = true
            android.util.Log.d("OtpViewModel", "🔄 Calling API to verify login OTP...")
            
            val result = authApiRepository.verifyLoginOtp(email, otp)
            _verifyResult.value = result as Resource<Any>
            _loading.value = false
        }
    }
    
    /**
     * Resend OTP - Use REAL API
     */
    fun resendOtp(email: String) {
        viewModelScope.launch {
            _loading.value = true
            android.util.Log.d("OtpViewModel", "🔄 Calling API to resend OTP...")
            
            val result = authApiRepository.resendOtp(email)
            _resendResult.value = result as Resource<Any>
            _loading.value = false
        }
    }
}
