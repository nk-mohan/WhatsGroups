package com.seabird.whatsdev.network.api

import com.seabird.whatsdev.network.model.*
import retrofit2.Response

interface ApiHelper {

    suspend fun registerUser(registerRequest: RegisterRequest): Response<RegisterResponse>
    suspend fun loginUser(loginRequest: LoginRequest): Response<LoginResponse>
    suspend fun addGroup(addGroupRequest: AddGroupRequest): Response<AddGroupResponse>
    suspend fun getRecentGroups(pageNumber: Int, perPageResult: Int): Response<GroupListResponse>
    suspend fun getTrendingGroups(pageNumber: Int, perPageResult: Int): Response<GroupListResponse>
    suspend fun getCategoryGroups(pageNumber: Int, perPageResult: Int, categoryName: String): Response<GroupListResponse>
    suspend fun updateViewedGroupStatus(id: String): Response<UpdateViewedGroupResponse>
    suspend fun reportGroup(id: String): Response<UpdateViewedGroupResponse>
    suspend fun getCategoryList(): Response<CategoryListResponse>

}