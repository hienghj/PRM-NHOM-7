package com.example.recloopmart.data.api

import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API Service for Favourite endpoints
 * Base URL: https://your-backend-url/api/Favourite
 */
interface FavouriteApiService {
    
    /**
     * GET /api/Favourite
     * Get all favourites of current user (Requires authentication)
     */
    @GET("Favourite")
    suspend fun getAllFavourites(): Response<List<FavouriteDTO>>
    
    /**
     * POST /api/Favourite
     * Add product to favourites (Requires authentication)
     */
    @POST("Favourite")
    suspend fun addFavourite(
        @Body favourite: FavouriteCreateDTO
    ): Response<FavouriteDTO>
    
    /**
     * DELETE /api/Favourite/{id}
     * Remove favourite (Requires authentication)
     */
    @DELETE("Favourite/{id}")
    suspend fun removeFavourite(@Path("id") id: Int): Response<Void>
    
    /**
     * GET /api/Favourite/check/{productId}
     * Check if product is favourited
     */
    @GET("Favourite/check/{productId}")
    suspend fun checkFavourite(@Path("productId") productId: Int): Response<Boolean>
}



