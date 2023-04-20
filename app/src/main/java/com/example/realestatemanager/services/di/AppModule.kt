package com.example.realestatemanager.services.di

import android.app.Application
import com.example.realestatemanager.services.repositories.HousingRepository
import com.example.realestatemanager.services.room.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule{

    @Singleton
    @Provides
    fun provideHousingRepository(
        housingDao: HousingDao,
        photoDao: PhotoDao,
        interestDao: InterestDao,
        housingInterestCrossRefDao: HousingInterestCrossRefDao
    ): HousingRepository {
        return HousingRepository(housingDao = housingDao,
            photoDao = photoDao,
            interestDao = interestDao,
            housingInterestCrossRefDao = housingInterestCrossRefDao
        )
    }

    @Singleton
    @Provides
    fun provideApp(
        app: Application
    ): AppDatabase {
        return AppDatabase.getInstance(app)
    }

    @Singleton
    @Provides
    fun provideHousingDao(
        appDatabase: AppDatabase
    ): HousingDao {
        return appDatabase.housingDao()
    }

    @Singleton
    @Provides
    fun providePhotoDao(
        appDatabase: AppDatabase
    ): PhotoDao {
        return appDatabase.photoDao()
    }

    @Singleton
    @Provides
    fun provideInterestDao(
        appDatabase: AppDatabase
    ): InterestDao {
        return appDatabase.interestDao()
    }

    @Singleton
    @Provides
    fun provideHousingInterestCrossRefDao(
        appDatabase: AppDatabase
    ): HousingInterestCrossRefDao {
        return appDatabase.housingInterestCrossRefDao()
    }

}
