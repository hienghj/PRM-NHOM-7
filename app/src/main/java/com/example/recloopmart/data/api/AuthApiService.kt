package com.example.recloopmart.data.api

import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API Service for Authentication endpoints
 * Base URL: https://your-backend-url/api/Auth
 */
interface AuthApiService {
    
    /**
     * POST /api/Auth/login
     */
    @POST("Auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequestDTO
    ): Response<LoginResponseDTO>
    
    /**
     * POST /api/Auth/register
     */
    @POST("Auth/register")
    suspend fun register(
        @Body registerRequest: RegisterRequestDTO
    ): Response<RegisterResponseDTO>
    
    /**
     * POST /api/Auth/verify-email
     */
    @POST("Auth/verify-email")
    suspend fun verifyOtp(
        @Body verifyRequest: VerifyOtpRequestDTO
    ): Response<String>
    
    /**
     * POST /api/Auth/verify-login-otp
     */
    @POST("Auth/verify-login-otp")
    suspend fun verifyLoginOtp(
        @Body verifyRequest: VerifyOtpRequestDTO
    ): Response<LoginResponseDTO>
    
    /**
     * POST /api/Auth/resend-otp
     */
    @POST("Auth/resend-otp")
    suspend fun resendOtp(
        @Body email: Map<String, String>
    ): Response<ApiResponse<Any>>
    
    /**
     * POST /api/Auth/auto-verify-email (Development only)
     */
    @POST("Auth/auto-verify-email")
    suspend fun autoVerifyEmail(
        @Body email: String
    ): Response<String>
    
    /**
     * GET /api/Auth/profile
     */
    @GET("Auth/profile")
    suspend fun getProfile(): Response<ProfileResponseDTO>
}



