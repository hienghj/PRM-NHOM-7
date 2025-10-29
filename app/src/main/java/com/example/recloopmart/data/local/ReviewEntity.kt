package com.example.recloopmart.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Database Entity for Product Review
 */
@Entity(
    tableName = "reviews",
    indices = [
        Index(value = ["productId"]),
        Index(value = ["userId"]),
        Index(value = ["rating"]),
        Index(value = ["createdAt"])
    ]
)
data class ReviewEntity(
    @PrimaryKey
    val id: Int,
    val productId: Int,
    val userId: Int,
    val rating: Int?,
    val reviewContent: String?,
    val createdAt: String,
    val isVerifiedPurchase: Boolean?,
    val lastUpdated: Long = System.currentTimeMillis()
)



