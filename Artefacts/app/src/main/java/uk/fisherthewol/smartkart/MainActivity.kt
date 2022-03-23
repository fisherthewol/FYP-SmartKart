package uk.fisherthewol.smartkart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ab: Toolbar = findViewById(R.id.ActionBar)
        setSupportActionBar(ab)
    }

    fun settingsClick(view: View) {
        startActivity(Intent(this, SettingsPage::class.java))
    }


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