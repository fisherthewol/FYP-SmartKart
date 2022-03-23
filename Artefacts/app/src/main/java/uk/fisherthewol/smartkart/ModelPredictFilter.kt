package uk.fisherthewol.smartkart

import android.text.InputFilter
import android.text.Spanned

class ModelPredictFilter : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val attemptInt = source.toString().toIntOrNull() ?: return R.integer.predict_min_bound.toString()
        if (attemptInt < R.integer.predict_min_bound) return R.integer.predict_min_bound.toString()
        if (attemptInt > R.integer.predict_max_bound) return R.integer.predict_max_bound.toString()
        return source
    }
}