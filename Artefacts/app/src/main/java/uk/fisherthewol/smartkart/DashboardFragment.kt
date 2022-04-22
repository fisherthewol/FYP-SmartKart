package uk.fisherthewol.smartkart

import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import uk.fisherthewol.smartkart.databinding.FragmentDashboardBinding

/**
 * Fragment subclass representing the current average speed.
 */
class DashboardFragment(private val trackingBool: LiveData<Boolean>) : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var model: AverageSpeedModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        this.model = AverageSpeedModel(this.requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager)
        // Observe average speed.
        model.getAverageSpeed().observe(viewLifecycleOwner) { value ->
            binding.averageSpeedDigits.text = value.toString()
        }
        // Observe when we're tracking:
        trackingBool.observe(viewLifecycleOwner) {
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