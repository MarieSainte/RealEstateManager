package com.example.realestatemanager.services.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import com.example.realestatemanager.models.InterestEntity

@Dao
interface InterestDao {

    @Insert(onConflict = IGNORE)
    suspend fun insert(interest: InterestEntity)

}