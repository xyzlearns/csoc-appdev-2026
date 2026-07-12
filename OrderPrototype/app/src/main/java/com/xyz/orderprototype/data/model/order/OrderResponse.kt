package com.xyz.orderprototype.data.model.order

data class OrderResponse(
    val id: String,
    val userEmail: String,
    val restaurantId: String,
    val itemNames: List<String>,
    val totalAmount: Double,
    val status: String
)