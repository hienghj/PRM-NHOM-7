package com.example.recloopmart.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Database Entity for Favourite
 */
@Entity(
    tableName = "favourites",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["productId"]),
        Index(value = ["userId", "productId"], unique = true)
    ]
)
data class FavouriteEntity(
    @PrimaryKey
    val id: Int,
    val userId: Int,
    val productId: Int,
    val createdAt: String?,
    val lastUpdated: Long = System.currentTimeMillis()
)



