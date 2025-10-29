package com.example.recloopmart.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.recloopmart.data.local.AppDatabase
import com.example.recloopmart.data.local.FavouriteEntity
import com.example.recloopmart.data.network.Resource
import com.example.recloopmart.data.repository.FavouriteRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for Favorites screen
 */
class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val favouriteRepository = FavouriteRepository(database.favouriteDao())
    
    // LiveData from Flow
    val favourites: LiveData<List<FavouriteEntity>> = 
        favouriteRepository.getAllFavourites().asLiveData()
    
    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    /**
     * Remove from favourites
     */
    fun removeFavourite(productId: Int) {
        viewModelScope.launch {
            _loading.value = true
            
            when (val result = favouriteRepository.removeFavourite(productId)) {
                is Resource.Success -> {
                    _error.value = null
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                is Resource.Loading -> {
                    // Already showing loading
                }
            }
            
            _loading.value = false
        }
    }
}
