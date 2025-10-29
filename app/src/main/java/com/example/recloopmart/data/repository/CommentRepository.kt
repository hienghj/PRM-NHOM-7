package com.example.recloopmart.data.repository

import com.example.recloopmart.data.api.CreateProductCommentDTO
import com.example.recloopmart.data.api.ProductCommentApiService
import com.example.recloopmart.data.api.ProductCommentDTO
import com.example.recloopmart.data.api.UpdateProductCommentDTO
import com.example.recloopmart.data.local.CommentDao
import com.example.recloopmart.data.local.CommentEntity
import com.example.recloopmart.data.network.ApiClient
import com.example.recloopmart.data.network.NetworkUtils
import com.example.recloopmart.data.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Repository for Product Comments
 */
class CommentRepository(
    private val commentDao: CommentDao,
    private val commentApi: ProductCommentApiService = ApiClient.commentApi
) {
    
    /**
     * Get comments by product ID (Offline-first)
     */
    fun getCommentsByProductId(productId: Int): Flow<Resource<List<CommentEntity>>> = flow {
        emit(Resource.Loading())
        
        // Fetch from API
        val apiResult = NetworkUtils.safeApiCall<List<ProductCommentDTO>>({
            commentApi.getCommentsByProductId(productId)
        })
        
        when (apiResult) {
            is Resource.Success -> {
                val entities = apiResult.data?.map { it.toEntity() } ?: emptyList()
                commentDao.insertAllComments(entities)
                emit(Resource.Success(entities))
            }
            is Resource.Error -> {
                emit(Resource.Error(apiResult.message ?: "Error"))
            }
            is Resource.Loading -> {}
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get comment by ID
     */
    suspend fun getCommentById(id: Int): Resource<CommentEntity> = withContext(Dispatchers.IO) {
        val cached = commentDao.getCommentById(id)
        
        val apiResult = NetworkUtils.safeApiCall<ProductCommentDTO>({
            commentApi.getCommentById(id)
        })
        
        when (apiResult) {
            is Resource.Success -> {
                val entity = apiResult.data?.toEntity()
                if (entity != null) {
                    commentDao.insertComment(entity)
                    Resource.Success(entity)
                } else {
                    Resource.Error("Comment not found")
                }
            }
            is Resource.Error -> {
                if (cached != null) {
                    Resource.Success(cached)
                } else {
                    Resource.Error(apiResult.message ?: "Comment not found")
                }
            }
            is Resource.Loading -> Resource.Loading()
        }
    }
    
    /**
     * Create new comment
     */
    suspend fun createComment(comment: CreateProductCommentDTO): Resource<CommentEntity> = withContext(Dispatchers.IO) {
        val apiResult = NetworkUtils.safeApiCall<ProductCommentDTO>({
            commentApi.createComment(comment)
        })
        
        when (apiResult) {
            is Resource.Success -> {
                val entity = apiResult.data?.toEntity()
                if (entity != null) {
                    commentDao.insertComment(entity)
                    Resource.Success(entity)
                } else {
                    Resource.Error("Failed to create comment")
                }
            }
            is Resource.Error -> {
                Resource.Error(apiResult.message ?: "Failed to create comment")
            }
            is Resource.Loading -> Resource.Loading()
        }
    }
    
    /**
     * Update comment
     */
    suspend fun updateComment(id: Int, comment: UpdateProductCommentDTO): Resource<Unit> = withContext(Dispatchers.IO) {
        val apiResult = NetworkUtils.safeApiCall<Void>({
            commentApi.updateComment(id, comment)
        })
        
        when (apiResult) {
            is Resource.Success -> {
                // Refresh comment from API
                val refreshResult = NetworkUtils.safeApiCall<ProductCommentDTO>({
                    commentApi.getCommentById(id)
                })
                if (refreshResult is Resource.Success) {
                    val entity = refreshResult.data?.toEntity()
                    if (entity != null) {
                        commentDao.updateComment(entity)
                    }
                }
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                Resource.Error(apiResult.message ?: "Failed to update comment")
            }
            is Resource.Loading -> Resource.Loading()
        }
    }
    
    /**
     * Delete comment
     */
    suspend fun deleteComment(id: Int): Resource<Unit> = withContext(Dispatchers.IO) {
        val apiResult = NetworkUtils.safeApiCall<Void>({
            commentApi.deleteComment(id)
        })
        
        when (apiResult) {
            is Resource.Success -> {
                commentDao.deleteCommentById(id)
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                Resource.Error(apiResult.message ?: "Failed to delete comment")
            }
            is Resource.Loading -> Resource.Loading()
        }
    }
}

/**
 * Extension function to convert ProductCommentDTO to CommentEntity
 */
private fun ProductCommentDTO.toEntity(): CommentEntity {
    return CommentEntity(
        id = this.id,
        productId = this.productId,
        userId = this.userId,
        userName = this.userName,
        content = this.content,
        parentId = this.parentId,
        createdAt = this.createdAt,
        lastUpdated = System.currentTimeMillis()
    )
}



