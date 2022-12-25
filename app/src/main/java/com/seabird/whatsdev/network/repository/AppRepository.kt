package com.seabird.whatsdev.network.repository

import com.seabird.whatsdev.network.api.ApiHelper
import com.seabird.whatsdev.network.model.LoginRequest
import com.seabird.whatsdev.network.model.RegisterRequest
import javax.inject.Inject

class AppRepository @Inject constructor(private val apiHelper: ApiHelper) {

    suspend fun registerUser(registerRequest: RegisterRequest) = apiHelper.registerUser(registerRequest)
    suspend fun loginUser(loginRequest: LoginRequest) = apiHelper.loginUser(loginRequest)
}