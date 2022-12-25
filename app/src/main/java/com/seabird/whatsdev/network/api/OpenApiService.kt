package com.seabird.whatsdev.network.api

import com.seabird.whatsdev.network.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenApiService {

    @POST("auth/register")
    suspend fun registerUser(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<LoginResponse>
}