package com.example.realestatemanager.services.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.realestatemanager.models.*
import com.example.realestatemanager.services.Utils

@Database(
    entities = [
        HousingEntity::class,
        PhotoEntity::class,
        InterestEntity::class,
        HousingInterestCrossRef::class], version = 8)
@TypeConverters(Utils::class)
abstract class AppDatabase: RoomDatabase(){

    abstract fun housingDao(): HousingDao
    abstract fun photoDao(): PhotoDao
    abstract fun interestDao(): InterestDao
    abstract fun housingInterestCrossRefDao(): HousingInterestCrossRefDao

    companion object{
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null){
                INSTANCE = Room.databaseBuilder(context, AppDatabase::class.java, "housing.db")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE as AppDatabase
        }
    }
}
