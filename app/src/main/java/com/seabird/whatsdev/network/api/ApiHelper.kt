package com.seabird.whatsdev.network.api

import com.seabird.whatsdev.network.model.LoginRequest
import com.seabird.whatsdev.network.model.LoginResponse
import com.seabird.whatsdev.network.model.RegisterRequest
import com.seabird.whatsdev.network.model.RegisterResponse
import retrofit2.Response

interface ApiHelper {

    suspend fun registerUser(registerRequest: RegisterRequest): Response<RegisterResponse>
    suspend fun loginUser(loginRequest: LoginRequest): Response<LoginResponse>

}