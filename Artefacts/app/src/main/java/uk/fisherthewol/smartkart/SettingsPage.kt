package uk.fisherthewol.smartkart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SettingsPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainerView, SettingsFragment())
            .commit()

        setContentView(R.layout.activity_settings_page)
    }
}