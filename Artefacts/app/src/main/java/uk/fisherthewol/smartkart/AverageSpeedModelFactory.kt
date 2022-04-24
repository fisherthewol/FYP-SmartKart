package uk.fisherthewol.smartkart

import android.location.LocationManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AverageSpeedModelFactory(private val locMan: LocationManager, private val speedLimit: MutableLiveData<Int> = MutableLiveData(30)): ViewModelProvider.Factory {
    // https://developer.android.com/codelabs/kotlin-android-training-view-model#7, https://medium.com/koderlabs/viewmodel-with-viewmodelprovider-factory-the-creator-of-viewmodel-8fabfec1aa4f
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AverageSpeedModel::class.java)) {
            return AverageSpeedModel(this.locMan, this.speedLimit) as T
        }
        throw IllegalArgumentException("Unknown ViewModelClass.")
    }
}