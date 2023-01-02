package com.seabird.whatsdev.network.api

import com.seabird.whatsdev.network.model.*
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val openApiService: OpenApiService, private val authApiService: AuthApiService) : ApiHelper {

    override suspend fun registerUser(registerRequest: RegisterRequest): Response<RegisterResponse> = openApiService.registerUser(registerRequest)

    override suspend fun loginUser(loginRequest: LoginRequest): Response<LoginResponse> = openApiService.loginUser(loginRequest)

    override suspend fun addGroup(addGroupRequest: AddGroupRequest): Response<AddGroupResponse> = authApiService.addGroup(addGroupRequest)

    override suspend fun getRecentGroups(pageNumber: Int, perPageResult: Int): Response<GroupListResponse> = authApiService.getRecentGroups(pageNumber, perPageResult)

    override suspend fun getTrendingGroups(pageNumber: Int, perPageResult: Int): Response<GroupListResponse> = authApiService.getTrendingGroups(pageNumber, perPageResult)

    override suspend fun getCategoryGroups(pageNumber: Int, perPageResult: Int, categoryName: String): Response<GroupListResponse> = authApiService.getCategoryGroups(categoryName, pageNumber, perPageResult)

    override suspend fun updateViewedGroupStatus(id: String): Response<UpdateViewedGroupResponse> = authApiService.updateViewedGroupStatus(id)

    override suspend fun reportGroup(id: String): Response<UpdateViewedGroupResponse> = authApiService.reportGroup(id)

    override suspend fun getCategoryList(): Response<CategoryListResponse> = authApiService.getCategoryList()
}