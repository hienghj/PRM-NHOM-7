package com.example.recloopmart.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Comment table
 */
@Dao
interface CommentDao {
    
    @Query("SELECT * FROM comments WHERE productId = :productId ORDER BY createdAt DESC")
    fun getCommentsByProductId(productId: Int): Flow<List<CommentEntity>>
    
    @Query("SELECT * FROM comments WHERE id = :id")
    suspend fun getCommentById(id: Int): CommentEntity?
    
    @Query("SELECT * FROM comments WHERE parentId = :parentId ORDER BY createdAt ASC")
    fun getRepliesByParentId(parentId: Int): Flow<List<CommentEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllComments(comments: List<CommentEntity>)
    
    @Update
    suspend fun updateComment(comment: CommentEntity)
    
    @Delete
    suspend fun deleteComment(comment: CommentEntity)
    
    @Query("DELETE FROM comments WHERE id = :id")
    suspend fun deleteCommentById(id: Int)
    
    @Query("DELETE FROM comments WHERE productId = :productId")
    suspend fun deleteCommentsByProductId(productId: Int)
    
    @Query("DELETE FROM comments")
    suspend fun deleteAllComments()
}



