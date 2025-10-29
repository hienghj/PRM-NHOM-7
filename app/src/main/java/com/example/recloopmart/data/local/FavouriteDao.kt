package com.example.recloopmart.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Favourite table
 */
@Dao
interface FavouriteDao {
    
    @Query("SELECT * FROM favourites ORDER BY lastUpdated DESC")
    fun getAllFavourites(): Flow<List<FavouriteEntity>>
    
    @Query("SELECT * FROM favourites WHERE productId = :productId LIMIT 1")
    suspend fun getFavouriteByProductId(productId: Int): FavouriteEntity?
    
    @Query("SELECT EXISTS(SELECT 1 FROM favourites WHERE productId = :productId LIMIT 1)")
    suspend fun isFavourite(productId: Int): Boolean
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(favourite: FavouriteEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFavourites(favourites: List<FavouriteEntity>)
    
    @Delete
    suspend fun deleteFavourite(favourite: FavouriteEntity)
    
    @Query("DELETE FROM favourites WHERE productId = :productId")
    suspend fun deleteFavouriteByProductId(productId: Int)
    
    @Query("DELETE FROM favourites")
    suspend fun deleteAllFavourites()
}



