package uk.fisherthewol.smartkart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ab: Toolbar = findViewById(R.id.ActionBar)
        setSupportActionBar(ab)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.SettingsButton -> {
            // TODO: Intent to settings window.
            Toast.makeText(applicationContext, "Pressed Button", Toast.LENGTH_SHORT).show()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}