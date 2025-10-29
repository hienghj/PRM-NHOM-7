package com.example.recloopmart.data.repository

import com.example.recloopmart.data.api.CategoryApiService
import com.example.recloopmart.data.api.CategoryDTO
import com.example.recloopmart.data.local.CategoryDao
import com.example.recloopmart.data.local.CategoryEntity
import com.example.recloopmart.data.network.ApiClient
import com.example.recloopmart.data.network.NetworkUtils
import com.example.recloopmart.data.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * Repository for Categories
 * Implements offline-first strategy with Room database
 */
class CategoryRepository(
    private val categoryDao: CategoryDao,
    private val categoryApi: CategoryApiService = ApiClient.categoryApi
) {
    
    /**
     * Get all categories (Offline-first)
     * Returns Flow that emits cached data then fresh data from API
     */
    fun getAllCategories(): Flow<Resource<List<CategoryEntity>>> = flow {
        emit(Resource.Loading())
        
        // Emit cached data first
        val cachedCategories = categoryDao.getAllCategories().first()
        if (cachedCategories.isNotEmpty()) {
            emit(Resource.Success(cachedCategories))
        }
        
        // Try to fetch fresh data from API
        val apiResult = NetworkUtils.safeApiCall<List<CategoryDTO>>({
            categoryApi.getAllCategories()
        })
        
        when (apiResult) {
            is Resource.Success -> {
                // Convert DTOs to Entities and cache them
                val entities = apiResult.data?.map { it.toEntity() } ?: emptyList()
                categoryDao.insertAllCategories(entities)
                emit(Resource.Success(entities))
            }
            is Resource.Error -> {
                // If API fails and no cache, emit error
                if (cachedCategories.isEmpty()) {
                    emit(Resource.Error(apiResult.message ?: "Error fetching categories"))
                }
            }
            is Resource.Loading -> {
                // Should not happen
            }
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get category by ID
     */
    suspend fun getCategoryById(id: Int): Resource<CategoryEntity> = withContext(Dispatchers.IO) {
        // Try cache first
        val cached = categoryDao.getCategoryById(id)
        
        // Fetch from API
        val apiResult = NetworkUtils.safeApiCall<CategoryDTO>({
            categoryApi.getCategoryById(id)
        })
        
        when (apiResult) {
            is Resource.Success -> {
                val entity = apiResult.data?.toEntity()
                if (entity != null) {
                    categoryDao.insertCategory(entity)
                    Resource.Success(entity)
                } else {
                    Resource.Error("Category not found")
                }
            }
            is Resource.Error -> {
                // If API fails, use cache
                if (cached != null) {
                    Resource.Success(cached)
                } else {
                    Resource.Error(apiResult.message ?: "Category not found")
                }
            }
            is Resource.Loading -> Resource.Loading()
        }
    }
    
    /**
     * Get categories from local database
     */
    fun getCategoriesLocal(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
    }
    
    /**
     * Clear all cached categories
     */
    suspend fun clearCache() = withContext(Dispatchers.IO) {
        categoryDao.deleteAllCategories()
    }
}

/**
 * Extension function to convert CategoryDTO to CategoryEntity
 */
private fun CategoryDTO.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        lastUpdated = System.currentTimeMillis()
    )
}



