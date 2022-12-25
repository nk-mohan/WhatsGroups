package com.seabird.whatsdev.ui.groups

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seabird.whatsdev.network.model.*
import com.seabird.whatsdev.network.other.Resource
import com.seabird.whatsdev.network.repository.AppRepository
import com.seabird.whatsdev.utils.AppConstants
import com.seabird.whatsdev.utils.SharedPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val appRepository: AppRepository
): ViewModel() {

    private val _registerRes = MutableLiveData<Resource<RegisterResponse>>()

    val registerRes : LiveData<Resource<RegisterResponse>>
        get() = _registerRes

    var groups = mutableListOf<GroupData>()
    var notifyNewGroupInsertedLiveData = MutableLiveData<Int>()

    init {
        groups = mutableListOf()
        notifyNewGroupInsertedLiveData = MutableLiveData<Int>()
        checkAndRegister()
    }

    private fun checkAndRegister() {
        viewModelScope.launch {
            if (SharedPreferenceManager.getStringValue(AppConstants.REFRESH_TOKEN).isNullOrBlank()) {
                if (!SharedPreferenceManager.getBooleanValue(AppConstants.IS_REGISTERED)) {
                    _registerRes.postValue(Resource.loading(null))
                    appRepository.registerUser(
                        RegisterRequest(
                            device_id = SharedPreferenceManager.getStringValue(AppConstants.DEVICE_ID),
                            device_model = Build.MODEL,
                            device_os_version = Build.VERSION.RELEASE
                        )
                    ).let {
                        if (it.isSuccessful) {
                            SharedPreferenceManager.setBooleanValue(AppConstants.IS_REGISTERED, true)
                            it.body()?.user?.let { user ->
                                SharedPreferenceManager.setStringValue(AppConstants.ACCESS_TOKEN, user.access_token)
                                SharedPreferenceManager.setStringValue(AppConstants.REFRESH_TOKEN, user.refresh_token)
                            }
                            getGroupList()
                        } else {
                            _registerRes.postValue(Resource.error(it.errorBody().toString(), it.code(), null))
                        }
                    }
                } else {
                    _registerRes.postValue(Resource.loading(null))
                    appRepository.loginUser(
                        LoginRequest(device_id = SharedPreferenceManager.getStringValue(AppConstants.DEVICE_ID))
                    ).let {
                        if (it.isSuccessful) {
                            SharedPreferenceManager.setBooleanValue(AppConstants.IS_REGISTERED, true)
                            it.body()?.user?.let { user ->
                                SharedPreferenceManager.setStringValue(AppConstants.ACCESS_TOKEN, user.access_token)
                                SharedPreferenceManager.setStringValue(AppConstants.REFRESH_TOKEN, user.refresh_token)
                            }
                            getGroupList()
                        } else {
                            _registerRes.postValue(Resource.error(it.errorBody().toString(), it.code(), null))
                        }
                    }
                }
            } else {
                getGroupList()
            }
        }
    }

    fun getGroupList() {
        if (groups.isEmpty())
            viewModelScope.launch(Dispatchers.IO) {
                val group1 = GroupData(
                    "Crypto Currencies",
                    "Stocks",
                    "https://cdn.pixabay.com/photo/2017/01/25/12/31/bitcoin-2007769_1280.jpg",
                    1244
                )
                val group2 = GroupData(
                    "YouTube Promotion",
                    "Social",
                    "https://img.freepik.com/free-photo/pile-3d-play-button-logos_1379-880.jpg",
                    2234
                )
                val group3 = GroupData(
                    "Funny Videos",
                    "Entertainment",
                    "https://img.freepik.com/free-vector/group-people-watching-funny-video-laughing-cartoon_1284-33366.jpg",
                    23214
                )
                val group4 = GroupData(
                    "Stocks and Recommendation",
                    "Stocks",
                    "https://img.freepik.com/free-vector/employee-working-office-interior-workplace-flat-vector-illustration_1150-37453.jpg",
                    35234
                )
                val group5 = GroupData(
                    "Movie Updates",
                    "Entertainment",
                    "https://img.freepik.com/free-vector/film-strip-background-with-clapper-board_1017-33456.jpg",
                    9843
                )
                val group6 = GroupData(
                    "Food Review",
                    "Education",
                    "https://img.freepik.com/free-photo/aloo-paratha-gobi-paratha-also-known-as-potato-cauliflower-stuffed-flatbread-dish-originating-from-indian-subcontinent_466689-76173.jpg",
                    5234
                )
                val group7 = GroupData(
                    "Friends & Family",
                    "Friendship",
                    "https://img.freepik.com/free-photo/silhouette-group-people-have-fun-top-mountain-near-tent-during-sunset_146671-18472.jpg",
                    2346
                )
                val group8 = GroupData(
                    "Education Videos",
                    "Education",
                    "https://img.freepik.com/free-vector/students-watching-webinar-computer-studying-online_74855-15522.jpg",
                    873
                )
                val group9 = GroupData(
                    "Top Games",
                    "Games",
                    "https://img.freepik.com/free-vector/character-playing-videogame_52683-36686.jpg",
                    4235
                )
                val group10 = GroupData(
                    "Foot Ball Fans",
                    "Sports",
                    "https://img.freepik.com/free-vector/football-background-design_1176-204.jpg",
                    4354
                )
                val group11 = GroupData(
                    "Latest Technologies",
                    "Technology",
                    "https://img.freepik.com/free-photo/medium-shot-man-wearing-vr-glasses_23-2149126949.jpg",
                    3454
                )
                val group12 = GroupData(
                    "Fitness Training",
                    "Education",
                    "https://img.freepik.com/free-vector/blue-geometric-woman-running-illustration_1284-52845.jpg",
                    23544
                )
                val group13 = GroupData(
                    "Facebook Users",
                    "Social",
                    "https://img.freepik.com/free-photo/pile-3d-facebook-logos_1379-875.jpg",
                    2341
                )
                val group14 = GroupData(
                    "World News",
                    "News",
                    "https://img.freepik.com/free-vector/blue-breaking-news-tv-background_1017-14201.jpg",
                    334
                )
                addDataToUI(group1)
                addDataToUI(group2)
                addDataToUI(group3)
                addDataToUI(group4)
                addDataToUI(group5)
                addDataToUI(group6)
                addDataToUI(group7)
                addDataToUI(group8)
                addDataToUI(group9)
                addDataToUI(group10)
                addDataToUI(group11)
                addDataToUI(group12)
                addDataToUI(group13)
                addDataToUI(group14)
            }
    }

    private fun addDataToUI(group: GroupData) {
        groups.add(group)
        viewModelScope.launch(Dispatchers.Main) {
            notifyNewGroupInsertedLiveData.value = groups.size
        }
    }
}