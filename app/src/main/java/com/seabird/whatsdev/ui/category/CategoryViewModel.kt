package com.seabird.whatsdev.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seabird.whatsdev.network.model.CategoryListResponse
import com.seabird.whatsdev.network.model.CategoryModel
import com.seabird.whatsdev.network.other.Resource
import com.seabird.whatsdev.network.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val appRepository: AppRepository
): ViewModel() {

    private val _categoryRes = MutableLiveData<Resource<CategoryListResponse>?>()

    val categoryRes : LiveData<Resource<CategoryListResponse>?>
        get() = _categoryRes

    var categorys= mutableListOf<CategoryModel>()
    var notifyNewCategoriesInsertedLiveData = MutableLiveData<Pair<Int, Int>>()

    fun getCategoryList() {
        _categoryRes.postValue(Resource.loading(null))
        viewModelScope.launch(Dispatchers.IO) {
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