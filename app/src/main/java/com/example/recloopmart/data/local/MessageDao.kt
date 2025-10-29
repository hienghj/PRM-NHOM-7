package com.example.recloopmart.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Message table
 */
@Dao
interface MessageDao {
    
    @Query("SELECT * FROM messages ORDER BY createdAt DESC")
    fun getAllMessages(): Flow<List<MessageEntity>>
    
    @Query("SELECT * FROM messages WHERE (senderId = :userId OR receiverId = :userId) ORDER BY createdAt ASC")
    fun getConversation(userId: Int): Flow<List<MessageEntity>>
    
    @Query("SELECT * FROM messages WHERE id = :id")
    suspend fun getMessageById(id: Int): MessageEntity?
    
    @Query("SELECT COUNT(*) FROM messages WHERE receiverId = :currentUserId AND isRead = 0")
    suspend fun getUnreadCount(currentUserId: Int): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMessages(messages: List<MessageEntity>)
    
    @Update
    suspend fun updateMessage(message: MessageEntity)
    
    @Query("UPDATE messages SET isRead = 1 WHERE id IN (:messageIds)")
    suspend fun markAsRead(messageIds: List<Int>)
    
    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
}



