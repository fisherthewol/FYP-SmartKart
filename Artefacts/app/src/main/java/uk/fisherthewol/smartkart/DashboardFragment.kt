package uk.fisherthewol.smartkart

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.location.LocationManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.google.android.material.color.MaterialColors
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
    // Nullable MediaPlayer, since we want to initialise asynchronously.
    private var mediaPlayer: MediaPlayer? = null
    private var mpHandler: MediaPlayerHandler = MediaPlayerHandler()

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
                    mpHandler.startOverLimit()
                }
                false -> {
                    // Adapted from https://stackoverflow.com/a/64509627, Chandra Sekhar, CC BY-SA 4.0
                    val color = MaterialColors.getColor(this.requireContext(), com.google.android.material.R.attr.colorOnSecondary, Color.BLACK)
                    binding.averageSpeedDigits.setTextColor(color)
                    mpHandler.stopOverLimit()
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
        val mp = MediaPlayer().apply {
            setDataSource(
                requireContext(),
                uri)
            setOnPreparedListener(mpHandler)
            setOnErrorListener(mpHandler)
            setOnSeekCompleteListener(mpHandler)
        }
        mp.prepareAsync()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // Deal with mediaPlayer. Adapted from https://stackoverflow.com/a/26316828, Li3ro, CC BY-SA 3.0
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
        }
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    /**
     * Inner class to handle flings on SpeedLimit item.
     */
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

    /**
     * Inner class to handle asynchronous preparation and errors for MediaPlayer.
     */
    private inner class MediaPlayerHandler:
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener {
        private var isPrepared = false

        /**
         * Callback for [MediaPlayer.prepareAsync].
         *
         * Set member [mediaPlayer] to the returned [MediaPlayer] and set [isLooping][MediaPlayer.isLooping] to true.
         *
         * @param mp [MediaPlayer] that is either in Prepared state or is null.
         */
        override fun onPrepared(mp: MediaPlayer?) {
            if (mp != null) {
                mediaPlayer = mp
                mediaPlayer?.isLooping = true
                isPrepared = true
            } else {
                Log.e("DashboardFragment:MediaPlayerListener:onPrepared", "Returned mediaPlayer was null.")
                isPrepared = false
            }
        }

        /**
         * Callback for [MediaPlayer] Asynchronous errors.
         */
        override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
            val what: String = when (what) {
                MediaPlayer.MEDIA_ERROR_UNKNOWN -> "MEDIA_ERROR_UNKNOWN"
                MediaPlayer.MEDIA_ERROR_SERVER_DIED -> "MEDIA_ERROR_SERVER_DIED"
                else -> "Unknown What, value $what"
            }
            val extra: String = when (extra) {
                MediaPlayer.MEDIA_ERROR_IO -> "MEDIA_ERROR_IO"
                MediaPlayer.MEDIA_ERROR_MALFORMED -> "MEDIA_ERROR_MALFORMED"
                MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> "MEDIA_ERROR_UNSUPPORTED"
                MediaPlayer.MEDIA_ERROR_TIMED_OUT -> "MEDIA_ERROR_TIMED_OUT"
                else -> "Unknown Extra, value $extra"

            }
            Log.e("DashboardFragment:MediaPlayerListener:onError", "Error from mediaPlayer.\n" +
                    "What: $what\nExtra: $extra")
            mediaPlayer?.reset()
            prepareMediaPlayer()
            return true
        }

        /**
         * Handle seeking completing.
         *
         * We seek when we "stop" the limit sound. Then, when this seek finishes, we can start again.
         */
        override fun onSeekComplete(p0: MediaPlayer?) {
            isPrepared = true
        }

        /**
         * Handle starting to play the overLimit sound.
         */
        fun startOverLimit() {
            if (isPrepared && mediaPlayer?.isPlaying == false) {
                mediaPlayer?.start()
                isPrepared = false
            }
        }

        /**
         * Pause and reseek the sound to the start.
         */
        fun stopOverLimit() {
            if (!isPrepared && mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                mediaPlayer?.seekTo(0)
                isPrepared = false
            }
        }
    }
}