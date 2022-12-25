package com.seabird.whatsdev.network.api

import com.seabird.whatsdev.network.model.LoginRequest
import com.seabird.whatsdev.network.model.LoginResponse
import com.seabird.whatsdev.network.model.RegisterRequest
import com.seabird.whatsdev.network.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/register")
    suspend fun registerUser(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<LoginResponse>
}