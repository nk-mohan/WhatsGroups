package com.seabird.whatsdev.network.model

data class RegisterResponse(
    val message: String? = null,
    val user: User? = null,
    val error: String? = null
)

data class User(
    val access_token: String = "",
    val device_id: String = "",
    val refresh_token: String = ""
)
