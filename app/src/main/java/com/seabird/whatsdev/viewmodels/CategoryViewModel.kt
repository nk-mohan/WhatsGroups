package com.seabird.whatsdev.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seabird.whatsdev.TAG
import com.seabird.whatsdev.network.model.CategoryListResponse
import com.seabird.whatsdev.network.model.CategoryModel
import com.seabird.whatsdev.network.other.Resource
import com.seabird.whatsdev.network.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val appRepository: AppRepository
): ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.d(TAG, "exceptionHandler: $exception")
    }

    private val _categoryRes = MutableLiveData<Resource<CategoryListResponse>?>()

    val categoryRes : LiveData<Resource<CategoryListResponse>?>
        get() = _categoryRes

    var categorys= mutableListOf<CategoryModel>()
    var notifyNewCategoriesInsertedLiveData = MutableLiveData<Pair<Int, Int>>()

    fun getCategoryList() {
        _categoryRes.postValue(Resource.loading(null))
        viewModelScope.launch(exceptionHandler) {
            val response = appRepository.getCategoryList()
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!.categories
                val startPosition = categorys.size
                categorys.addAll(data)
                notifyNewCategoriesInsertedLiveData.postValue(Pair(startPosition, data.size))
                _categoryRes.postValue(Resource.success(null))
            } else {
                _categoryRes.postValue(Resource.error(response.errorBody().toString(), response.code(), null))
            }
        }
    }

}