package com.seabird.whatsdev.network.model

data class LoginResponse(
    val user: User? = null,
    val error: String? = null
)