package com.xyz.orderprototype.data.cart

import com.xyz.orderprototype.data.model.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object CartManager {

    private val _cartItems =
        MutableStateFlow<List<CartItem>>(emptyList())
    private var currentRestaurantId: String? = null

    val cartItems: StateFlow<List<CartItem>> = _cartItems

    fun addItem(item: MenuItem) {

        if (
            currentRestaurantId != null &&
            currentRestaurantId != item.restaurantId
        ) {

            _cartItems.value = emptyList()
        }

        currentRestaurantId = item.restaurantId

        val existingItem =
            _cartItems.value.find {
                it.item.id == item.id
            }

        if (existingItem != null) {

            _cartItems.value =
                _cartItems.value.map {

                    if (it.item.id == item.id) {
                        it.copy(
                            quantity = it.quantity + 1
                        )
                    } else {
                        it
                    }
                }

        } else {

            _cartItems.value =
                _cartItems.value +
                        CartItem(
                            item = item,
                            quantity = 1
                        )
        }
    }

    fun getRestaurantId(): String? {
        return currentRestaurantId
    }

    fun clearCart() {

        _cartItems.value = emptyList()

        currentRestaurantId = null
    }
}