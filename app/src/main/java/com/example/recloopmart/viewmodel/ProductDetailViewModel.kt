package com.example.recloopmart.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recloopmart.data.local.AppDatabase
import com.example.recloopmart.data.local.ProductEntity
import com.example.recloopmart.data.network.Resource
import com.example.recloopmart.data.repository.FavouriteRepository
import com.example.recloopmart.data.repository.ProductRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for Product Detail screen
 * Uses MVVM pattern with Repository and Coroutines
 */
class ProductDetailViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val productRepository = ProductRepository(database.productDao())
    private val favouriteRepository = FavouriteRepository(database.favouriteDao())
    
    private val _product = MutableLiveData<ProductEntity?>()
    val product: LiveData<ProductEntity?> = _product
    
    private val _favorite = MutableLiveData<Boolean>(false)
    val favorite: LiveData<Boolean> = _favorite
    
    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    /**
     * Load product by ID from API/Database
     */
    fun load(productId: String) {
        // Try to parse productId as Int
        val id = productId.toIntOrNull()
        if (id == null) {
            _error.value = "Invalid product ID"
            return
        }
        
        viewModelScope.launch {
            _loading.value = true
            
            // Load product
            when (val result = productRepository.getProductById(id)) {
                is Resource.Success -> {
                    _product.value = result.data
                    _error.value = null
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                is Resource.Loading -> {
                    // Already showing loading
                }
            }
            
            // Load favourite status
            _favorite.value = favouriteRepository.isFavourite(id)
            
            _loading.value = false
        }
    }
    
    /**
     * Toggle favourite status
     */
    fun toggleFavorite() {
        val productId = _product.value?.id ?: return
        
        viewModelScope.launch {
            when (val result = favouriteRepository.toggleFavourite(productId)) {
                is Resource.Success -> {
                    _favorite.value = result.data
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                is Resource.Loading -> {
                    // Ignore loading state for toggle
                }
            }
        }
    }
}
