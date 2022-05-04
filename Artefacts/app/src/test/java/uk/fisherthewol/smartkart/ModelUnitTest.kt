package uk.fisherthewol.smartkart

import android.location.Location
import android.location.LocationManager
import androidx.arch.core.executor.TaskExecutor
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class ModelUnitTest {
    /* Adapated from https://medium.com/pxhouse/unit-testing-with-mutablelivedata-22b3283a7819 G Venios 2017.*/
    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    val constantSpeedms = 11.0F
    @Test
    fun `Single location update gets correct AverageSpeed`() {
        val mockLocationManager: LocationManager = mock()
        val modelUnderTest = AverageSpeedModel(mockLocationManager)
        val singleLocation: Location = mock {
            on {hasSpeed()} doReturn true
            on {speed} doReturn constantSpeedms
        }
        modelUnderTest.onLocationChanged(singleLocation)
        assertEquals(constantSpeedms.toDouble(), modelUnderTest.getAverageSpeed().value)
    }
}