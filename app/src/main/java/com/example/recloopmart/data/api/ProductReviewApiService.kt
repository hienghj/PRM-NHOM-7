package com.example.recloopmart.data.api

import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API Service for Product Reviews
 * Base URL: https://your-backend-url/api/ProductReview
 */
interface ProductReviewApiService {
    
    /**
     * GET /api/ProductReview/{productId}
     * Get all reviews for a product (Requires authentication)
     */
    @GET("ProductReview/{productId}")
    suspend fun getReviewsByProductId(
        @Path("productId") productId: Int
    ): Response<List<ProductReviewDTO>>
    
    /**
     * GET /api/ProductReview/details/{id}
     * Get review by ID (Requires authentication)
     */
    @GET("ProductReview/details/{id}")
    suspend fun getReviewById(@Path("id") id: Int): Response<ProductReviewDTO>
    
    /**
     * POST /api/ProductReview
     * Create new review (Requires authentication)
     */
    @POST("ProductReview")
    suspend fun createReview(
        @Body review: ReviewCreateDTO
    ): Response<ProductReviewDTO>
    
    /**
     * PUT /api/ProductReview/{id}
     * Update review (Requires authentication + ownership)
     */
    @PUT("ProductReview/{id}")
    suspend fun updateReview(
        @Path("id") id: Int,
        @Body review: ReviewUpdateDTO
    ): Response<Void>
    
    /**
     * DELETE /api/ProductReview/{id}
     * Delete review (Requires authentication + ownership)
     */
    @DELETE("ProductReview/{id}")
    suspend fun deleteReview(@Path("id") id: Int): Response<Void>
}



