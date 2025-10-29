package com.example.recloopmart.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Database Entity for Product Comment
 */
@Entity(
    tableName = "comments",
    indices = [
        Index(value = ["productId"]),
        Index(value = ["userId"]),
        Index(value = ["parentId"]),
        Index(value = ["createdAt"])
    ]
)
data class CommentEntity(
    @PrimaryKey
    val id: Int,
    val productId: Int,
    val userId: Int,
    val userName: String?,
    val content: String,
    val parentId: Int?,
    val createdAt: String,
    val lastUpdated: Long = System.currentTimeMillis()
)



