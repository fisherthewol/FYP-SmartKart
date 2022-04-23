package uk.fisherthewol.smartkart

import android.content.Context
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import uk.fisherthewol.smartkart.databinding.FragmentDashboardBinding
import kotlin.math.roundToInt

/**
 * Fragment subclass representing the current average speed.
 */
class DashboardFragment() : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val model: AverageSpeedModel by activityViewModels {
        AverageSpeedModelFactory(
            this.requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        // Observe average speed.
        model.getAverageSpeed().observe(viewLifecycleOwner) { value ->
            binding.averageSpeedDigits.text = AverageSpeedModel.convertToUnit(value).roundToInt().toString() // Note: rounds upwards on tie.
        }
        // Observe speed limit.
        model.getSpeedLimit().observe(viewLifecycleOwner) { value ->
            binding.speedLimitText.text = value.toString()
        }
        // Observe when we're tracking:
        model.getTrackingBool().observe(viewLifecycleOwner) {
            val prefMan = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
            when (it) {
                true -> model.startTracking(prefMan.getInt("model_predict_time", R.integer.predict_default))
                false -> model.stopTracking()
            }
        }
        // Observe when we're over speedLimit.
        model.getOverSpeedLimit().observe(viewLifecycleOwner) {
            when (it) {
                true -> binding.averageSpeedDigits.setTextColor(Color.RED)
                false -> {
                    // Adapted from by ashughes and jpaugh https://stackoverflow.com/a/14468034; CC BY-SA 4.0
                    val t = TypedValue()
                    context?.theme?.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, t, true)
                    binding.averageSpeedDigits.setTextColor(t.data)
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}