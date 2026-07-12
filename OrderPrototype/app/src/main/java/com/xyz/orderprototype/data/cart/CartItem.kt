package com.xyz.orderprototype.data.cart

import com.xyz.orderprototype.data.model.MenuItem

data class CartItem(
    val item: MenuItem,
    val quantity: Int
)