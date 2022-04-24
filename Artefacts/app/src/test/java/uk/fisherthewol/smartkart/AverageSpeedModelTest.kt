package uk.fisherthewol.smartkart

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import androidx.core.content.getSystemService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock


/**
 * See https://developer.android.com/training/testing/local-tests#mocking-dependencies
 */
@RunWith(MockitoJUnitRunner::class)
class AverageSpeedModelTest {
    private lateinit var speedModelUnderTest: AverageSpeedModel
    private var mockLocationManager: LocationManager
    private var mockContext: Context

    init {
        mockContext = mock()
        mockLocationManager = mockContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @Test
    fun initAverageSpeedModel_withTestLocationManager() {

    }
}