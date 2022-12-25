package com.seabird.whatsdev.network.api

import com.seabird.whatsdev.network.model.LoginRequest
import com.seabird.whatsdev.network.model.LoginResponse
import com.seabird.whatsdev.network.model.RegisterRequest
import com.seabird.whatsdev.network.model.RegisterResponse
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {

    override suspend fun registerUser(registerRequest: RegisterRequest): Response<RegisterResponse> = apiService.registerUser(registerRequest)

    override suspend fun loginUser(loginRequest: LoginRequest): Response<LoginResponse> = apiService.loginUser(loginRequest)
}