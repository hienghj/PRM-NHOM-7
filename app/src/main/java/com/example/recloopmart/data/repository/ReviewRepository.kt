package com.example.recloopmart.data.repository

import com.example.recloopmart.data.api.ProductReviewApiService
import com.example.recloopmart.data.api.ProductReviewDTO
import com.example.recloopmart.data.api.ReviewCreateDTO
import com.example.recloopmart.data.api.ReviewUpdateDTO
import com.example.recloopmart.data.local.ReviewDao
import com.example.recloopmart.data.local.ReviewEntity
import com.example.recloopmart.data.network.ApiClient
import com.example.recloopmart.data.network.NetworkUtils
import com.example.recloopmart.data.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Repository for Product Reviews
 */
class ReviewRepository(
    private val reviewDao: ReviewDao,
    private val reviewApi: ProductReviewApiService = ApiClient.reviewApi
) {
    
    /**
     * Get reviews by product ID (Offline-first)
     */
    fun getReviewsByProductId(productId: Int): Flow<Resource<List<ReviewEntity>>> = flow {
        emit(Resource.Loading())
        
        // Fetch from API
        val apiResult = NetworkUtils.safeApiCall<List<ProductReviewDTO>>({
            reviewApi.getReviewsByProductId(productId)
        })
        
        when (apiResult) {
            is Resource.Success -> {
                val entities = apiResult.data?.map { it.toEntity() } ?: emptyList()
                reviewDao.insertAllReviews(entities)
                emit(Resource.Success(entities))
            }
            is Resource.Error -> {
                emit(Resource.Error(apiResult.message ?: "Error"))
            }
            is Resource.Loading -> {}
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get review by ID
     */
    suspend fun getReviewById(id: Int): Resource<ReviewEntity> = withContext(Dispatchers.IO) {
        val cached = reviewDao.getReviewById(id)
        
        val apiResult = NetworkUtils.safeApiCall<ProductReviewDTO>({
            reviewApi.getReviewById(id)
        })
        
        when (apiResult) {
            is Resource.Success -> {
                val entity = apiResult.data?.toEntity()
                if (entity != null) {
                    reviewDao.insertReview(entity)
                    Resource.Success(entity)
                } else {
                    Resource.Error("Review not found")
                }
            }
            is Resource.Error -> {
                if (cached != null) {
                    Resource.Success(cached)
                } else {
                    Resource.Error(apiResult.message ?: "Review not found")
                }
            }
            is Resource.Loading -> Resource.Loading()
        }
    }
    
    /**
     * Create new review
     */
    suspend fun createReview(review: ReviewCreateDTO): Resource<ReviewEntity> = withContext(Dispatchers.IO) {
        val apiResult = NetworkUtils.safeApiCall<ProductReviewDTO>({
            reviewApi.createReview(review)
        })
        
        when (apiResult) {
            is Resource.Success -> {
                val entity = apiResult.data?.toEntity()
                if (entity != null) {
                    reviewDao.insertReview(entity)
                    Resource.Success(entity)
                } else {
                    Resource.Error("Failed to create review")
                }
            }
            is Resource.Error -> {
                Resource.Error(apiResult.message ?: "Failed to create review")
            }
            is Resource.Loading -> Resource.Loading()
        }
    }
    
    /**
     * Update review
     */
    suspend fun updateReview(id: Int, review: ReviewUpdateDTO): Resource<Unit> = withContext(Dispatchers.IO) {
        val apiResult = NetworkUtils.safeApiCall<Void>({
            reviewApi.updateReview(id, review)
        })
        
        when (apiResult) {
            is Resource.Success -> {
                // Refresh review from API
                val refreshResult = NetworkUtils.safeApiCall<ProductReviewDTO>({
                    reviewApi.getReviewById(id)
                })
                if (refreshResult is Resource.Success) {
                    val entity = refreshResult.data?.toEntity()
                    if (entity != null) {
                        reviewDao.updateReview(entity)
                    }
                }
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                Resource.Error(apiResult.message ?: "Failed to update review")
            }
            is Resource.Loading -> Resource.Loading()
        }
    }
    
    /**
     * Delete review
     */
    suspend fun deleteReview(id: Int): Resource<Unit> = withContext(Dispatchers.IO) {
        val apiResult = NetworkUtils.safeApiCall<Void>({
            reviewApi.deleteReview(id)
        })
        
        when (apiResult) {
            is Resource.Success -> {
                reviewDao.deleteReviewById(id)
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                Resource.Error(apiResult.message ?: "Failed to delete review")
            }
            is Resource.Loading -> Resource.Loading()
        }
    }
    
    /**
     * Get average rating for product
     */
    suspend fun getAverageRating(productId: Int): Double = withContext(Dispatchers.IO) {
        reviewDao.getAverageRating(productId) ?: 0.0
    }
    
    /**
     * Get review count for product
     */
    suspend fun getReviewCount(productId: Int): Int = withContext(Dispatchers.IO) {
        reviewDao.getReviewCount(productId)
    }
}

/**
 * Extension function to convert ProductReviewDTO to ReviewEntity
 */
private fun ProductReviewDTO.toEntity(): ReviewEntity {
    return ReviewEntity(
        id = this.id,
        productId = this.productId,
        userId = this.userId,
        rating = this.rating,
        reviewContent = this.reviewContent,
        createdAt = this.createdAt,
        isVerifiedPurchase = this.isVerifiedPurchase,
        lastUpdated = System.currentTimeMillis()
    )
}



