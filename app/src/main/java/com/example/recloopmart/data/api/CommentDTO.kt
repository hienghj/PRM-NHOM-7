package com.example.recloopmart.data.api

import com.google.gson.annotations.SerializedName

/**
 * Product Comment DTO matching backend
 * GET /api/ProductComment/product/{productId}
 */
data class ProductCommentDTO(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("productId")
    val productId: Int,
    
    @SerializedName("userId")
    val userId: Int,
    
    @SerializedName("userName")
    val userName: String?,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("parentId")
    val parentId: Int?,
    
    @SerializedName("createdAt")
    val createdAt: String
)

/**
 * Create Comment Request DTO
 * POST /api/ProductComment
 */
data class CreateProductCommentDTO(
    @SerializedName("productId")
    val productId: Int,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("parentId")
    val parentId: Int? = null
)

/**
 * Update Comment Request DTO
 * PUT /api/ProductComment/{id}
 */
data class UpdateProductCommentDTO(
    @SerializedName("content")
    val content: String
)



