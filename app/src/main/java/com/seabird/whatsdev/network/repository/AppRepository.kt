package com.seabird.whatsdev.network.repository

import com.seabird.whatsdev.network.api.ApiHelper
import com.seabird.whatsdev.network.model.AddGroupRequest
import com.seabird.whatsdev.network.model.LoginRequest
import com.seabird.whatsdev.network.model.RegisterRequest
import javax.inject.Inject

class AppRepository @Inject constructor(private val apiHelper: ApiHelper) {

    suspend fun registerUser(registerRequest: RegisterRequest) = apiHelper.registerUser(registerRequest)
    suspend fun loginUser(loginRequest: LoginRequest) = apiHelper.loginUser(loginRequest)
    suspend fun addGroup(addGroupRequest: AddGroupRequest) = apiHelper.addGroup(addGroupRequest)
    suspend fun getRecentGroups(pageNumber: Int, perPageResult: Int) = apiHelper.getRecentGroups(pageNumber, perPageResult)
    suspend fun getTrendingGroups(pageNumber: Int, perPageResult: Int) = apiHelper.getTrendingGroups(pageNumber, perPageResult)
    suspend fun updateViewedGroupStatus(id: String) = apiHelper.updateViewedGroupStatus(id)
    suspend fun reportGroup(id: String) = apiHelper.reportGroup(id)
    suspend fun getCategoryList() = apiHelper.getCategoryList()
}