package com.example.recloopmart.util

import android.content.Context
import android.content.SharedPreferences
import com.example.recloopmart.data.api.LoginResponseDTO

/**
 * Manager for SharedPreferences
 * Handles storing and retrieving user session data
 */
class SharedPrefsManager(context: Context) {
    
    private val sharedPrefs: SharedPreferences = 
        context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
    
    /**
     * Save login response
     */
    fun saveLoginData(loginResponse: LoginResponseDTO) {
        sharedPrefs.edit().apply {
            loginResponse.token?.let { putString(Constants.KEY_AUTH_TOKEN, it) }
            loginResponse.userId?.let { putInt(Constants.KEY_USER_ID, it) }
            loginResponse.email?.let { putString(Constants.KEY_USER_EMAIL, it) }
            loginResponse.fullName?.let { putString(Constants.KEY_USER_NAME, it) }
            putBoolean(Constants.KEY_IS_LOGGED_IN, true)
            apply()
        }
        
        // Also set token in ApiClient for network requests
        loginResponse.token?.let { 
            com.example.recloopmart.data.network.ApiClient.setAuthToken(it)
            android.util.Log.d("SharedPrefsManager", "Token set in ApiClient: ${it.take(20)}...")
        }
    }
    
    /**
     * Save auth token only
     */
    fun saveToken(token: String) {
        sharedPrefs.edit().apply {
            putString(Constants.KEY_AUTH_TOKEN, token)
            putBoolean(Constants.KEY_IS_LOGGED_IN, true)
            apply()
        }
        
        // Also set token in ApiClient for network requests
        com.example.recloopmart.data.network.ApiClient.setAuthToken(token)
        android.util.Log.d("SharedPrefsManager", "Token set in ApiClient: ${token.take(20)}...")
    }
    
    /**
     * Get auth token
     */
    fun getAuthToken(): String? {
        return sharedPrefs.getString(Constants.KEY_AUTH_TOKEN, null)
    }
    
    /**
     * Get user ID
     */
    fun getUserId(): Int {
        return sharedPrefs.getInt(Constants.KEY_USER_ID, -1)
    }
    
    /**
     * Get user email
     */
    fun getUserEmail(): String? {
        return sharedPrefs.getString(Constants.KEY_USER_EMAIL, null)
    }
    
    /**
     * Get user name
     */
    fun getUserName(): String? {
        return sharedPrefs.getString(Constants.KEY_USER_NAME, null)
    }
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return sharedPrefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false)
    }
    
    /**
     * Clear all login data (logout)
     */
    fun clearLoginData() {
        sharedPrefs.edit().apply {
            remove(Constants.KEY_AUTH_TOKEN)
            remove(Constants.KEY_USER_ID)
            remove(Constants.KEY_USER_EMAIL)
            remove(Constants.KEY_USER_NAME)
            putBoolean(Constants.KEY_IS_LOGGED_IN, false)
            apply()
        }
        
        // Also clear token from ApiClient
        com.example.recloopmart.data.network.ApiClient.setAuthToken(null)
    }
}



