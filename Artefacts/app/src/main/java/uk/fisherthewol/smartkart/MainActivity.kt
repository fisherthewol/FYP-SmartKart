package uk.fisherthewol.smartkart

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    var model: AverageSpeedModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.dashboardFragContainer, DashboardFragment())
            .commit()
        setContentView(R.layout.activity_main)
        val ab: Toolbar = findViewById(R.id.ActionBar)
        setSupportActionBar(ab)

        // Attempt to get location permission:
        if (checkForLocationPermissions()) {
            if (model == null) {
                model = AverageSpeedModel(this.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
            }
        } else {
            requestLocationPermissions(this)
        }
    }

    private fun checkForLocationPermissions() : Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    // Using https://github.com/googlearchive/android-RuntimePermissions/blob/96612da3a0b4489fdf818a624a7e26fc768c65c9/kotlinApp/app/src/main/java/com/example/android/system/runtimepermissions/MainActivity.kt#L207 as a reference for this section.
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
            if (grantResults.filter {it == PackageManager.PERMISSION_GRANTED}.size == LOCATION_PERM_ARR.size && model == null) {
                model = AverageSpeedModel(this.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
            }
        } else {
            // Other requests.
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /*fun settingsClick(view: View) {
        startActivity(Intent(this, SettingsPage::class.java))
    }*/

    /*
    //TODO: Get a setting button on top bar. Consider drawer??
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.SettingsButton -> {
            Toast.makeText(applicationContext, "Pressed Button", Toast.LENGTH_SHORT).show()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
    */
    companion object {
        const val LOCATION_REQ_CODE: Int = 1
        val LOCATION_PERM_ARR: Array<String> = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
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