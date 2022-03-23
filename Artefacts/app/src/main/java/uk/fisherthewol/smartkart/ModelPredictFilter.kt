package uk.fisherthewol.smartkart

import android.text.InputFilter
import android.text.Spanned

class ModelPredictFilter(private val minBound: Int, private val maxBound: Int) : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        // If length is 0, we're deleting text; accept buffer.
        if (source?.length == 0) {
            return null
        }
        // Calculate value after input
        val inputInt = source.toString().toIntOrNull() ?: return this.minBound.toString()
        // If below lower bound, return lower bound.
        if (inputInt < this.minBound) return this.minBound.toString()
        // If above upper bound, return upper bound.
        if (inputInt > this.maxBound) return this.maxBound.toString()
        // Else, accept original char sequence.
        return null
    }
}