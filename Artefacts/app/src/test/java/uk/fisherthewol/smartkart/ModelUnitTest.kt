package uk.fisherthewol.smartkart

import android.location.Location
import android.location.LocationManager
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

    private fun `Generate Model To Test`(): AverageSpeedModel {
        val mockLocationManager: LocationManager = mock()
        return AverageSpeedModel(mockLocationManager)
    }

    @Test
    fun `Single location update returns correct AverageSpeed`() {
        val modelUnderTest = `Generate Model To Test`()
        val singleLocation: Location = mock {
            on {hasSpeed()} doReturn true
            on {speed} doReturn constantSpeedms
        }
        modelUnderTest.onLocationChanged(singleLocation)
        assertEquals(constantSpeedms.toDouble(), modelUnderTest.getAverageSpeed().value)
    }

    @Test
    fun `Batched location updates at constant speed returns correct AverageSpeed`() {
        val modelUnderTest = `Generate Model To Test`()
        val singleLocation: Location = mock {
            on {hasSpeed()} doReturn true
            on {speed} doReturn constantSpeedms
        }
        val multipleLocations: MutableList<Location> = buildList {
            for (i in 0..10) {
                add(singleLocation)
            }
        } as MutableList<Location>
        modelUnderTest.onLocationChanged(multipleLocations)
        assertEquals(constantSpeedms.toDouble(), modelUnderTest.getAverageSpeed().value)
    }

    @Test
    fun `Two locations with different speeds returns correct AverageSpeed`() {
        val modelUnderTest = `Generate Model To Test`()
        val loc1: Location = mock {
            on {hasSpeed()} doReturn true
            on {speed} doReturn 10.0F
        }
        val loc2: Location = mock {
            on {hasSpeed()} doReturn true
            on {speed} doReturn 20.0F
        }
        modelUnderTest.onLocationChanged(loc1)
        modelUnderTest.onLocationChanged(loc2)
        assertEquals(15.0, modelUnderTest.getAverageSpeed().value!!, 0.000)
    }

    @Test
    fun `Ten locations with different speeds returns correct AverageSpeed`() {
        val modelUnderTest = `Generate Model To Test`()
        val locations: List<Location> = buildList {
            for (i in 0..10) {
                val loc: Location = mock {
                    on {hasSpeed()} doReturn true
                    on {speed} doReturn i.toFloat()
                }
                add(loc)
            }
        }
        for (loc in locations) {
            modelUnderTest.onLocationChanged(loc)
        }
        assertEquals(5.0, modelUnderTest.getAverageSpeed().value!!, 0.000)
    }
}