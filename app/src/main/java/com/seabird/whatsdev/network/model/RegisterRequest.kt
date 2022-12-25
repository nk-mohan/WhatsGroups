package com.seabird.whatsdev.network.model

data class RegisterRequest(
    val device_id: String? = null,
    val device_model: String? = null,
    val device_os_version: String? = null
)