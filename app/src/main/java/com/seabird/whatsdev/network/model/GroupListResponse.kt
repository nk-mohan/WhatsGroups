package com.seabird.whatsdev.network.model

data class GroupListResponse(
    val data: List<GroupResponse>,
    val meta: MetaResponse
)