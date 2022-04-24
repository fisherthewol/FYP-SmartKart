package uk.fisherthewol.smartkart

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.location.provider.ProviderProperties
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AverageSpeedModelTest {
    private var context: Context
    private var locationManager: LocationManager
    var locs = mutableListOf<Location>(
        // Time code: https://stackoverflow.com/a/10178116 Nesim Razon CC BY-SA 3.0
        Location(LocationManager.GPS_PROVIDER).apply {
            latitude = 53.474458
            longitude = -1.502204
            accuracy = 1.0F
            speed = AverageSpeedModel.unitToMS(45.0).toFloat()
            time = System.currentTimeMillis() / 1000L
            elapsedRealtimeNanos = 10
        },
        Location(LocationManager.GPS_PROVIDER).apply {
            latitude = 53.481802
            longitude = -1.494988
            accuracy = 1.0F
            speed = AverageSpeedModel.unitToMS(45.0).toFloat()
            time = (System.currentTimeMillis() / 1000L) + 10
            elapsedRealtimeNanos = 10
        },
        Location(LocationManager.GPS_PROVIDER).apply {
            latitude = 53.487391368230696
            longitude = -1.4928507664039068
            accuracy = 1.0F
            speed = AverageSpeedModel.unitToMS(26.0).toFloat()
            time = (System.currentTimeMillis() / 1000L) + 20
            elapsedRealtimeNanos = 10
        },
    )

    init {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.addTestProvider(
            LocationManager.GPS_PROVIDER,
            true,
            true,
            false,
            false,
            true,
            true,
            true,
            ProviderProperties.POWER_USAGE_HIGH,
            ProviderProperties.ACCURACY_FINE
        )
    }

    @Test
    fun initialisesToProvidedSpeedLimitAndNotTracking() {
        val modelUnderTest = AverageSpeedModel(locationManager, MutableLiveData(30))
        assertEquals(30,modelUnderTest.getSpeedLimit().value)
        assertEquals(false, modelUnderTest.getTrackingBool().value)
    }

    @Test
    fun initialisesToNotOverSpeedlimit() {
        val modelUnderTest = AverageSpeedModel(locationManager, MutableLiveData(30))
        assertEquals(false, modelUnderTest.getOverSpeedLimit().value)
    }

    @Test
    fun checkBasicTracking() {
        // Run test on a main thread with appropriate looper. Adapted from https://stackoverflow.com/a/66564234, Hitesh Sahu, CC BY-SA 4.0
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val modelUnderTest = AverageSpeedModel(locationManager, MutableLiveData(50))
            // Should be false first.
            assertEquals(false, modelUnderTest.getTrackingBool().value)
            modelUnderTest.startTracking(5)
            // Add false data.
            locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, locs[0])
            locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, locs[1])
            locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, locs[2])
            assertEquals(17, modelUnderTest.getAverageSpeed().value?.toInt())
        }
    }
}