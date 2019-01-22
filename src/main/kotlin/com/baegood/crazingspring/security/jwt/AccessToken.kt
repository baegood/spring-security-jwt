package com.baegood.crazingspring.security.jwt

import com.fasterxml.jackson.annotation.JsonIgnore
import io.jsonwebtoken.Claims

data class AccessToken(
    val token: String,
    @JsonIgnore
    val claims: Claims
)
