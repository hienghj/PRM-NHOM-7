package com.example.recloopmart.data.repository

import com.example.recloopmart.data.api.ApiResponse
import com.example.recloopmart.data.api.AuthApiService
import com.example.recloopmart.data.api.LoginRequestDTO
import com.example.recloopmart.data.api.LoginResponseDTO
import com.example.recloopmart.data.api.RegisterRequestDTO
import com.example.recloopmart.data.api.RegisterResponseDTO
import com.example.recloopmart.data.api.VerifyOtpRequestDTO
import com.example.recloopmart.data.network.ApiClient
import com.example.recloopmart.data.network.NetworkUtils
import com.example.recloopmart.data.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthApiRepository(
    private val authApi: AuthApiService = ApiClient.authApi
) {
    suspend fun login(email: String, password: String): Resource<LoginResponseDTO> = withContext(Dispatchers.IO) {
        android.util.Log.d("AuthApiRepo", "Login: $email")
        val request = LoginRequestDTO(email, password)
        val result: Resource<LoginResponseDTO> = NetworkUtils.safeApiCall({ authApi.login(request) })
        when (result) {
            is Resource.Success -> Resource.Success(result.data!!)
            is Resource.Error -> Resource.Error(result.message ?: "Login failed")
            is Resource.Loading -> Resource.Loading()
        }
    }
    
    suspend fun register(email: String, password: String, fullName: String, phone: String?): Resource<RegisterResponseDTO> = withContext(Dispatchers.IO) {
        android.util.Log.d("AuthApiRepo", "Register: $email")
        val request = RegisterRequestDTO(email, password, fullName, phone)
        val result: Resource<RegisterResponseDTO> = NetworkUtils.safeApiCall({ authApi.register(request) })
        when (result) {
            is Resource.Success -> Resource.Success(result.data!!)
            is Resource.Error -> Resource.Error(result.message ?: "Register failed")
            is Resource.Loading -> Resource.Loading()
        }
    }
    
    suspend fun verifyOtp(email: String, otp: String): Resource<String> = withContext(Dispatchers.IO) {
        android.util.Log.d("AuthApiRepo", "VerifyOTP: $email")
        val request = VerifyOtpRequestDTO(email, otp)
        val result: Resource<String> = NetworkUtils.safeApiCall({ authApi.verifyOtp(request) })
        when (result) {
            is Resource.Success -> Resource.Success(result.data ?: "Verified")
            is Resource.Error -> Resource.Error(result.message ?: "Verify failed")
            is Resource.Loading -> Resource.Loading()
        }
    }
    
    suspend fun resendOtp(email: String): Resource<String> = withContext(Dispatchers.IO) {
        android.util.Log.d("AuthApiRepo", "ResendOTP: $email")
        val request = mapOf("email" to email)
        val result: Resource<ApiResponse<Any>> = NetworkUtils.safeApiCall({ authApi.resendOtp(request) })
        when (result) {
            is Resource.Success -> Resource.Success(result.data?.message ?: "Sent")
            is Resource.Error -> Resource.Error(result.message ?: "Resend failed")
            is Resource.Loading -> Resource.Loading()
        }
    }
    
    suspend fun verifyLoginOtp(email: String, otp: String): Resource<LoginResponseDTO> = withContext(Dispatchers.IO) {
        android.util.Log.d("AuthApiRepo", "VerifyLoginOTP: $email")
        val request = VerifyOtpRequestDTO(email, otp)
        val result: Resource<LoginResponseDTO> = NetworkUtils.safeApiCall({ authApi.verifyLoginOtp(request) })
        when (result) {
            is Resource.Success -> Resource.Success(result.data!!)
            is Resource.Error -> Resource.Error(result.message ?: "Verify login OTP failed")
            is Resource.Loading -> Resource.Loading()
        }
    }
}

