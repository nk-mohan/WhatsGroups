package com.seabird.whatsdev.ui.category

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seabird.whatsdev.network.model.CategoryData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {

    val categoryList = MutableLiveData<List<CategoryData>>()

    fun getCategoryList() {
        viewModelScope.launch(Dispatchers.IO) {
            val categoryDatas = mutableListOf<CategoryData>()
            val category1 = CategoryData("Education", "https://img.freepik.com/free-vector/students-watching-webinar-computer-studying-online_74855-15522.jpg")
            val category2 = CategoryData("Entertainment", "https://img.freepik.com/free-vector/realistic-bollywood-cinema-sign_52683-35071.jpg")
            val category3 = CategoryData("Friendship", "https://img.freepik.com/free-vector/group-young-people-posing-photo_52683-18823.jpg")
            val category4 = CategoryData("Games", "https://img.freepik.com/free-vector/character-playing-videogame_52683-36686.jpg")
            val category5 = CategoryData("Love & Dating", "https://img.freepik.com/free-vector/dating-couple-enjoying-romantic-dinner_74855-5233.jpg")
            val category6 = CategoryData("News", "https://img.freepik.com/free-vector/blue-breaking-news-tv-background_1017-14201.jpg")
            val category7 = CategoryData("Social Media", "https://img.freepik.com/free-vector/colorful-icons-set_79603-1266.jpg")
            val category8 = CategoryData("Stocks", "https://img.freepik.com/free-vector/investor-with-laptop-monitoring-growth-dividends-trader-sitting-stack-money-investing-capital-analyzing-profit-graphs-vector-illustration-finance-stock-trading-investment_74855-8432.jpg")
            val category9 = CategoryData("Sports", "https://img.freepik.com/free-photo/sports-tools_53876-138077.jpg")
            val category10 = CategoryData("Technology", "https://img.freepik.com/free-photo/medium-shot-man-wearing-vr-glasses_23-2149126949.jpg")
            val category11 = CategoryData("Other", "https://img.freepik.com/free-vector/man-woman-touching-each-other-when-work-is-done_1150-35029.jpg")
            categoryDatas.add(category1)
            categoryDatas.add(category2)
            categoryDatas.add(category3)
            categoryDatas.add(category4)
            categoryDatas.add(category5)
            categoryDatas.add(category6)
            categoryDatas.add(category7)
            categoryDatas.add(category8)
            categoryDatas.add(category9)
            categoryDatas.add(category10)
            categoryDatas.add(category11)
            categoryList.postValue(categoryDatas)
        }
    }

}