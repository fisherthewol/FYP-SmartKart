package uk.fisherthewol.smartkart

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)
        // Limit input on predict time.
        val modelPredict: SeekBarPreference? = findPreference("model_predict_time")
        if (modelPredict != null) {
            modelPredict.max = resources.getInteger(R.integer.predict_max_bound)
            modelPredict.min = resources.getInteger(R.integer.predict_min_bound)
        }
    }

}