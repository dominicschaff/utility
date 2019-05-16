package zz.utility.browser

import android.net.Uri
import android.os.Bundle
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_video_player.*
import zz.utility.R
import zz.utility.helpers.PipActivity
import java.io.File

class VideoPlayerActivity : PipActivity() {

    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        val u = Uri.fromFile(File(intent.extras?.getString(PATH) ?: return))

        player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector()).also { player_view.player = it }

        player.playWhenReady = true
        player.prepare(ExtractorMediaSource.Factory(DefaultDataSourceFactory(this, "videoapp")).createMediaSource(u))
        player.addListener(object : Player.EventListener {
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}
            override fun onSeekProcessed() {}
            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {}
            override fun onPlayerError(error: ExoPlaybackException?) {}
            override fun onLoadingChanged(isLoading: Boolean) {}
            override fun onPositionDiscontinuity(reason: Int) {}
            override fun onRepeatModeChanged(repeatMode: Int) {}
            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {}
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) finish()
            }
        })
    }

    override fun onStop() {
        super.onStop()
        player.stop(true)
        player.release()
    }
}
