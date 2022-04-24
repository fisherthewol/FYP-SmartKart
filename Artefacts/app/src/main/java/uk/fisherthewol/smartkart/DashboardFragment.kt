package uk.fisherthewol.smartkart

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.location.LocationManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.view.GestureDetectorCompat
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
    // NOTE: This is a delegate. Until AverageSpeedModel gets "cleared", this will return the same viewmodel (singleton style).
    private val model: AverageSpeedModel by activityViewModels {
        AverageSpeedModelFactory(
            this.requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager)
    }
    // Gesture detection to allow swiping on speed limit. https://developer.android.com/training/gestures/detector#detect-a-subset-of-supported-gestures
    private lateinit var mDetector: GestureDetectorCompat
    // Shared Preferences reference; lateinit because we need context.
    private lateinit var prefMan: SharedPreferences
    // Raw initialise MediaPlayer, since we want to initialise asynchronously.
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Get viewBinding.
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        // Bind lateinits:
        mDetector = GestureDetectorCompat(this.requireContext(), SpeedLimitGestureListener())
        prefMan = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        prepareMediaPlayer()

        // Listen for touches on speedLimit.
        binding.speedLimitText.setOnTouchListener{
            v, event -> mDetector.onTouchEvent(event)
            true
        }
        // Observe changes in average speed.
        model.getAverageSpeed().observe(viewLifecycleOwner) { value ->
            binding.averageSpeedDigits.text = AverageSpeedModel.msToUnit(value).roundToInt().toString().padStart(2, '0') // Note: rounds upwards on tie.
        }
        // Observe changes in speed limit.
        model.getSpeedLimit().observe(viewLifecycleOwner) { value ->
            binding.speedLimitText.text = value.toString().padStart(2, '0')
        }
        // Observe when we're tracking:
        model.getTrackingBool().observe(viewLifecycleOwner) {
            when (it) {
                true -> model.startTracking(prefMan.getInt("model_predict_time", R.integer.predict_default))
                false -> model.stopTracking()
            }
        }
        // Observe when we're over speedLimit.
        model.getOverSpeedLimit().observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    binding.averageSpeedDigits.setTextColor(Color.RED)
                    mediaPlayer?.seekTo(0)
                    mediaPlayer?.start()
                }
                false -> {
                    binding.averageSpeedDigits.setTextColor(Color.WHITE) // TODO: Get actual colour from theme.
                    mediaPlayer?.pause()
                }
            }
        }
        return binding.root
    }

    /**
     * Initialise mediaPlayer asynchronously.
     * Adapted from https://developer.android.com/guide/topics/media/mediaplayer#mediaplayer and https://stackoverflow.com/a/33266646, Naren Neelamegam, CC BY-SA 3.0
     */
    private fun prepareMediaPlayer() {
        // Adapted from https://stackoverflow.com/a/38340580, Uli, CC BY-SA 4.0
        val uri: Uri = Uri.Builder().apply {
            scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            authority(resources.getResourcePackageName(R.raw.over_limit))
            appendPath(resources.getResourceTypeName(R.raw.over_limit))
            appendPath(resources.getResourceEntryName(R.raw.over_limit))
        }.build()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(
                requireContext(),
                uri)
            prepareAsync()
            isLooping = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // Release
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private inner class SpeedLimitGestureListener: GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        /**
         * Handle up/down movement to change speed limit.
         *
         * See https://stackoverflow.com/questions/28098737/difference-between-onscroll-and-onfling-of-gesturedetector
         */
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            when {
                velocityY >= 0 -> {
                    // Positive Y.
                    model.speedLimit.value = model.speedLimit.value?.plus(SPEED_LIMIT_INCREMENT)
                    val maxSpeed = prefMan.getInt("max_speed_limit_mph", 70)
                    if (model.speedLimit.value!! > maxSpeed) model.speedLimit.value = maxSpeed
                }
                velocityY < 0 -> {
                    // Negative Y.
                    model.speedLimit.value = model.speedLimit.value?.minus(SPEED_LIMIT_INCREMENT)
                    if (model.speedLimit.value!! < 0) model.speedLimit.value = 0
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }
}