package com.example.recloopmart.data.api

import com.google.gson.annotations.SerializedName

/**
 * Product Review DTO matching backend
 * GET /api/ProductReview/{productId}
 */
data class ProductReviewDTO(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("productId")
    val productId: Int,
    
    @SerializedName("userId")
    val userId: Int,
    
    @SerializedName("rating")
    val rating: Int?,
    
    @SerializedName("reviewContent")
    val reviewContent: String?,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("isVerifiedPurchase")
    val isVerifiedPurchase: Boolean?
)

/**
 * Create Review Request DTO
 * POST /api/ProductReview
 */
data class ReviewCreateDTO(
    @SerializedName("productId")
    val productId: Int,
    
    @SerializedName("rating")
    val rating: Int,
    
    @SerializedName("reviewContent")
    val reviewContent: String,
    
    @SerializedName("isVerifiedPurchase")
    val isVerifiedPurchase: Boolean = false
)

/**
 * Update Review Request DTO
 * PUT /api/ProductReview/{id}
 */
data class ReviewUpdateDTO(
    @SerializedName("rating")
    val rating: Int,
    
    @SerializedName("reviewContent")
    val reviewContent: String
)



