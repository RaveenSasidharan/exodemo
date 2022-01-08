package com.example.exoplayerdemo

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.exoplayerdemo.databinding.FragmentFirstBinding
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var simpleExoPlayer: ExoPlayer? = null
    private val mediaUrl = "https://tubbr-test-trans-video.s3.amazonaws.com/users/user_id_192/2022/1/06/hls/14db7082-21d0-457c-8b98-58141d6a9f78.m3u8"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializePlayer()


    }

    private fun initializePlayer() {
        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter.Builder(requireContext()).build()
        val videoTrackSelectionFactory =
            AdaptiveTrackSelection.Factory()
        val trackSelector: TrackSelector = DefaultTrackSelector(requireContext() ,videoTrackSelectionFactory)
        val httpDataSource = DefaultHttpDataSource.Factory()
            .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
            .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
            .setUserAgent(Util.getUserAgent(requireContext().applicationContext, getString(R.string.app_name)))
            .setAllowCrossProtocolRedirects(true)
        val dataSourceFactory : DefaultDataSource.Factory = DefaultDataSource.Factory(requireContext().applicationContext,httpDataSource)

        if (simpleExoPlayer == null) {
            simpleExoPlayer =  ExoPlayer.Builder(requireContext())
                .setBandwidthMeter(bandwidthMeter)
                .setTrackSelector(trackSelector)
                .build()

        }

        simpleExoPlayer?.playWhenReady = true


        // Create a HLS media source pointing to a playlist uri.
        // Create a HLS media source pointing to a playlist uri.
        val hlsMediaSource: HlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .setAllowChunklessPreparation(true)
            .createMediaSource(MediaItem.fromUri(mediaUrl))
        simpleExoPlayer?.setMediaSource(hlsMediaSource)
        simpleExoPlayer?.prepare()



        binding.storyDisplayVideo.setShutterBackgroundColor(Color.BLACK)
        binding.storyDisplayVideo.player = simpleExoPlayer

        simpleExoPlayer!!.addListener(object : Player.Listener
        {
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                error.printStackTrace()
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                super.onIsLoadingChanged(isLoading)
            }

        })

    }


    override fun onDestroy() {
        super.onDestroy()
        simpleExoPlayer?.release()
        simpleExoPlayer = null

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}