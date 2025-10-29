package com.example.recloopmart.data.repository

import com.example.recloopmart.data.api.LoginResponseDTO
import com.example.recloopmart.data.api.RegisterResponseDTO
import com.example.recloopmart.data.local.UserEntity
import com.example.recloopmart.data.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for Authentication - Room DB Only
 */
class AuthRepository(
    private val userDao: com.example.recloopmart.data.local.UserDao? = null
) {
    
    /**
     * Login user - Room DB only
     */
    suspend fun login(email: String, password: String): Resource<Any> = withContext(Dispatchers.IO) {
        android.util.Log.d("AuthRepository", "Starting login for email: $email")
        
        try {
            // Check if user exists in Room DB
            val user = userDao?.getUserByEmail(email)
            
            if (user != null) {
                android.util.Log.d("AuthRepository", "User found in Room DB: $email")
                android.util.Log.d("AuthRepository", "Stored password: '${user.password}'")
                android.util.Log.d("AuthRepository", "Input password: '$password'")
                android.util.Log.d("AuthRepository", "Passwords match: ${user.password == password}")
                
                // Check password
                if (user.password == null) {
                    android.util.Log.d("AuthRepository", "User password is null (old data), accepting any password")
                    // For old users without password, accept any password
                } else if (user.password != password) {
                    android.util.Log.d("AuthRepository", "Invalid password for: $email")
                    return@withContext Resource.Error("Mật khẩu không đúng")
                }
                
                // Check if email is confirmed
                if (user.isEmailConfirmed) {
                    android.util.Log.d("AuthRepository", "Email confirmed, generating token for: $email")
                    
                    // Generate token for confirmed users
                    val token = "mock_token_${System.currentTimeMillis()}"
                    
                    val response = LoginResponseDTO(
                        token = token,
                        userId = user.id,
                        email = user.email,
                        fullName = user.fullName,
                        role = user.role
                    )
                    
                    return@withContext Resource.Success(response)
                } else {
                    android.util.Log.d("AuthRepository", "Email not confirmed, generating OTP for: $email")
                    
                    // Generate OTP for unconfirmed users
                    val otp = (100000..999999).random().toString()
                    android.util.Log.d("AuthRepository", "Generated OTP for login: $otp")
                    android.util.Log.d("AuthRepository", "📧 OTP sent to email: $email")
                    
                    return@withContext Resource.Success(mapOf(
                        "requiresOtp" to true,
                        "email" to email,
                        "userId" to user.id,
                        "message" to "Vui lòng nhập mã OTP để hoàn tất đăng nhập."
                    ))
                }
            } else {
                android.util.Log.d("AuthRepository", "User not found: $email")
                return@withContext Resource.Error("Tài khoản không tồn tại")
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Login failed", e)
            return@withContext Resource.Error("Đăng nhập thất bại: ${e.message}")
        }
    }
    
    /**
     * Register user - Room DB only
     */
    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String?
    ): Resource<Any> = withContext(Dispatchers.IO) {
        android.util.Log.d("AuthRepository", "Starting registration for email: $email")
        
        try {
            // Generate OTP
            val otp = (100000..999999).random().toString()
            android.util.Log.d("AuthRepository", "Generated OTP: $otp")
            
            // Save user to Room database
            val userEntity = UserEntity(
                email = email,
                password = password,
                fullName = fullName,
                phoneNumber = phoneNumber,
                address = null,
                avatarUrl = "https://cdn-icons-png.flaticon.com/512/7747/7747940.png",
                createdAt = System.currentTimeMillis().toString(),
                role = "User",
                isEmailConfirmed = false
            )
            userDao?.insertUser(userEntity)
            android.util.Log.d("AuthRepository", "User data saved to Room database: $email")
            
            // Simulate email sending
            android.util.Log.d("AuthRepository", "📧 OTP sent to email: $email")
            android.util.Log.d("AuthRepository", "📧 OTP: $otp")
            
            // Return success response
            val response = RegisterResponseDTO(
                message = "Đăng ký thành công. Vui lòng kiểm tra email để lấy mã xác thực.",
                userId = 1,
            email = email,
            isEmailConfirmed = false
        )
        
            return@withContext Resource.Success(response)
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Registration failed", e)
            return@withContext Resource.Error("Đăng ký thất bại: ${e.message}")
        }
    }
    
    /**
     * Verify OTP - Room DB only
     */
    suspend fun verifyOtp(email: String, otp: String): Resource<Any> = withContext(Dispatchers.IO) {
        android.util.Log.d("AuthRepository", "Starting OTP verification for email: $email")
        
        try {
            // Simulate OTP verification (accept any 6-digit OTP)
            if (otp.length == 6 && otp.all { it.isDigit() }) {
                android.util.Log.d("AuthRepository", "OTP verification successful")
                
                // Update user to set isEmailConfirmed = true
                val user = userDao?.getUserByEmail(email)
                if (user != null) {
                    val updatedUser = user.copy(isEmailConfirmed = true)
                    userDao?.updateUser(updatedUser)
                    android.util.Log.d("AuthRepository", "User email confirmed: $email")
                }
                
                // Generate mock token
                val token = "mock_token_${System.currentTimeMillis()}"
                
                val response = LoginResponseDTO(
                    token = token,
                    userId = user?.id ?: 1,
                            email = email,
                    fullName = user?.fullName ?: "User",
                    role = user?.role ?: "User"
                )
                
                return@withContext Resource.Success(response)
            } else {
                android.util.Log.d("AuthRepository", "Invalid OTP format")
                return@withContext Resource.Error("Mã OTP không hợp lệ")
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "OTP verification failed", e)
            return@withContext Resource.Error("Xác thực OTP thất bại: ${e.message}")
        }
    }
    
    /**
     * Verify Login OTP - Room DB only
     */
    suspend fun verifyLoginOtp(email: String, otp: String): Resource<Any> = withContext(Dispatchers.IO) {
        android.util.Log.d("AuthRepository", "Starting login OTP verification for email: $email")
        
        try {
            // Simulate OTP verification (accept any 6-digit OTP)
            if (otp.length == 6 && otp.all { it.isDigit() }) {
                android.util.Log.d("AuthRepository", "Login OTP verification successful")
                
                // Update user to set isEmailConfirmed = true
                val user = userDao?.getUserByEmail(email)
                if (user != null) {
                    val updatedUser = user.copy(isEmailConfirmed = true)
                    userDao?.updateUser(updatedUser)
                    android.util.Log.d("AuthRepository", "User email confirmed: $email")
                }
                
                // Generate mock token
                val token = "mock_token_${System.currentTimeMillis()}"
                
                val response = LoginResponseDTO(
                    token = token,
                    userId = user?.id ?: 1,
                email = email,
                    fullName = user?.fullName ?: "User",
                    role = user?.role ?: "User"
                )
                
                return@withContext Resource.Success(response)
            } else {
                android.util.Log.d("AuthRepository", "Invalid OTP format")
                return@withContext Resource.Error("Mã OTP không hợp lệ")
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Login OTP verification failed", e)
            return@withContext Resource.Error("Xác thực OTP thất bại: ${e.message}")
        }
    }
    
    /**
     * Resend OTP - Room DB only
     */
    suspend fun resendOtp(email: String): Resource<Any> = withContext(Dispatchers.IO) {
        android.util.Log.d("AuthRepository", "Resending OTP for email: $email")
        
        try {
            // Generate new OTP
            val otp = (100000..999999).random().toString()
            android.util.Log.d("AuthRepository", "Generated new OTP: $otp")
            android.util.Log.d("AuthRepository", "📧 New OTP sent to email: $email")
            
            return@withContext Resource.Success(mapOf(
                "message" to "Mã OTP mới đã được gửi đến email của bạn.",
                "otp" to otp
            ))
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Resend OTP failed", e)
            return@withContext Resource.Error("Gửi lại OTP thất bại: ${e.message}")
        }
    }
    
    /**
     * Clear all users - For testing purposes
     */
    suspend fun clearAllUsers(): Resource<Any> = withContext(Dispatchers.IO) {
        try {
            userDao?.deleteAllUsers()
            android.util.Log.d("AuthRepository", "All users cleared from database")
            return@withContext Resource.Success("Database cleared")
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Clear users failed", e)
            return@withContext Resource.Error("Clear users failed: ${e.message}")
        }
    }
}