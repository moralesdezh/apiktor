package com.example.routing.response

import kotlinx.serialization.Serializable

@Serializable
class AuthResponse (
    val accessToken: String,
    val refreshToken: String
)