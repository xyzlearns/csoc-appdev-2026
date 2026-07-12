package com.xyz.orderprototype.data.model.order

data class OrderRequest(
    val restaurantId: String,
    val itemNames: List<String>,
    val totalAmount: Double
)