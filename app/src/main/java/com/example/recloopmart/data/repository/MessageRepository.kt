package com.example.recloopmart.data.repository

import com.example.recloopmart.data.api.MessageApiService
import com.example.recloopmart.data.api.MessageCreateDTO
import com.example.recloopmart.data.api.MessageDTO
import com.example.recloopmart.data.api.MessageReadDTO
import com.example.recloopmart.data.local.MessageDao
import com.example.recloopmart.data.local.MessageEntity
import com.example.recloopmart.data.network.ApiClient
import com.example.recloopmart.data.network.NetworkUtils
import com.example.recloopmart.data.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Repository for Messages
 */
class MessageRepository(
    private val messageDao: MessageDao,
    private val messageApi: MessageApiService = ApiClient.messageApi
) {
    
    /**
     * Get all messages
     */
    fun getAllMessages(): Flow<Resource<List<MessageEntity>>> = flow {
        emit(Resource.Loading())
        
        val apiResult = NetworkUtils.safeApiCall<List<MessageDTO>>({
            messageApi.getAllMessages()
        })
        
        when (apiResult) {
            is Resource.Success -> {
                val entities = apiResult.data?.map { it.toEntity() } ?: emptyList()
                messageDao.insertAllMessages(entities)
                emit(Resource.Success(entities))
            }
            is Resource.Error -> {
                emit(Resource.Error(apiResult.message ?: "Error"))
            }
            is Resource.Loading -> {}
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get conversation with user
     */
    fun getConversation(userId: Int): Flow<Resource<List<MessageEntity>>> = flow {
        emit(Resource.Loading())
        
        val apiResult = NetworkUtils.safeApiCall<List<MessageDTO>>({
            messageApi.getConversation(userId)
        })
        
        when (apiResult) {
            is Resource.Success -> {
                val entities = apiResult.data?.map { it.toEntity() } ?: emptyList()
                messageDao.insertAllMessages(entities)
                emit(Resource.Success(entities))
            }
            is Resource.Error -> {
                emit(Resource.Error(apiResult.message ?: "Error"))
            }
            is Resource.Loading -> {}
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Send message
     */
    suspend fun sendMessage(receiverId: Int, content: String): Resource<MessageEntity> = withContext(Dispatchers.IO) {
        val request = MessageCreateDTO(receiverId, content)
        val apiResult = NetworkUtils.safeApiCall<MessageDTO>({
            messageApi.sendMessage(request)
        })
        
        when (apiResult) {
            is Resource.Success -> {
                val entity = apiResult.data?.toEntity()
                if (entity != null) {
                    messageDao.insertMessage(entity)
                    Resource.Success(entity)
                } else {
                    Resource.Error("Failed to send message")
                }
            }
            is Resource.Error -> {
                Resource.Error(apiResult.message ?: "Failed to send message")
            }
            is Resource.Loading -> Resource.Loading()
        }
    }
    
    /**
     * Mark messages as read
     */
    suspend fun markAsRead(messageIds: List<Int>): Resource<Unit> = withContext(Dispatchers.IO) {
        val request = MessageReadDTO(messageIds)
        val apiResult = NetworkUtils.safeApiCall<Void>({
            messageApi.markAsRead(request)
        })
        
        when (apiResult) {
            is Resource.Success -> {
                messageDao.markAsRead(messageIds)
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                Resource.Error(apiResult.message ?: "Failed to mark as read")
            }
            is Resource.Loading -> Resource.Loading()
        }
    }
    
    /**
     * Get unread count
     */
    suspend fun getUnreadCount(): Resource<Int> = withContext(Dispatchers.IO) {
        NetworkUtils.safeApiCall<Int>({
            messageApi.getUnreadCount()
        })
    }
}

/**
 * Extension function to convert MessageDTO to MessageEntity
 */
private fun MessageDTO.toEntity(): MessageEntity {
    return MessageEntity(
        id = this.id,
        senderId = this.senderId,
        receiverId = this.receiverId,
        content = this.content,
        createdAt = this.createdAt,
        isRead = this.isRead,
        senderName = this.senderName,
        receiverName = this.receiverName,
        lastUpdated = System.currentTimeMillis()
    )
}



