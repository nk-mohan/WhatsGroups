package com.seabird.whatsdev.ui.addgroup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.seabird.whatsdev.network.model.AddGroupRequest
import com.seabird.whatsdev.network.model.AddGroupResponse
import com.seabird.whatsdev.network.other.ErrorResponse
import com.seabird.whatsdev.network.other.Resource
import com.seabird.whatsdev.network.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddGroupViewModel @Inject constructor(
    private val appRepository: AppRepository
): ViewModel() {
    private val _addGroupRes = MutableLiveData<Resource<AddGroupResponse>>()

    val addGroupRes : LiveData<Resource<AddGroupResponse>>
        get() = _addGroupRes

    fun addGroup(addGroupRequest: AddGroupRequest) {
        viewModelScope.launch {
            _addGroupRes.postValue(Resource.loading(null))
            val gson = Gson()
            val type = object : TypeToken<ErrorResponse>() {}.type
            appRepository.addGroup(addGroupRequest).let {
                if (it.isSuccessful) {
                    _addGroupRes.postValue(Resource.success(it.body()))
                } else {
                    val errorResponse: ErrorResponse? = gson.fromJson(it.errorBody()!!.charStream(), type)
                    _addGroupRes.postValue(Resource.error(errorResponse?.getError() ?: it.message(), it.code(), null))
                }
            }
        }
    }
}