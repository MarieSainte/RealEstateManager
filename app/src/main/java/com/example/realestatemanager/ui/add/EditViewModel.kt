package com.example.realestatemanager.ui.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestatemanager.models.*
import com.example.realestatemanager.services.repositories.HousingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


interface EditViewModelAbstract{

    fun getHousing(housingId: Long)
    fun addHousing(housing: HousingEntity)
    fun updateHousing(housing: HousingEntity)
    fun addPhoto(photo: PhotoEntity)
    fun addInterest(interest: InterestEntity)
    fun addHousingInterestCrossRef(housingInterestCrossRef: HousingInterestCrossRef)
    fun getHousingWithInterest(housingId: Long)
    fun deletePhoto(photoEntity: PhotoEntity)
}

@HiltViewModel
class EditViewModel
@Inject constructor(

    private val housingRepository: HousingRepository,

    ): ViewModel(), EditViewModelAbstract {

    // FOR COROUTINE
    private val ioScope = CoroutineScope(Dispatchers.IO)

    var housing : HousingEntity= HousingEntity()
    var photoId = MutableLiveData<Long>()
    var housingId = MutableLiveData<Long>()
    var housingLiveData = MutableLiveData<HousingWithPhoto>()
    var housingWithInterest = MutableLiveData<HousingWithInterest>()

    // GET A SPECIFIC HOUSING TO SET OUR LIVEDATA
    override fun getHousing(housingId: Long) {
        ioScope.launch{
            housingLiveData.postValue(housingRepository.getHousing(housingId))
        }
    }
    // ADD HOUSING IN ROOM AND RETURN THE HOUSING ID IN OUR LIVEDATA
    override fun addHousing(housing: HousingEntity){
        ioScope.launch{
            housingId.postValue(housingRepository.insert(housing = housing))
        }
    }
    // UPDATE HOUSING
    override fun updateHousing(housing: HousingEntity){
        ioScope.launch{
            housingRepository.update(housing = housing)
        }
    }
    // ADD PHOTO IN ROOM
    override fun addPhoto(photo: PhotoEntity){
        ioScope.launch{
            photoId.postValue(housingRepository.insert(photo = photo))
        }
    }
    // ADD INTEREST IN ROOM
    override fun addInterest(interest: InterestEntity){
        ioScope.launch{
            housingRepository.insert(interest = interest)
        }
    }
    // ADD REFERENCE OF HOUSING AND INTEREST IN ROOM
    override fun addHousingInterestCrossRef(housingInterestCrossRef: HousingInterestCrossRef){
        ioScope.launch{
            housingRepository.insert(housingInterestCrossRef = housingInterestCrossRef)
        }
    }
    // GET A SPECIFIC HOUSING WITH INTEREST TO SET OUR LIVEDATA
    override fun getHousingWithInterest(housingId: Long) {
        ioScope.launch{
            housingWithInterest.postValue(housingRepository.getHousingWithInterest(housingId))
        }
    }

    // DELETE A SPECIFIC PHOTO IN ROOM
    override fun deletePhoto(photoEntity: PhotoEntity) {
        ioScope.launch{
            housingRepository.deletePhoto(photoEntity)
        }
    }
}