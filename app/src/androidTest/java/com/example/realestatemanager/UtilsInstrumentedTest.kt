package com.example.realestatemanager

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.realestatemanager.services.Utils
import com.example.realestatemanager.ui.MainActivity
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class UtilsInstrumentedTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    @SmallTest
    fun isInternetAvailableTest() {
        // Context of the app under test.
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals(true, Utils.isInternetAvailable(context))
    }

}