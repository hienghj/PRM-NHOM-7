package com.example.recloopmart.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recloopmart.data.Product

class ProductManageViewModel : ViewModel() {

    // TODO: Initialize ProductRepository properly with database instance
    // private val repository = ProductRepository(...)

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products
    
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadProducts() {
        // TODO: Implement when repository is properly initialized
        _products.value = emptyList()
    }

    fun delete(product: Product) {
        _products.value = _products.value?.filter { it.id != product.id }
    }

    fun add(product: Product) {
        val current = _products.value ?: emptyList()
        _products.value = current + product
    }
}


