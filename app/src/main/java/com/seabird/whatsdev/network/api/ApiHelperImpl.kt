package com.seabird.whatsdev.network.api

import com.seabird.whatsdev.network.model.*
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val openApiService: OpenApiService, private val authApiService: AuthApiService) : ApiHelper {

    override suspend fun registerUser(registerRequest: RegisterRequest): Response<RegisterResponse> = openApiService.registerUser(registerRequest)

    override suspend fun loginUser(loginRequest: LoginRequest): Response<LoginResponse> = openApiService.loginUser(loginRequest)

    override suspend fun addGroup(addGroupRequest: AddGroupRequest): Response<AddGroupResponse> = authApiService.addGroup(addGroupRequest)
}