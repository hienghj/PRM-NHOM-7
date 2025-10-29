package com.example.recloopmart.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Database Entity for User
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val password: String?,
    val fullName: String?,
    val phoneNumber: String?,
    val address: String?,
    val avatarUrl: String?,
    val createdAt: String?,
    val role: String?,
    val isEmailConfirmed: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)



