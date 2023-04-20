package com.example.realestatemanager

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import androidx.room.Room.inMemoryDatabaseBuilder
import androidx.test.platform.app.InstrumentationRegistry
import com.example.realestatemanager.services.provider.HouseContentProvider.Companion.instance
import com.example.realestatemanager.services.room.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ContentProviderTestKotlin {


    private var mContentResolver: ContentResolver? = null
    private lateinit var appDatabase: AppDatabase
    private val ioScope = CoroutineScope(Dispatchers.IO)

    @Before
     fun setUp(){

        appDatabase = inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        mContentResolver = InstrumentationRegistry.getInstrumentation().context
            .contentResolver
    }
    @After
    fun closeDB() {
        appDatabase.close()
    }

    @Test
    fun insertAndGetItem() {
        ioScope.launch {
            val contentProvider = instance
            mContentResolver!!.insert(contentProvider.URI_HOUSING, generateItem())
            val cursor = mContentResolver!!.query(
                ContentUris.withAppendedId(
                    contentProvider.URI_HOUSING,
                    1
                ), null, null, null, null
            )
            Assert.assertNull(cursor)
            Assert.assertEquals(cursor!!.count.toLong(), 1)
            Assert.assertTrue(cursor.moveToFirst())
            Assert.assertEquals(
                cursor.getString(cursor.getColumnIndexOrThrow("description")),
                "My description"
            )
            cursor.close()
        }
    }

    private fun generateItem(): ContentValues? {
        val values = ContentValues()
        values.put("description", "My description")
        values.put("status", "Available")
        return values
    }
}