package com.example.recloopmart.data.api

import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API Service for Product endpoints
 * Base URL: https://your-backend-url/api/Product
 */
interface ProductApiService {
    
    /**
     * GET /api/Product
     * Get all products (AllowAnonymous)
     */
    @GET("Product")
    suspend fun getAllProducts(): Response<List<ProductDTO>>
    
    /**
     * GET /api/Product/{id}
     * Get product by ID (AllowAnonymous)
     */
    @GET("Product/{id}")
    suspend fun getProductById(@Path("id") id: Int): Response<ProductDTO>
    
    /**
     * POST /api/Product
     * Create new product (Requires authentication)
     */
    @POST("Product")
    suspend fun createProduct(
        @Body product: ProductCreateDTO
    ): Response<ProductDTO>
    
    /**
     * PUT /api/Product/{id}
     * Update product (Requires authentication + ownership)
     */
    @PUT("Product/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body product: ProductUpdateDTO
    ): Response<Void>
    
    /**
     * DELETE /api/Product/{id}
     * Soft delete product (Requires authentication + ownership)
     */
    @DELETE("Product/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<Void>
    
    /**
     * GET /api/Product/compare/{id1}/{id2}
     * Compare two products using AI (AllowAnonymous)
     */
    @GET("Product/compare/{id1}/{id2}")
    suspend fun compareProducts(
        @Path("id1") id1: Int,
        @Path("id2") id2: Int
    ): Response<Map<String, Any>>
    
    /**
     * GET /api/Product/category/{categoryId}
     * Get products by category (AllowAnonymous)
     */
    @GET("Product/category/{categoryId}")
    suspend fun getProductsByCategory(
        @Path("categoryId") categoryId: Int
    ): Response<List<ProductDTO>>
}



