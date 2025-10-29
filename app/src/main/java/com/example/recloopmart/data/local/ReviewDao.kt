package com.example.recloopmart.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Review table
 */
@Dao
interface ReviewDao {
    
    @Query("SELECT * FROM reviews WHERE productId = :productId ORDER BY createdAt DESC")
    fun getReviewsByProductId(productId: Int): Flow<List<ReviewEntity>>
    
    @Query("SELECT * FROM reviews WHERE id = :id")
    suspend fun getReviewById(id: Int): ReviewEntity?
    
    @Query("SELECT AVG(rating) FROM reviews WHERE productId = :productId")
    suspend fun getAverageRating(productId: Int): Double?
    
    @Query("SELECT COUNT(*) FROM reviews WHERE productId = :productId")
    suspend fun getReviewCount(productId: Int): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllReviews(reviews: List<ReviewEntity>)
    
    @Update
    suspend fun updateReview(review: ReviewEntity)
    
    @Delete
    suspend fun deleteReview(review: ReviewEntity)
    
    @Query("DELETE FROM reviews WHERE id = :id")
    suspend fun deleteReviewById(id: Int)
    
    @Query("DELETE FROM reviews WHERE productId = :productId")
    suspend fun deleteReviewsByProductId(productId: Int)
    
    @Query("DELETE FROM reviews")
    suspend fun deleteAllReviews()
}



