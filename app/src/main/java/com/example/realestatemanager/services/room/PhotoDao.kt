package com.example.realestatemanager.services.room

import androidx.room.*
import com.example.realestatemanager.models.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(photo: PhotoEntity):Long

    @Query("SELECT * FROM photo where roomId = :housingId")
    fun getAllPhoto(housingId : Long): Flow<List<PhotoEntity>>

    @Delete
    suspend fun delete(photo: PhotoEntity)
}