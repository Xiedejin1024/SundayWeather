package com.sundayweather.android.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sundayweather.android.logic.Repository
import com.sundayweather.android.logic.dao.PlaceDao
import com.sundayweather.android.logic.model.Place

class PlaceViewModel : ViewModel() {


    private val searchLiveData = MutableLiveData<String>()
    val placeList = ArrayList<Place>()

    val placeLiveData = Transformations.switchMap(searchLiveData) { query ->
        Repository.searchPlaces(query)
    }

    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }

    fun savePlace(place: Place) = Repository.savePlace(place)

    fun getSavePlace() = Repository.getSavePlace()

    fun isPlaceSaved() = Repository.isPlaceSaved()

}