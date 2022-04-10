package uk.fisherthewol.smartkart

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager

class AverageSpeedModel(val locationMan: LocationManager): LocationListener {
    private val locations: MutableList<Location> = emptyList<Location>().toMutableList()
    init {
        locationMan.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            5.0f,
            this)
    }

    /**
     * Calculate average speed of device.
     *
     * Depends on [Location.getSpeed] existing on the locations.
     *
     * @return Average speed in m/s. TODO: Check actual units.
     */
    val averageSpeed: Double
        get() = when {
            locations.isEmpty() -> 0.0
            else -> locations.map { it.speed }.average()
        }

    override fun onLocationChanged(loc: Location) {
        if (loc.hasSpeed()) {
            this.locations.add(loc)
        }
    }
}