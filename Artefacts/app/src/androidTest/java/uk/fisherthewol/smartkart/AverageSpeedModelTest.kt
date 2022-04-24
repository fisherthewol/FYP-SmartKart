package uk.fisherthewol.smartkart

import android.content.Context
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
    fun initialisesToProvidedSpeedLimit() {
        val modelUnderTest = AverageSpeedModel(locationManager, MutableLiveData(30))
        assertEquals(modelUnderTest.getSpeedLimit().value, 30)
    }
}