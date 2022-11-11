package com.seabird.whatsdev.ui.groups

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seabird.whatsdev.network.model.GroupData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroupViewModel : ViewModel() {

    val groupList = MutableLiveData<List<GroupData>>()

    fun getGroupList() {
        viewModelScope.launch(Dispatchers.IO) {
            val groupDatas = mutableListOf<GroupData>()
            val group1 = GroupData("Crypto Currencies", "Stocks", "https://cdn.pixabay.com/photo/2017/01/25/12/31/bitcoin-2007769_1280.jpg")
            val group2 = GroupData("YouTube Promotion", "Social", "https://img.freepik.com/free-photo/pile-3d-play-button-logos_1379-880.jpg")
            val group3 = GroupData("Funny Videos", "Entertainment", "https://img.freepik.com/free-vector/group-people-watching-funny-video-laughing-cartoon_1284-33366.jpg")
            val group4 = GroupData("Stocks and Recommendation", "Stocks", "https://img.freepik.com/free-vector/employee-working-office-interior-workplace-flat-vector-illustration_1150-37453.jpg")
            val group5 = GroupData("Movie Updates", "Entertainment", "https://img.freepik.com/free-vector/film-strip-background-with-clapper-board_1017-33456.jpg")
            val group6 = GroupData("Food Review", "Education", "https://img.freepik.com/free-photo/aloo-paratha-gobi-paratha-also-known-as-potato-cauliflower-stuffed-flatbread-dish-originating-from-indian-subcontinent_466689-76173.jpg")
            val group7 = GroupData("Friends & Family", "Friendship", "https://img.freepik.com/free-photo/silhouette-group-people-have-fun-top-mountain-near-tent-during-sunset_146671-18472.jpg")
            val group8 = GroupData("Education Videos", "Education", "https://img.freepik.com/free-vector/students-watching-webinar-computer-studying-online_74855-15522.jpg")
            val group9 = GroupData("Top Games", "Games", "https://img.freepik.com/free-vector/character-playing-videogame_52683-36686.jpg")
            val group10 = GroupData("Foot Ball Fans", "Sports", "https://img.freepik.com/free-vector/football-background-design_1176-204.jpg")
            val group11 = GroupData("Latest Technologies", "Technology", "https://img.freepik.com/free-photo/medium-shot-man-wearing-vr-glasses_23-2149126949.jpg")
            val group12 = GroupData("Fitness Training", "Education", "https://img.freepik.com/free-vector/blue-geometric-woman-running-illustration_1284-52845.jpg")
            val group13 = GroupData("Facebook Users", "Social", "https://img.freepik.com/free-photo/pile-3d-facebook-logos_1379-875.jpg")
            val group14 = GroupData("World News", "News", "https://img.freepik.com/free-vector/blue-breaking-news-tv-background_1017-14201.jpg")
            groupDatas.add(group1)
            groupDatas.add(group2)
            groupDatas.add(group3)
            groupDatas.add(group4)
            groupDatas.add(group5)
            groupDatas.add(group6)
            groupDatas.add(group7)
            groupDatas.add(group8)
            groupDatas.add(group9)
            groupDatas.add(group10)
            groupDatas.add(group11)
            groupDatas.add(group12)
            groupDatas.add(group13)
            groupDatas.add(group14)
            groupList.postValue(groupDatas)
        }
    }
}