package uk.fisherthewol.smartkart

import android.location.Location
import android.location.LocationManager

class AverageSpeedModel(private val locationMan: LocationManager) {
    private val values: Array<Location> = emptyArray()

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
}