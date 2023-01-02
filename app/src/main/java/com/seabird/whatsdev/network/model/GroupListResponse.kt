package com.seabird.whatsdev.network.model

data class GroupListResponse(
    val data: List<GroupModel>,
    val meta: MetaResponse
)