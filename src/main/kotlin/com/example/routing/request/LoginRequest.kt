package com.example.routing.request

import kotlinx.serialization.Serializable

@Serializable
class LoginRequest (
    val username: String,
    val password: String
)
