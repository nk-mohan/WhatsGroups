package com.seabird.whatsdev.network.api

import com.seabird.whatsdev.network.model.*
import retrofit2.Response

interface ApiHelper {

    suspend fun registerUser(registerRequest: RegisterRequest): Response<RegisterResponse>
    suspend fun loginUser(loginRequest: LoginRequest): Response<LoginResponse>
    suspend fun addGroup(addGroupRequest: AddGroupRequest): Response<AddGroupResponse>

}