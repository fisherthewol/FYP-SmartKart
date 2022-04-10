package uk.fisherthewol.smartkart

import android.location.Location
import android.location.LocationManager

class AverageSpeedModel(private val LocationManager: LocationManager) {
    val values: Array<Location> = emptyArray()

    fun averageSpeed(): Double = values.filter { it.hasSpeed() }.map { it.speed }.average()
}