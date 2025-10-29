package com.example.recloopmart.data.api

import com.google.gson.annotations.SerializedName

/**
 * Favourite Create Request DTO
 * POST /api/Favourite
 */
data class FavouriteCreateDTO(
    @SerializedName("productId")
    val productId: Int
)

/**
 * Favourite Response DTO
 */
data class FavouriteDTO(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("userId")
    val userId: Int,
    
    @SerializedName("productId")
    val productId: Int,
    
    @SerializedName("createdAt")
    val createdAt: String?,
    
    @SerializedName("product")
    val product: ProductDTO?
)



