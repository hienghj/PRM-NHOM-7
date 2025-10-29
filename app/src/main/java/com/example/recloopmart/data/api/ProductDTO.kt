package com.example.recloopmart.data.api

import com.google.gson.annotations.SerializedName

/**
 * API Response DTO matching C# ProductDTO from backend
 * GET /api/Product, GET /api/Product/{id}
 */
data class ProductDTO(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("descriptions")
    val descriptions: String?,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("condition")
    val condition: String?,
    
    @SerializedName("locations")
    val locations: String?,
    
    @SerializedName("createdAt")
    val createdAt: String?,
    
    @SerializedName("isActive")
    val isActive: Boolean,
    
    @SerializedName("categoryName")
    val categoryName: String?,
    
    @SerializedName("imageUrls")
    val imageUrls: List<String>?,
    
    @SerializedName("sellerName")
    val sellerName: String?
)

/**
 * Request DTO for creating new product
 * POST /api/Product
 */
data class ProductCreateDTO(
    @SerializedName("title")
    val title: String,
    
    @SerializedName("descriptions")
    val descriptions: String?,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("condition")
    val condition: String?,
    
    @SerializedName("locations")
    val locations: String?,
    
    @SerializedName("categoryId")
    val categoryId: Int
)

/**
 * Request DTO for updating product
 * PUT /api/Product/{id}
 */
data class ProductUpdateDTO(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("descriptions")
    val descriptions: String?,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("condition")
    val condition: String?,
    
    @SerializedName("locations")
    val locations: String?,
    
    @SerializedName("categoryId")
    val categoryId: Int
)



