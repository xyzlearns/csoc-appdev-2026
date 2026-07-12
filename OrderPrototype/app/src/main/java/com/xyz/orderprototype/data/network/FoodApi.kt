package com.xyz.orderprototype.data.network

import com.xyz.orderprototype.data.model.Restaurant
import retrofit2.http.GET
import retrofit2.http.Path
import com.xyz.orderprototype.data.model.MenuItem
import com.xyz.orderprototype.data.model.auth.LoginRequest
import com.xyz.orderprototype.data.model.auth.LoginResponse
import com.xyz.orderprototype.data.model.auth.RegisterRequest
import com.xyz.orderprototype.data.model.auth.UserResponse
import com.xyz.orderprototype.data.model.order.OrderRequest
import com.xyz.orderprototype.data.model.order.OrderResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface FoodApi {

    @GET("api/restaurants")
    suspend fun getRestaurants(): List<Restaurant>

    @GET("api/restaurants/{restaurantId}/menu")
    suspend fun getMenu(
        @Path("restaurantId")
        restaurantId: String
    ): List<MenuItem>

    @GET("api/restaurants/{id}")
    suspend fun getRestaurant(
        @Path("id") id: String
    ): Restaurant

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    )

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("api/auth/me")
    suspend fun getMe(
        @retrofit2.http.Header("Authorization")
        token: String
    ): UserResponse

    @PUT("api/auth/me")
    suspend fun updateMe(
        @retrofit2.http.Header("Authorization")
        token: String,
        @Body request: com.xyz.orderprototype.data.model.auth.UpdateProfileRequest
    ): UserResponse

    @POST("api/orders")
    suspend fun placeOrder(
        @retrofit2.http.Header("Authorization")
        token: String,

        @Body request: OrderRequest
    )

    @GET("api/orders")
    suspend fun getOrders(
        @Header("Authorization")
        token: String
    ): List<OrderResponse>
}
