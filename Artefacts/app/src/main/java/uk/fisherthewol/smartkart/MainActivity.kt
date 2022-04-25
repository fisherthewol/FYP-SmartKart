package uk.fisherthewol.smartkart

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import uk.fisherthewol.smartkart.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val model: AverageSpeedModel by viewModels {
        AverageSpeedModelFactory(
            getSystemService(
                Context.LOCATION_SERVICE
            ) as LocationManager
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.dashboardFragContainer, DashboardFragment())
            .commit()
        setContentView(binding.root)
        setSupportActionBar(binding.ActionBar)

        // Attempt to get location permission:
        if (checkForLocationPermissions()) {
            binding.StartButton.isEnabled = true
        } else {
            binding.StartButton.isEnabled = false
            requestLocationPermissions(this)
        }
    }

    /**
     * Handle returning to activity from requesting permissions.
     *
     * https://github.com/googlearchive/android-RuntimePermissions/blob/96612da3a0b4489fdf818a624a7e26fc768c65c9/kotlinApp/app/src/main/java/com/example/android/system/runtimepermissions/MainActivity.kt#L207 is used as reference.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (permissions.isEmpty()) {
            // Cancellation of request. Request again.
            requestLocationPermissions(this)
            return
        }
        if (requestCode == LOCATION_REQ_CODE) {
            // We're dealing with a request to show permissions. From https://github.com/googlearchive/android-RuntimePermissions/blob/96612da3a0b4489fdf818a624a7e26fc768c65c9/kotlinApp/app/src/main/java/com/example/android/system/runtimepermissions/extensions/CollectionsExts.kt
            // All granted permissions.
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Enable button.
                binding.StartButton.isEnabled = true
            } else {
                // Show snackbar.
                Snackbar.make(
                    binding.mainActivityLayout,
                    getString(R.string.noLocationPermsSB),
                    Snackbar.LENGTH_LONG)
                    .show()
                // Disable button.
                binding.StartButton.isEnabled = false
            }
        } else {
            // Other requests.
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /**
     * Check whether activity has location permissions.
     */
    private fun checkForLocationPermissions() : Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    /**
     * Toggle tracking based on current state.
     */
    fun toggleTracking(view: View) {
        // Button pressed, toggle state of tracking boolean.
        if (model.trackingBool.value == null) {
            // If null: log, set to false, set button string appropriate. Return early to stop.
            Log.w("MainActivity", "TrackingBool has become null. Setting to false, to " +
                    "be in a known state.")
            model.trackingBool.value = false
            binding.StartButton.text = getString(R.string.button_start)
            return
        }
        model.trackingBool.value = ! model.trackingBool.value!!
        // Update text.
        when (model.trackingBool.value!!) {
            true -> {
                // Tracking.
                binding.StartButton.text = getString(R.string.button_stop)
            }
            false -> {
                // Not tracking.
                binding.StartButton.text = getString(R.string.button_start)
            }
        }
    }

    // TODO: Get a setting button on top bar. Consider drawer??
    fun settingsClick(view: View) {
        startActivity(Intent(this, SettingsPage::class.java))
    }

    companion object {
        // Request code for getting location permissions.
        const val LOCATION_REQ_CODE: Int = 1
        // Location permissions we are requesting.
        private val LOCATION_PERM_ARR: Array<String> = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        /**
         * Request relevant location permissions.
         */
        fun requestLocationPermissions(act: Activity) {
            // Request to get location permissions.
            ActivityCompat.requestPermissions(
                act,
                LOCATION_PERM_ARR,
                LOCATION_REQ_CODE
            )
        }

    }
}