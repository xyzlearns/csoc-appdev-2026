package com.xyz.orderprototype.data.local.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow
import androidx.room.Query

@Dao
interface AppCacheDao {

    @Query("SELECT * FROM restaurants ORDER BY rating DESC")
    fun observeRestaurants(): Flow<List<CachedRestaurant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRestaurants(restaurants: List<CachedRestaurant>)

    @Query("SELECT * FROM menu_items WHERE restaurantId = :restaurantId")
    fun observeMenuItems(restaurantId: String): Flow<List<CachedMenuItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMenuItems(items: List<CachedMenuItem>)

    @Query("SELECT * FROM orders ORDER BY updatedAt DESC")
    fun observeOrders(): Flow<List<CachedOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveOrders(orders: List<CachedOrder>)
}
