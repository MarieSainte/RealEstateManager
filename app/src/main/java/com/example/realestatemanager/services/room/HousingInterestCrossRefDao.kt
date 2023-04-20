package com.example.realestatemanager.services.room

import androidx.room.Dao
import androidx.room.Insert
import com.example.realestatemanager.models.HousingInterestCrossRef

@Dao
interface HousingInterestCrossRefDao {

    @Insert
    suspend fun insert(housingInterestCrossRef: HousingInterestCrossRef)
}