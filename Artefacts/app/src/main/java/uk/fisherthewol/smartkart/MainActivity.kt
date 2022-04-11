package uk.fisherthewol.smartkart

import android.Manifest
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
        checkForLocationPermissions()
    }

    private fun checkForLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request to get permissions.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                55 // TODO: Check whether we need to save this value.
            )
        } else {
            if (this.model == null) {
                this.model =
                    AverageSpeedModel(this.getSystemService(LOCATION_SERVICE) as LocationManager)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        TODO("Check for permissions then add model.")
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
}