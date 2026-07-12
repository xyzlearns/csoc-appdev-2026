package com.xyz.orderprototype.data.local.cache

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xyz.orderprototype.data.model.MenuItem
import com.xyz.orderprototype.data.model.Restaurant
import com.xyz.orderprototype.data.model.order.OrderResponse

@Entity(tableName = "restaurants")
data class CachedRestaurant(
    @PrimaryKey val id: String,
    val name: String,
    val image: String,
    val rating: Double,
    val deliveryTime: String,
    val category: String,
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toRestaurant() = Restaurant(id, name, image, rating, deliveryTime, category)
}

fun Restaurant.toCachedRestaurant() =
    CachedRestaurant(id, name, image, rating, deliveryTime, category)

@Entity(tableName = "menu_items")
data class CachedMenuItem(
    @PrimaryKey val id: String,
    val restaurantId: String,
    val name: String,
    val description: String,
    val price: Double,
    val image: String,
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toMenuItem() = MenuItem(id, restaurantId, name, description, price, image)
}

fun MenuItem.toCachedMenuItem() =
    CachedMenuItem(id, restaurantId, name, description, price, image)

@Entity(tableName = "orders")
data class CachedOrder(
    @PrimaryKey val id: String,
    val userEmail: String,
    val restaurantId: String,
    val itemNames: String,
    val totalAmount: Double,
    val status: String,
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toOrderResponse() =
        OrderResponse(
            id = id,
            userEmail = userEmail,
            restaurantId = restaurantId,
            itemNames = itemNames.split("|").filter { it.isNotBlank() },
            totalAmount = totalAmount,
            status = status
        )
}

fun OrderResponse.toCachedOrder() =
    CachedOrder(
        id = id,
        userEmail = userEmail,
        restaurantId = restaurantId,
        itemNames = itemNames.joinToString("|"),
        totalAmount = totalAmount,
        status = status
    )
