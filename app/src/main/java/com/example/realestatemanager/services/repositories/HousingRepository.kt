package com.example.realestatemanager.services.repositories

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.realestatemanager.models.*
import com.example.realestatemanager.services.room.HousingDao
import com.example.realestatemanager.services.room.HousingInterestCrossRefDao
import com.example.realestatemanager.services.room.InterestDao
import com.example.realestatemanager.services.room.PhotoDao
import kotlinx.coroutines.flow.Flow

class HousingRepository(
    private val housingDao: HousingDao,
    private val photoDao: PhotoDao,
    private val interestDao: InterestDao,
    private val housingInterestCrossRefDao: HousingInterestCrossRefDao
){

    // GET ALL HOUSINGS IN A FLOW
    fun getAllFlow(): Flow<List<HousingWithPhoto>> = housingDao.getAllFlow()

    // GET A FILTER LIST
    fun getFilterFlow(settings : SupportSQLiteQuery): Flow<List<HousingWithPhoto>> = housingDao.getFilterFlow(settings)

    // GET A SPECIFIC HOUSING WITH PHOTO IN A FLOW
    fun getHousingFlow(housingId: Long): Flow<HousingWithPhoto> = housingDao.getHousingFlow(housingId)

    // GET A SPECIFIC HOUSING WITH PHOTO
    fun getHousing(housingId: Long): HousingWithPhoto = housingDao.getHousing(housingId = housingId)

    // GET A SPECIFIC HOUSING WITH INTEREST
    fun getHousingWithInterest(housingId: Long): HousingWithInterest = housingDao.getInterestInHousing(housingId = housingId)

    //CRUD HOUSING TABLE
    suspend fun insert(housing: HousingEntity):Long = housingDao.insert(housing = housing)
    suspend fun update(housing: HousingEntity) = housingDao.update(housing = housing)

    //CRUD PHOTO TABLE
    suspend fun insert(photo: PhotoEntity):Long = photoDao.insert(photo = photo)

    //CRUD INTEREST TABLE
    suspend fun insert(interest: InterestEntity) = interestDao.insert(interest = interest)

    //CRUD HousingInterestCrossRef TABLE
    suspend fun insert(housingInterestCrossRef: HousingInterestCrossRef) = housingInterestCrossRefDao.insert(housingInterestCrossRef = housingInterestCrossRef)

    // MATH TO SIMULATE THE LOAD
    fun loadSimulator(price: Double, deposite: Int, rate: Int, duration: Int) : Int {
        var result : Double = (price - deposite) * (rate/100)
        result += (price - deposite)
        result /= duration
        return result.toInt()
    }

    suspend fun deletePhoto(photoEntity: PhotoEntity) = photoDao.delete(photo = photoEntity)
}
