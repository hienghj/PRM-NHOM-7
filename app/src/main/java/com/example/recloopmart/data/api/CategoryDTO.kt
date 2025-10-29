package com.example.recloopmart.data.api

import com.google.gson.annotations.SerializedName

/**
 * Category DTO matching backend
 */
data class CategoryDTO(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String?
)



