package uk.fisherthewol.smartkart

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager

class AverageSpeedModel(locationMan: LocationManager): LocationListener {
    private val locations: MutableList<Location> = emptyList<Location>().toMutableList()
    init {
        // TODO: Implement this one level above.
        /*if (ActivityCompat.checkSelfPermission(
                conForPermissionCheck,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                conForPermissionCheck,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }*/
        locationMan.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0, // Minimum time between updates.
            5.0f, // Minimum distance between updateds.
            this)
    }

    /**
     * Calculate average speed of device.
     *
     * Depends on [Location.hasSpeed] == true.
     *
     * @return Average speed in m/s.
     */
    public var averageSpeed: Double = 0.0

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