package com.example.recloopmart.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Database Entity for Category
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val description: String?,
    val lastUpdated: Long = System.currentTimeMillis()
)



