package com.example.recloopmart.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recloopmart.data.local.AppDatabase
import com.example.recloopmart.data.local.ProductEntity
import com.example.recloopmart.data.network.Resource
import com.example.recloopmart.data.repository.ProductRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel for Home screen
 * Displays all products
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val productRepository = ProductRepository(database.productDao())
    
    private val _products = MutableLiveData<List<ProductEntity>>(emptyList())
    val products: LiveData<List<ProductEntity>> = _products
    
    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    init {
        loadProducts()
    }
    
    /**
     * Load all products from API/Database
     */
    fun loadProducts() {
        viewModelScope.launch {
            productRepository.getAllProducts().collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _products.value = resource.data ?: emptyList()
                        _loading.value = false
                        _error.value = null
                    }
                    is Resource.Error -> {
                        _error.value = resource.message
                        _loading.value = false
                    }
                    is Resource.Loading -> {
                        _loading.value = true
                    }
                }
            }
        }
    }
    
    /**
     * Search products
     */
    fun searchProducts(query: String) {
        viewModelScope.launch {
            productRepository.searchProducts(query).collectLatest { results ->
                _products.value = results
            }
        }
    }
}



