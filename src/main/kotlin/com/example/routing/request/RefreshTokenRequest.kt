package com.example.routing.request

import kotlinx.serialization.Serializable

@Serializable
class RefreshTokenRequest (
    val token: String
)