package com.example.recloopmart.data.api

import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API Service for Product Comments
 * Base URL: https://your-backend-url/api/ProductComment
 */
interface ProductCommentApiService {
    
    /**
     * GET /api/ProductComment/product/{productId}
     * Get all comments for a product (AllowAnonymous)
     */
    @GET("ProductComment/product/{productId}")
    suspend fun getCommentsByProductId(
        @Path("productId") productId: Int
    ): Response<List<ProductCommentDTO>>
    
    /**
     * GET /api/ProductComment/{id}
     * Get comment by ID (AllowAnonymous)
     */
    @GET("ProductComment/{id}")
    suspend fun getCommentById(@Path("id") id: Int): Response<ProductCommentDTO>
    
    /**
     * POST /api/ProductComment
     * Create new comment (Requires authentication)
     */
    @POST("ProductComment")
    suspend fun createComment(
        @Body comment: CreateProductCommentDTO
    ): Response<ProductCommentDTO>
    
    /**
     * PUT /api/ProductComment/{id}
     * Update comment (Requires authentication + ownership)
     */
    @PUT("ProductComment/{id}")
    suspend fun updateComment(
        @Path("id") id: Int,
        @Body comment: UpdateProductCommentDTO
    ): Response<Void>
    
    /**
     * DELETE /api/ProductComment/{id}
     * Delete comment (Requires authentication + ownership)
     */
    @DELETE("ProductComment/{id}")
    suspend fun deleteComment(@Path("id") id: Int): Response<Void>
}



