package com.xyz.orderprototype.data.model.auth

data class LoginResponse(
    val token: String,
    val id: String,
    val name: String,
    val email: String
)