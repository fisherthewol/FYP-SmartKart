package uk.fisherthewol.smartkart

import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import uk.fisherthewol.smartkart.databinding.FragmentDashboardBinding

/**
 * Fragment subclass representing the current average speed.
 */
class DashboardFragment() : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val model: AverageSpeedModel by activityViewModels()

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
            binding.averageSpeedDigits.text = value.toString()
        }
        // Observe when we're tracking:
        model.trackingBool.observe(viewLifecycleOwner) {
            when (it) {
                true -> model.startTracking()
                false -> model.stopTracking()
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}