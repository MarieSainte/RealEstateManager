package com.example.realestatemanager.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.realestatemanager.models.HousingWithPhoto
import com.example.realestatemanager.services.repositories.HousingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface MainViewModelAbstract{

    val housingListFlow: Flow<List<HousingWithPhoto>>
    fun housingFilterFlow(settings: SimpleSQLiteQuery) : Flow<List<HousingWithPhoto>>
    fun getExpandedSearch(): Boolean?
    fun setExpandedSearch(boolean: Boolean)
    fun getHousingFlow(id: Long): Flow<HousingWithPhoto>
    fun setHousingLiveData(housingWithPhoto: HousingWithPhoto)
    fun loadSimulator(price: Double, deposite: Int, rate: Int, duration: Int) : Int
}

@HiltViewModel
class MainViewModel
@Inject constructor(

    private val housingRepository: HousingRepository,

    ): ViewModel(),MainViewModelAbstract{

    companion object{
        // SAVE DATA FOR DETAIL SCREEN
        var housingIdDetail: Long = -999
        var housingWithPhoto :HousingWithPhoto?=null
    }

    // SAVE HOUSING FROM LIST SCREEN FOR DETAIL SCREEN
    private var _housingLiveData : MutableLiveData<HousingWithPhoto>? = MutableLiveData(null)
    var housingLiveData : LiveData<HousingWithPhoto>? = _housingLiveData

    // EXPAND THE SEARCH VIEW FOR LIST SCREEN
    private val _expandedSearch = MutableLiveData(false)
    var expandedSearch : LiveData<Boolean> = _expandedSearch

    // List of all accommodations
    override val housingListFlow: Flow<List<HousingWithPhoto>> = housingRepository.getAllFlow()

    // GET A SPECIFIC HOUSING
    override fun getHousingFlow(id: Long): Flow<HousingWithPhoto> {
        return housingRepository.getHousingFlow(id)
    }
    //GET FILTER LIST
    override fun housingFilterFlow(settings: SimpleSQLiteQuery): Flow<List<HousingWithPhoto>> {
        return housingRepository.getFilterFlow(settings)
    }
    // MATH TO SIMULATE THE LOAD
    override fun loadSimulator(price : Double, deposite : Int, rate : Int, duration : Int): Int {
        return housingRepository.loadSimulator(price, deposite, rate, duration)
    }
    //-------------
    // GETTER AND SETTER
    //--------------
    override fun setHousingLiveData(housingWithPhoto: HousingWithPhoto) {
        _housingLiveData?.postValue(housingWithPhoto)
    }

    fun getHousingIdDetail(): Long {
        return housingIdDetail
    }
    fun setHousingIdDetail(x: Long?) {
        if (x != null) {
            housingIdDetail = x
        }
    }

    override fun getExpandedSearch(): Boolean {
        return _expandedSearch.value!!
    }
    override fun setExpandedSearch(boolean: Boolean) {
        _expandedSearch.value = boolean
    }

    fun getHousingWithPhoto(): HousingWithPhoto? {
        return housingWithPhoto
    }
    fun setHousingWithPhoto(x: HousingWithPhoto) {
        housingWithPhoto = x
    }
}

