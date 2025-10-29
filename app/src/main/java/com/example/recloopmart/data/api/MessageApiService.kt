package com.example.recloopmart.data.api

import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API Service for Messages
 */
interface MessageApiService {
    
    /**
     * GET all messages (conversations)
     */
    @GET("Message")
    suspend fun getAllMessages(): Response<List<MessageDTO>>
    
    /**
     * GET conversation with specific user
     */
    @GET("Message/conversation/{userId}")
    suspend fun getConversation(
        @Path("userId") userId: Int
    ): Response<List<MessageDTO>>
    
    /**
     * POST send message
     */
    @POST("Message")
    suspend fun sendMessage(
        @Body message: MessageCreateDTO
    ): Response<MessageDTO>
    
    /**
     * PUT mark messages as read
     */
    @PUT("Message/mark-read")
    suspend fun markAsRead(
        @Body messageIds: MessageReadDTO
    ): Response<Void>
    
    /**
     * GET unread count
     */
    @GET("Message/unread-count")
    suspend fun getUnreadCount(): Response<Int>
}



