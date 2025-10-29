package com.example.recloopmart.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

/**
 * Room Database Entity for Product
 * Stores products locally for offline access
 */
@Entity(
    tableName = "products",
    indices = [
        Index(value = ["categoryName"]),
        Index(value = ["isActive"]),
        Index(value = ["lastUpdated"]),
        Index(value = ["title"])
    ]
)
@TypeConverters(Converters::class)
data class ProductEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val descriptions: String?,
    val price: Double,
    val condition: String?,
    val locations: String?,
    val createdAt: String?,
    val isActive: Boolean,
    val categoryName: String?,
    val imageUrls: List<String>?,
    val sellerName: String?,
    val lastUpdated: Long = System.currentTimeMillis()
)



