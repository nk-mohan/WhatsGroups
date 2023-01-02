package com.seabird.whatsdev.network.api

import com.seabird.whatsdev.network.model.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {

    @POST("whatsgroups/create")
    suspend fun addGroup(@Body addGroupRequest: AddGroupRequest): Response<AddGroupResponse>

    @GET("whatsgroups/")
    suspend fun getRecentGroups(@Query("page") page:Int, @Query("per_page") per_page:Int): Response<GroupListResponse>

    @GET("whatsgroups/trending")
    suspend fun getTrendingGroups(@Query("page") page:Int, @Query("per_page") per_page:Int): Response<GroupListResponse>

    @PUT("whatsgroups/{id}")
    suspend fun updateViewedGroupStatus(@Path("id") id: String): Response<UpdateViewedGroupResponse>

    @PUT("whatsgroups/report/{id}")
    suspend fun reportGroup(@Path("id") id: String): Response<UpdateViewedGroupResponse>

    @GET("whatsgroups/categories")
    suspend fun getCategoryList(): Response<CategoryListResponse>

    @GET("whatsgroups/category/{category_name}")
    suspend fun getCategoryGroups(@Path("category_name") category_name: String, @Query("page") page:Int, @Query("per_page") per_page:Int): Response<GroupListResponse>

}