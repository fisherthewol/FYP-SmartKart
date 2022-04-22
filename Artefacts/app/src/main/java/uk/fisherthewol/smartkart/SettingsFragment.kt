package uk.fisherthewol.smartkart

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)
        val predictBar: SeekBarPreference? = findPreference("model_predict_time")
        if (predictBar != null) {
            predictBar.seekBarIncrement = 10
            predictBar.setDefaultValue(resources.getInteger(R.integer.predict_default))
            predictBar.max = resources.getInteger(R.integer.predict_max_bound)
            predictBar.min = resources.getInteger(R.integer.predict_min_bound)
        }
    }

}