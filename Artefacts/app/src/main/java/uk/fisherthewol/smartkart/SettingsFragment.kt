package uk.fisherthewol.smartkart

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)
        // Limit input on predict time.
        val modelPredict: EditTextPreference? = findPreference("model_predict_time")
        modelPredict?.setOnBindEditTextListener { 
                EditText -> EditText.inputType = InputType.TYPE_CLASS_NUMBER }
    }

}