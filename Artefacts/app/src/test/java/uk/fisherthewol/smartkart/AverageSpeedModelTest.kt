package uk.fisherthewol.smartkart

import android.content.Context
import android.location.LocationManager
import android.location.provider.ProviderProperties
import androidx.lifecycle.MutableLiveData
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock


/**
 * See https://developer.android.com/training/testing/local-tests#mocking-dependencies
 */
@RunWith(MockitoJUnitRunner::class)
class AverageSpeedModelTest {
    private var mockLocationManager: LocationManager
    private var mockContext: Context

    init {
        mockContext = mock()
        mockLocationManager = mockContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @Test
    fun initAverageSpeedModel_withTestLocationManager() {
        val prop = ProviderProperties.Builder().apply {
            setAccuracy(ProviderProperties.ACCURACY_FINE)
            setHasSpeedSupport(true)
            setPowerUsage(ProviderProperties.POWER_USAGE_HIGH)
        }.build()
        mockLocationManager.addTestProvider(LocationManager.GPS_PROVIDER, prop)
        val speedModelUnderTest = AverageSpeedModel(mockLocationManager, MutableLiveData(30))
        assertEquals(speedModelUnderTest.getSpeedLimit().value, 30)
    }
}