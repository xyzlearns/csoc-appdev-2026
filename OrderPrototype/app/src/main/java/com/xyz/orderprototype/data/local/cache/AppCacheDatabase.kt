package com.xyz.orderprototype.data.local.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CachedRestaurant::class, CachedMenuItem::class, CachedOrder::class],
    version = 1,
    exportSchema = false
)
abstract class AppCacheDatabase : RoomDatabase() {

    abstract fun cacheDao(): AppCacheDao

    companion object {
        @Volatile
        private var instance: AppCacheDatabase? = null

        fun getInstance(context: Context): AppCacheDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppCacheDatabase::class.java,
                    "food_delivery_cache.db"
                ).build().also { instance = it }
            }
    }
}
