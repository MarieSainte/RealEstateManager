package com.example.realestatemanager.services.room

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.realestatemanager.models.HousingEntity
import com.example.realestatemanager.models.HousingWithInterest
import com.example.realestatemanager.models.HousingWithPhoto
import kotlinx.coroutines.flow.Flow


@Dao
interface HousingDao{

    // GET ALL HOUSINGS
    @Query("select * from housing")
    fun getAllFlow(): Flow<List<HousingWithPhoto>>

    // GET A FILTERED HOUSING LIST
    @RawQuery(observedEntities = [HousingEntity::class])
    fun getFilterFlow(settings: SupportSQLiteQuery): Flow<List<HousingWithPhoto>>

    @Query("select * from housing where roomId = :housingId")
    fun getHousingFlow(housingId : Long): Flow<HousingWithPhoto>

    // GET A SPECIFIC HOUSING
    @Query("select * from housing where roomId = :housingId")
    fun getHousingLiveData(housingId : String): HousingEntity

    // GET A SPECIFIC HOUSING
    @Query("select * from housing where roomId = :housingId")
    fun getHousing(housingId : Long): HousingWithPhoto

    // FOR THE CONTENT PROVIDER
    @Query("select * from housing where roomId = :housingId")
    fun getHousingWithCursor(housingId : String): Cursor

    @Transaction
    @Query("SELECT * FROM housing where roomId = :housingId")
    fun getPhotoInHousing(housingId : Long): HousingWithPhoto
    @Transaction
    @Query("SELECT * FROM housing where roomId = :roomId")
    fun getPhotoInHousingLive(roomId : Long): LiveData<HousingWithPhoto>
    @Transaction
    @Query("SELECT * FROM housing where roomId = :housingId")
    fun getInterestInHousing(housingId : Long): HousingWithInterest

    @Insert
    suspend fun insert(housing: HousingEntity) :Long

    @Update
    suspend fun update(housing: HousingEntity)
}
