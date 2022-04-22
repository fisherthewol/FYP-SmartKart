package uk.fisherthewol.smartkart

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Class to model average speed.
 *
 * Consider https://developer.android.com/training/location/request-updates, https://developer.android.com/topic/libraries/architecture/viewmodel
 */
class AverageSpeedModel(private val locationMan: LocationManager, private var speedLimit: MutableLiveData<Int> = MutableLiveData(0)): ViewModel(), LocationListener {
    private val locations: MutableList<Location> = emptyList<Location>().toMutableList()
    private var averageSpeed: MutableLiveData<Double> = MutableLiveData(0.0)
    var trackingBool: MutableLiveData<Boolean> = MutableLiveData(false)

    /**
     * Get average speed as regular LiveData.
     */
    fun getAverageSpeed(): LiveData<Double> = averageSpeed
    fun getSpeedLimit(): LiveData<Int> = speedLimit
    
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
                5.0f, // Minimum distance between updates.
                this)
        } catch (e: SecurityException) {
            Log.e("AverageSpeedModel", "Creator of model did not appropriately grep permission.", e)
        }
    }

    /**
     * Stop tracking average speed.
     */
    fun stopTracking() {
        // Consider: Request flush here.
        locationMan.removeUpdates(this)
    }

    /**
     * On location change, check if we can get a speed; if so, add it to the list,
     * and calculate average speed.
     */
    override fun onLocationChanged(loc: Location) {
        if (loc.hasSpeed()) {
            this.locations.add(loc)
            this.averageSpeed.value = this.locations.map { it.speed }.average()
        }
    }

    /**
     * Handle batched location updates.
     */
    override fun onLocationChanged(locations: MutableList<Location>) {
        for (loc in locations) {
            if (loc.hasSpeed()) {
                this.locations.add(loc)
            }
        }
        this.averageSpeed.value = this.locations.map { it.speed }.average()
    }
}