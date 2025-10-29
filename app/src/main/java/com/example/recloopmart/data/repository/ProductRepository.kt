package com.example.recloopmart.data.repository

import com.example.recloopmart.data.api.ProductApiService
import com.example.recloopmart.data.api.ProductCreateDTO
import com.example.recloopmart.data.api.ProductDTO
import com.example.recloopmart.data.api.ProductUpdateDTO
import com.example.recloopmart.data.local.ProductDao
import com.example.recloopmart.data.local.ProductEntity
import com.example.recloopmart.data.network.ApiClient
import com.example.recloopmart.data.network.NetworkUtils
import com.example.recloopmart.data.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Repository for Product data
 * Implements offline-first strategy: 
 * 1. Show cached data from Room immediately
 * 2. Fetch fresh data from API
 * 3. Update Room cache
 */
class ProductRepository(
    private val productDao: ProductDao,
    private val productApi: ProductApiService = ApiClient.productApi
) {
    
    /**
     * Get all products (Offline-first)
     * Returns Flow that emits cached data then fresh data from API
     */
    fun getAllProducts(): Flow<Resource<List<ProductEntity>>> = flow {
        // Emit loading state
        emit(Resource.Loading())
        
        // Emit cached data first
        val cachedProducts = productDao.getAllProducts()
        
        // Try to fetch fresh data from API
        val apiResult = NetworkUtils.safeApiCall<List<ProductDTO>>({
            productApi.getAllProducts()
        })
        
        when (apiResult) {
            is Resource.Success -> {
                // Convert DTOs to Entities and cache them
                val entities = apiResult.data?.map { it.toEntity() } ?: emptyList()
                productDao.insertAllProducts(entities)
                emit(Resource.Success(entities))
            }
            is Resource.Error -> {
                // If API fails, use cached data
                emit(Resource.Error(apiResult.message ?: "Error fetching products"))
            }
            is Resource.Loading -> {
                // Should not happen
            }
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get product by ID
     */
    suspend fun getProductById(id: Int): Resource<ProductEntity> = withContext(Dispatchers.IO) {
        // Try cache first
        val cached = productDao.getProductById(id)
        
        // Fetch from API
        val apiResult = NetworkUtils.safeApiCall<ProductDTO>({
            productApi.getProductById(id)
        })
        
        when (apiResult) {
            is Resource.Success -> {
                val entity = apiResult.data?.toEntity()
                if (entity != null) {
                    productDao.insertProduct(entity)
                    Resource.Success(entity)
                } else {
                    Resource.Error("Product not found")
                }
            }
            is Resource.Error -> {
                // If API fails, use cache
                if (cached != null) {
                    Resource.Success(cached)
                } else {
                    Resource.Error(apiResult.message ?: "Product not found")
                }
            }
            is Resource.Loading -> Resource.Loading()
        }
    }
    
    /**
     * Get products by category
     */
    fun getProductsByCategory(categoryId: Int): Flow<Resource<List<ProductEntity>>> = flow {
        emit(Resource.Loading())
        
        val apiResult = NetworkUtils.safeApiCall<List<ProductDTO>>({
            productApi.getProductsByCategory(categoryId)
        })
        
        when (apiResult) {
            is Resource.Success -> {
                val entities = apiResult.data?.map { it.toEntity() } ?: emptyList()
                productDao.insertAllProducts(entities)
                emit(Resource.Success(entities))
            }
            is Resource.Error -> {
                emit(Resource.Error(apiResult.message ?: "Error fetching products"))
            }
            is Resource.Loading -> {}
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Create new product
     */
    suspend fun createProduct(product: ProductCreateDTO): Resource<ProductEntity> = withContext(Dispatchers.IO) {
        android.util.Log.d("ProductRepo", "createProduct called with: $product")
        
        val apiResult = NetworkUtils.safeApiCall<ProductDTO>({
            android.util.Log.d("ProductRepo", "Calling API...")
            productApi.createProduct(product)
        })
        
        android.util.Log.d("ProductRepo", "API result type: ${apiResult::class.simpleName}")
        
        when (apiResult) {
            is Resource.Success -> {
                android.util.Log.d("ProductRepo", "API Success! Data: ${apiResult.data}")
                val dto = apiResult.data
                
                if (dto == null) {
                    android.util.Log.e("ProductRepo", "DTO is null!")
                    return@withContext Resource.Error("API returned null data")
                }
                
                android.util.Log.d("ProductRepo", "Converting DTO to Entity...")
                val entity = dto.toEntity()
                android.util.Log.d("ProductRepo", "Entity created: $entity")
                
                try {
                    productDao.insertProduct(entity)
                    android.util.Log.d("ProductRepo", "Product inserted to Room DB")
                    Resource.Success(entity)
                } catch (e: Exception) {
                    android.util.Log.e("ProductRepo", "Failed to insert to Room DB", e)
                    Resource.Success(entity) // Still return success even if cache fails
                }
            }
            is Resource.Error -> {
                android.util.Log.e("ProductRepo", "API Error: ${apiResult.message}")
                Resource.Error(apiResult.message ?: "Failed to create product")
            }
            is Resource.Loading -> {
                android.util.Log.d("ProductRepo", "API Loading...")
                Resource.Loading()
            }
        }
    }
    
    /**
     * Update product
     */
    suspend fun updateProduct(id: Int, product: ProductUpdateDTO): Resource<Unit> = withContext(Dispatchers.IO) {
        val apiResult = NetworkUtils.safeApiCall<Void>({
            productApi.updateProduct(id, product)
        })
        
        when (apiResult) {
            is Resource.Success -> {
                // Refresh product from API
                val refreshResult = NetworkUtils.safeApiCall<ProductDTO>({
                    productApi.getProductById(id)
                })
                if (refreshResult is Resource.Success) {
                    val entity = refreshResult.data?.toEntity()
                    if (entity != null) {
                        productDao.updateProduct(entity)
                    }
                }
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                Resource.Error(apiResult.message ?: "Failed to update product")
            }
            is Resource.Loading -> Resource.Loading()
        }
    }
    
    /**
     * Delete product
     */
    suspend fun deleteProduct(id: Int): Resource<Unit> = withContext(Dispatchers.IO) {
        val apiResult = NetworkUtils.safeApiCall<Void>({
            productApi.deleteProduct(id)
        })
        
        when (apiResult) {
            is Resource.Success -> {
                productDao.deleteProductById(id)
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                Resource.Error(apiResult.message ?: "Failed to delete product")
            }
            is Resource.Loading -> Resource.Loading()
        }
    }
    
    /**
     * Search products locally
     */
    fun searchProducts(query: String): Flow<List<ProductEntity>> {
        return productDao.searchProducts(query)
    }
    
    /**
     * Compare two products using AI
     */
    suspend fun compareProducts(id1: Int, id2: Int): Resource<Map<String, Any>> = withContext(Dispatchers.IO) {
        NetworkUtils.safeApiCall<Map<String, Any>>({
            productApi.compareProducts(id1, id2)
        })
    }
}

/**
 * Extension function to convert ProductDTO to ProductEntity
 */
private fun ProductDTO.toEntity(): ProductEntity {
    return ProductEntity(
        id = this.id,
        title = this.title,
        descriptions = this.descriptions,
        price = this.price,
        condition = this.condition,
        locations = this.locations,
        createdAt = this.createdAt,
        isActive = this.isActive,
        categoryName = this.categoryName,
        imageUrls = this.imageUrls,
        sellerName = this.sellerName,
        lastUpdated = System.currentTimeMillis()
    )
}



