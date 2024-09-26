package com.example.routing.response

import kotlinx.serialization.Serializable

@Serializable
class RefreshTokenResponse (
    val token: String
)