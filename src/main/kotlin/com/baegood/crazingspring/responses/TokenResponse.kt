package com.baegood.crazingspring.responses

data class TokenResponse(
    val token: String,
    val type: String = "Bearer"
)
