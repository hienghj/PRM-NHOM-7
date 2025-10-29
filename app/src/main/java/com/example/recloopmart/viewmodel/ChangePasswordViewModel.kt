package com.example.recloopmart.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.recloopmart.data.network.Resource
import com.example.recloopmart.data.repository.UserRepository
import android.app.Application
import com.example.recloopmart.data.local.AppDatabase
import kotlinx.coroutines.launch

/**
 * ViewModel for Change Password
 */
class ChangePasswordViewModel(application: Application) : ViewModel() {
    
    private val userRepository = UserRepository(AppDatabase.getDatabase(application).userDao())
    
    private val _changePasswordResult = MutableLiveData<Resource<Unit>>()
    val changePasswordResult: LiveData<Resource<Unit>> = _changePasswordResult
    
    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading
    
    /**
     * Change password
     */
    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            _loading.value = true
            
            val result = userRepository.changePassword(oldPassword, newPassword)
            _changePasswordResult.value = result
            
            _loading.value = false
        }
    }
    
    /**
     * Factory class for ChangePasswordViewModel
     */
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChangePasswordViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ChangePasswordViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
