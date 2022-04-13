package uk.fisherthewol.smartkart

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log

/**
 * Class to model average speed.
 *
 * Consider https://developer.android.com/training/location/request-updates
 */
class AverageSpeedModel(private val locationMan: LocationManager): LocationListener {
    private val locations: MutableList<Location> = emptyList<Location>().toMutableList()
    var averageSpeed: Double = 0.0

    /**
     * Start tracking average speed.
     *
     * Users should call [stopTracking] when they wish to stop.
     */
    fun startTracking() {
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
     * Stop tracking average speed.
     */
    fun stopTracking() {
        locationMan.removeUpdates(this)
    }

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

    /**
     * Handle batched location updates.
     *
     * TODO: Check if batches are ordered or not.
     */
    override fun onLocationChanged(locations: MutableList<Location>) {
        for (loc in locations) {
            if (loc.hasSpeed()) {
                this.locations.add(loc)
                this.averageSpeed = this.locations.map { it.speed }.average()
            }
        }
    }
}