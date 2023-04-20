package com.example.realestatemanager

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.realestatemanager.models.HousingEntity
import com.example.realestatemanager.services.room.AppDatabase
import com.example.realestatemanager.services.room.HousingDao
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HousingDaoTest {

    private lateinit var dao: HousingDao
    private lateinit var mDb: AppDatabase
    private lateinit var housing :HousingEntity

    @Before
    fun createDb() {
        mDb = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = mDb.housingDao()
        housing = HousingEntity(
            1,
            "My description test"
        )
    }

    @After
    fun cleanUp() {
        mDb.close()
    }

    @Test
    fun testInsert() = runBlocking {
        // act
        dao.insert(housing)
        val housingList = dao.getAllFlow().first()
        // assert
        assertEquals(housingList.first().housing.description, "My description test")
    }

    @Test
    fun testUpdate() = runBlocking {

        // act
        dao.insert(housing)
        housing.description = "update test"
        dao.update(housing)
        val housingListUpdated = dao.getAllFlow().first()

        // assert
        assertEquals(housingListUpdated.first().housing.description, "update test")
    }
}