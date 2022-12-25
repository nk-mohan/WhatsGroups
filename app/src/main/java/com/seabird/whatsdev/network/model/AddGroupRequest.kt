package com.seabird.whatsdev.network.model

data class AddGroupRequest(
    val title: String,
    val description: String,
    val link: String,
    val category: String
)