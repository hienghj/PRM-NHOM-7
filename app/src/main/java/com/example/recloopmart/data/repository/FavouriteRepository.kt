package com.example.recloopmart.data.repository

import com.example.recloopmart.data.api.FavouriteApiService
import com.example.recloopmart.data.api.FavouriteCreateDTO
import com.example.recloopmart.data.api.FavouriteDTO
import com.example.recloopmart.data.local.FavouriteDao
import com.example.recloopmart.data.local.FavouriteEntity
import com.example.recloopmart.data.network.ApiClient
import com.example.recloopmart.data.network.NetworkUtils
import com.example.recloopmart.data.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repository for Favourites
 */
class FavouriteRepository(
    private val favouriteDao: FavouriteDao,
    private val favouriteApi: FavouriteApiService = ApiClient.favouriteApi
) {
    
    /**
     * Get all favourites
     */
    fun getAllFavourites(): Flow<List<FavouriteEntity>> {
        return favouriteDao.getAllFavourites()
    }
    
    /**
     * Add to favourites
     */
    suspend fun addFavourite(productId: Int): Resource<Unit> = withContext(Dispatchers.IO) {
        val request = FavouriteCreateDTO(productId)
        val apiResult = NetworkUtils.safeApiCall<FavouriteDTO>({
            favouriteApi.addFavourite(request)
        })
        
        when (apiResult) {
            is Resource.Success -> {
                val data = apiResult.data
                if (data != null) {
                    val entity = FavouriteEntity(
                        id = data.id,
                        userId = data.userId,
                        productId = data.productId,
                        createdAt = data.createdAt
                    )
                    favouriteDao.insertFavourite(entity)
                }
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                Resource.Error(apiResult.message ?: "Failed to add favourite")
            }
            is Resource.Loading -> Resource.Loading()
        }
    }
    
    /**
     * Remove from favourites
     */
    suspend fun removeFavourite(productId: Int): Resource<Unit> = withContext(Dispatchers.IO) {
        val favourite = favouriteDao.getFavouriteByProductId(productId)
        
        if (favourite != null) {
            val apiResult = NetworkUtils.safeApiCall<Void>({
                favouriteApi.removeFavourite(favourite.id)
            })
            
            when (apiResult) {
                is Resource.Success -> {
                    favouriteDao.deleteFavouriteByProductId(productId)
                    Resource.Success(Unit)
                }
                is Resource.Error -> {
                    Resource.Error(apiResult.message ?: "Failed to remove favourite")
                }
                is Resource.Loading -> Resource.Loading()
            }
        } else {
            Resource.Error("Favourite not found")
        }
    }
    
    /**
     * Check if product is favourited
     */
    suspend fun isFavourite(productId: Int): Boolean = withContext(Dispatchers.IO) {
        favouriteDao.isFavourite(productId)
    }
    
    /**
     * Toggle favourite status
     */
    suspend fun toggleFavourite(productId: Int): Resource<Boolean> = withContext(Dispatchers.IO) {
        val isFav = isFavourite(productId)
        
        if (isFav) {
            val result = removeFavourite(productId)
            when (result) {
                is Resource.Success -> Resource.Success(false)
                is Resource.Error -> Resource.Error(result.message ?: "Error")
                is Resource.Loading -> Resource.Loading()
            }
        } else {
            val result = addFavourite(productId)
            when (result) {
                is Resource.Success -> Resource.Success(true)
                is Resource.Error -> Resource.Error(result.message ?: "Error")
                is Resource.Loading -> Resource.Loading()
            }
        }
    }
}



