package com.example.recloopmart.data.api

import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API Service for Category endpoints
 * Base URL: https://your-backend-url/api/Category
 */
interface CategoryApiService {
    
    /**
     * GET /api/Category
     * Get all categories (AllowAnonymous)
     */
    @GET("Category")
    suspend fun getAllCategories(): Response<List<CategoryDTO>>
    
    /**
     * GET /api/Category/{id}
     * Get category by ID (AllowAnonymous)
     */
    @GET("Category/{id}")
    suspend fun getCategoryById(@Path("id") id: Int): Response<CategoryDTO>
}



