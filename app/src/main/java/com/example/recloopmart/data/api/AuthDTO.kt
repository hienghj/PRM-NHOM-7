package com.example.recloopmart.data.api

import com.google.gson.annotations.SerializedName

/**
 * Login Request DTO
 * POST /api/Auth/login
 */
data class LoginRequestDTO(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String
)

/**
 * Login Response DTO
 */
data class LoginResponseDTO(
    @SerializedName("token")
    val token: String?,
    
    @SerializedName("userId")
    val userId: Int?,
    
    @SerializedName("email")
    val email: String?,
    
    @SerializedName("fullName")
    val fullName: String?,
    
    @SerializedName("role")
    val role: String?,
    
    @SerializedName("requiresOtp")
    val requiresOtp: Boolean? = null,
    
    @SerializedName("message")
    val message: String? = null
)

/**
 * Register Request DTO
 * POST /api/Auth/register
 */
data class RegisterRequestDTO(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String,
    
    @SerializedName("fullName")
    val fullName: String,
    
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    
    @SerializedName("gender")
    val gender: String? = null,
    
    @SerializedName("dateOfBirth")
    val dateOfBirth: String? = null,
    
    @SerializedName("address")
    val address: String? = null
)

/**
 * OTP Verification Request DTO
 */
data class VerifyOtpRequestDTO(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("otp")
    val otp: String
)

/**
 * Register Response DTO
 */
data class RegisterResponseDTO(
    @SerializedName("message")
    val message: String,
    
    @SerializedName("userId")
    val userId: Int?,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("isEmailConfirmed")
    val isEmailConfirmed: Boolean = false
)

/**
 * Profile Response DTO
 */
data class ProfileResponseDTO(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("fullName")
    val fullName: String?,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("address")
    val address: String?,
    
    @SerializedName("dateOfBirth")
    val dateOfBirth: String?,
    
    @SerializedName("gender")
    val gender: String?,
    
    @SerializedName("phoneNumber")
    val phoneNumber: String?
)

/**
 * Generic API Response
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String?,
    
    @SerializedName("data")
    val data: T?
)



