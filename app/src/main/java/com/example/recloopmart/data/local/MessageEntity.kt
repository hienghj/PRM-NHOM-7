package com.example.recloopmart.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Database Entity for Message
 */
@Entity(
    tableName = "messages",
    indices = [
        Index(value = ["senderId"]),
        Index(value = ["receiverId"]),
        Index(value = ["createdAt"]),
        Index(value = ["isRead"])
    ]
)
data class MessageEntity(
    @PrimaryKey
    val id: Int,
    val senderId: Int,
    val receiverId: Int,
    val content: String,
    val createdAt: String,
    val isRead: Boolean,
    val senderName: String?,
    val receiverName: String?,
    val lastUpdated: Long = System.currentTimeMillis()
)



