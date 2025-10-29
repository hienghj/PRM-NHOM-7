package com.example.recloopmart.util

import android.content.Context
import com.example.recloopmart.data.network.ApiClient

/**
 * Helper class to debug authentication issues
 */
object AuthDebugHelper {
    
    /**
     * Check authentication status and print detailed logs
     */
    fun checkAuthStatus(context: Context, tag: String = "AuthDebug") {
        android.util.Log.d(tag, "========== AUTH STATUS CHECK ==========")
        
        // 1. Check SharedPreferences
        val prefsManager = SharedPrefsManager(context)
        val tokenFromPrefs = prefsManager.getAuthToken()
        val isLoggedIn = prefsManager.isLoggedIn()
        val userId = prefsManager.getUserId()
        val userEmail = prefsManager.getUserEmail()
        
        android.util.Log.d(tag, "1. SharedPreferences:")
        android.util.Log.d(tag, "   - Is Logged In: $isLoggedIn")
        android.util.Log.d(tag, "   - User ID: $userId")
        android.util.Log.d(tag, "   - User Email: $userEmail")
        if (tokenFromPrefs != null) {
            android.util.Log.d(tag, "   - Token exists: YES (${tokenFromPrefs.length} chars)")
            android.util.Log.d(tag, "   - Token preview: ${tokenFromPrefs.take(30)}...")
            
            // Decode JWT to check claims
            try {
                val parts = tokenFromPrefs.split(".")
                if (parts.size == 3) {
                    val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP))
                    android.util.Log.d(tag, "   - Token payload: $payload")
                }
            } catch (e: Exception) {
                android.util.Log.e(tag, "   - Failed to decode token", e)
            }
        } else {
            android.util.Log.e(tag, "   - Token exists: NO ❌")
        }
        
        // 2. Check ApiClient
        val tokenFromApiClient = ApiClient.getAuthToken()
        android.util.Log.d(tag, "2. ApiClient:")
        if (tokenFromApiClient != null) {
            android.util.Log.d(tag, "   - Token in ApiClient: YES ✓")
            android.util.Log.d(tag, "   - Token preview: ${tokenFromApiClient.take(30)}...")
            
            // Check if tokens match
            if (tokenFromPrefs == tokenFromApiClient) {
                android.util.Log.d(tag, "   - Tokens match: YES ✓")
            } else {
                android.util.Log.e(tag, "   - Tokens match: NO ❌")
                android.util.Log.e(tag, "   - This is a BUG! Token in SharedPrefs and ApiClient are different!")
            }
        } else {
            android.util.Log.e(tag, "   - Token in ApiClient: NO ❌")
            android.util.Log.e(tag, "   - This is why you get 403!")
            
            if (tokenFromPrefs != null) {
                android.util.Log.w(tag, "   - Fix: Token exists in SharedPrefs but not in ApiClient")
                android.util.Log.w(tag, "   - Setting token now...")
                ApiClient.setAuthToken(tokenFromPrefs)
                android.util.Log.d(tag, "   - Token set! Try your request again.")
            }
        }
        
        android.util.Log.d(tag, "========================================")
    }
    
    /**
     * Force reload token from SharedPreferences to ApiClient
     */
    fun forceReloadToken(context: Context): Boolean {
        val prefsManager = SharedPrefsManager(context)
        val token = prefsManager.getAuthToken()
        
        return if (token != null) {
            ApiClient.setAuthToken(token)
            android.util.Log.d("AuthDebug", "✓ Token reloaded into ApiClient")
            true
        } else {
            android.util.Log.e("AuthDebug", "✗ No token in SharedPrefs to reload")
            false
        }
    }
}


