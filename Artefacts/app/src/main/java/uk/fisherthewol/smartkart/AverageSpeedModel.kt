package uk.fisherthewol.smartkart

import android.location.Location
import android.location.LocationManager
import androidx.core.util.Consumer

class AverageSpeedModel(private val locationMan: LocationManager) {
    private val values: MutableList<Location> = emptyList<Location>().toMutableList()

    /**
     * Calculate average speed of device.
     *
     * Depends on [Location.getSpeed] existing on the locations.
     *
     * @return Average speed in m/s. TODO: Check actual units.
     */
    val averageSpeed: Double
        get() = when {
            values.isEmpty() -> 0.0
            else -> values.filter { it.hasSpeed() }.map { it.speed }.average()
        }

    /**
     * Consumer to execute getCurrentLocation with.
     *
     * Working from https://stuff.mit.edu/afs/sipb/project/android/docs/training/basics/location/locationmanager.html
     */
    val locationConsumer = Consumer<Location> {
        this.values.add(it)
    }
}