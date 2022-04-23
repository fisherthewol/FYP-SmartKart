package uk.fisherthewol.smartkart

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

const val ACCEL_REVERSE_INDEX = 5

/**
 * Class to model average speed.
 *
 * Consider https://developer.android.com/training/location/request-updates, https://developer.android.com/topic/libraries/architecture/viewmodel
 */
class AverageSpeedModel(private val locationMan: LocationManager, private var speedLimit: MutableLiveData<Int> = MutableLiveData(0)): ViewModel(), LocationListener {
    private val locations: MutableList<Location> = emptyList<Location>().toMutableList()
    private val averageSpeed: MutableLiveData<Double> = MutableLiveData(0.0)
    private val overSpeedLimit: MutableLiveData<Boolean> = MutableLiveData(false)
    private var predictTime = 1
    val trackingBool: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getAverageSpeed(): LiveData<Double> = averageSpeed
    fun getSpeedLimit(): LiveData<Int> = speedLimit
    fun getOverSpeedLimit(): LiveData<Boolean> = overSpeedLimit
    fun getTrackingBool(): LiveData<Boolean> = trackingBool
    
    /**
     * Start tracking average speed.
     *
     * Users should call [stopTracking] when they wish to stop.
     *
     * @param predictTime Time to predict ahead for speed limit.
     */
    fun startTracking(predictTime: Int) {
        try {
            locationMan.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0, // Minimum time between updates.
                5.0f, // Minimum distance between updates.
                this)
        } catch (e: SecurityException) {
            Log.e("AverageSpeedModel", "Creator of model did not appropriately grep permission.", e)
        }
        this.predictTime = predictTime
    }

    /**
     * Stop tracking average speed.
     */
    fun stopTracking() {
        // Consider: Request flush here.
        locationMan.removeUpdates(this)
    }

    /**
     * Reset tracking details.
     */
    fun resetTracking() {
        this.averageSpeed.value = 0.0
        this.locations.clear()
    }

    /**
     * Find recent acceleration rate.
     *
     * @return Acceleration in m/s^2
     */
    private fun findAcceleration(): Double {
        // Use a = (v - u)/t historically
        val (v, u) = if (locations.size < ACCEL_REVERSE_INDEX) {
            Pair(locations.last(), locations.first())
        } else {
            Pair(locations.last(), locations[locations.lastIndex - ACCEL_REVERSE_INDEX])
        }
        val time = v.time - u.time
        return ((v.speed - u.speed) / time).toDouble()
    }

    /**
     * Predict what the speed will be in x seconds.
     * @param time Time in seconds to predict ahead.
     * @return Predicted speed in m/s.
     */
    private fun predictSpeed(): Double {
        // Use v = u + at prediction.
        // https://www.calculatorsoup.com/calculators/physics/velocity_a_t.php
        val accel = findAcceleration()
        return this.averageSpeed.value?.plus((accel * this.predictTime)) ?: 0.0
    }

    /**
     * Function to run after updating location.
     *
     * Note that this originally also checked [averageSpeed]; consider if this should be checked.
     */
    private fun afterLocationUpdate() {
        this.overSpeedLimit.value = (this.predictSpeed() > this.speedLimit.value!!)
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
        afterLocationUpdate()
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
        afterLocationUpdate()
    }

    companion object {
        /**
         * Convert from mps to current speed unit.
         *
         * Currently only converts to MPH; should add toggle to handle KMH.
         *
         * @param ms Speed, in metres per second, to convert.
         * @return Speed converted to current speed unit (MPH or KMH).
         */
        @JvmStatic
        fun convertToUnit(ms: Double): Double = ms * 2.237
    }
}