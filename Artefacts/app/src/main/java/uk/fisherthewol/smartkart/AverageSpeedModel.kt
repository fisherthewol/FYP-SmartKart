package uk.fisherthewol.smartkart

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log

/***
 * Class to model average speed.
 */
class AverageSpeedModel(locationMan: LocationManager): LocationListener {
    private val locations: MutableList<Location> = emptyList<Location>().toMutableList()
    init {
        try {
            locationMan.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0, // Minimum time between updates.
                5.0f, // Minimum distance between updateds.
                this)
        } catch (e: SecurityException) {
            Log.e("AverageSpeedModel", "Activity creating model did not appropriately grep permission.", e)
        }
    }

    /**
     * Calculate average speed of device.
     *
     * Depends on [Location.hasSpeed] == true.
     *
     * @return Average speed in m/s.
     */
    var averageSpeed: Double = 0.0

    /**
     * On location change, check if we can get a speed; if so, add it to the list,
     * and calculate average speed.
     */
    override fun onLocationChanged(loc: Location) {
        if (loc.hasSpeed()) {
            this.locations.add(loc)
            this.averageSpeed = this.locations.map { it.speed }.average()
        }
    }
}