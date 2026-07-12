package com.xyz.orderprototype.data.model

data class Restaurant(
    val id: String,
    val name: String,
    val image: String,
    val rating: Double,
    val deliveryTime: String,
    val category: String
)