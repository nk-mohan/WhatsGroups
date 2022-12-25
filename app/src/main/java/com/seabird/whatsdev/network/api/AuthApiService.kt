package com.seabird.whatsdev.network.api

import com.seabird.whatsdev.network.model.AddGroupRequest
import com.seabird.whatsdev.network.model.AddGroupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("whatsgroups/create")
    suspend fun addGroup(@Body addGroupRequest: AddGroupRequest): Response<AddGroupResponse>

}