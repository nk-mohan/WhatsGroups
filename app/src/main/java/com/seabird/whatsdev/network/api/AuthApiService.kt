package com.seabird.whatsdev.network.api

import com.seabird.whatsdev.network.model.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {

    @POST("whatsgroups/create")
    suspend fun addGroup(@Body addGroupRequest: AddGroupRequest): Response<AddGroupResponse>

    @GET("whatsgroups/")
    suspend fun getRecentGroups(@Query("page") page:Int, @Query("per_page") per_page:Int): Response<GroupListResponse>

    @GET("whatsgroups/trending/")
    suspend fun getTrendingGroups(@Query("page") page:Int, @Query("per_page") per_page:Int): Response<GroupListResponse>

    @PUT("whatsgroups/{id}")
    suspend fun updateViewedGroupStatus(@Path("id") id: String): Response<UpdateViewedGroupResponse>

}