package com.seabird.whatsdev.ui.category

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seabird.whatsdev.network.model.CategoryData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {

    var categorys= mutableListOf<CategoryData>()
    var notifyNewCategoryInsertedLiveData = MutableLiveData<Int>()

    fun getCategoryList() {
        viewModelScope.launch(Dispatchers.IO) {
            val category12 = CategoryData("Arts & Design", "https://images.pexels.com/photos/3951832/pexels-photo-3951832.jpeg")
            val category13 = CategoryData("Audios & Videos", "https://images.pexels.com/photos/5367423/pexels-photo-5367423.jpeg")
            val category14 = CategoryData("Business & Finance", "https://images.pexels.com/photos/7433871/pexels-photo-7433871.jpeg")
            val category1 = CategoryData("Education", "https://images.pexels.com/photos/267885/pexels-photo-267885.jpeg")
            val category2 = CategoryData("Entertainment", "https://images.pexels.com/photos/1179581/pexels-photo-1179581.jpeg")
            val category3 = CategoryData("Friendship", "https://images.pexels.com/photos/935835/pexels-photo-935835.jpeg")
            val category4 = CategoryData("Games", "https://img.freepik.com/free-vector/character-playing-videogame_52683-36686.jpg")
            val category5 = CategoryData("Love & Dating", "https://images.pexels.com/photos/1690732/pexels-photo-1690732.jpeg")
            val category6 = CategoryData("News", "https://img.freepik.com/free-vector/blue-breaking-news-tv-background_1017-14201.jpg")
            val category7 = CategoryData("Social Media", "https://img.freepik.com/free-vector/colorful-icons-set_79603-1266.jpg")
            val category8 = CategoryData("Medical", "https://images.pexels.com/photos/161688/medical-tablets-pills-drug-161688.jpeg")
            val category9 = CategoryData("Sports", "https://images.pexels.com/photos/69773/uss-nimitz-basketball-silhouettes-sea-69773.jpeg")
            val category10 = CategoryData("Technology", "https://img.freepik.com/free-photo/medium-shot-man-wearing-vr-glasses_23-2149126949.jpg")
            val category11 = CategoryData("Other", "https://img.freepik.com/free-vector/man-woman-touching-each-other-when-work-is-done_1150-35029.jpg")
            addDataToUI(category12)
            addDataToUI(category13)
            addDataToUI(category14)
            addDataToUI(category1)
            addDataToUI(category2)
            addDataToUI(category3)
            addDataToUI(category4)
            addDataToUI(category5)
            addDataToUI(category6)
            addDataToUI(category7)
            addDataToUI(category8)
            addDataToUI(category9)
            addDataToUI(category10)
            addDataToUI(category11)
        }
    }

    private fun addDataToUI(category: CategoryData) {
        categorys.add(category)
        viewModelScope.launch(Dispatchers.Main) {
            notifyNewCategoryInsertedLiveData.value = categorys.size
        }
    }

}