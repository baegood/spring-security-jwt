package com.baegood.crazingspring.requests

data class AccountRequest(
    val email: String,
    val password: String,
    var name: String
)
