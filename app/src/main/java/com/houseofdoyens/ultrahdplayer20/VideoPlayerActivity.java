package com.houseofdoyens.ultrahdplayer20;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.EntypoIcons;

import java.io.File;
import java.util.ArrayList;

import static com.google.android.exoplayer2.ui.AspectRatioFrameLayout.OnClickListener;
import static com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL;
import static com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT;
import static com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT;
import static com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH;
import static com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM;


public class VideoPlayerActivity extends AppCompatActivity {

    // bandwidth meter to measure and estimate bandwidth
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    private static final String TAG = "PlayerActivity";
    static ArrayList<String> AllVideosName = new ArrayList<>();
    static ArrayList<String> AllVideosPath = new ArrayList<>();

    /*Setting up Icons*/
    private IconDrawable eq, ratio, screenshot, rotation;

    /*Adding Equalizer*/
    private Equalizer mEqualizer;


    private SimpleExoPlayer player;
    private SimpleExoPlayerView playerView;
    private ComponentListener componentListener;
    private ActivityInfo activityInfo;

    private Bundle bundle;
    private AppPreferences properties;

    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;
    private Button rotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        properties = AppPreferences.getInstance();
        properties.preferences = getSharedPreferences(properties.app, Context.MODE_PRIVATE);
        properties.edit_preferences = properties.preferences.edit();

        componentListener = new ComponentListener();
        playerView = (SimpleExoPlayerView) findViewById(R.id.video_view);
        playerView.setResizeMode(RESIZE_MODE_ZOOM);

        screenshot = new IconDrawable(this, EntypoIcons.entypo_copy).colorRes(R.color.colorAccent).sizeDp(25);
        eq = new IconDrawable(this, EntypoIcons.entypo_sound_mix).colorRes(R.color.colorAccent).sizeDp(25);
        ratio = new IconDrawable(this, EntypoIcons.entypo_crop).colorRes(R.color.colorAccent).sizeDp(25);
        rotation = new IconDrawable(this, EntypoIcons.entypo_documents).colorRes(R.color.colorWhite).sizeDp(20);

        rotate = (Button) findViewById(R.id.exo_fullscreen_button);
        rotate.setBackground(rotation);
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getRequestedOrientation() == activityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(activityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                } else {
                    setRequestedOrientation(activityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });

        bundle = getIntent().getExtras();
        AllVideosName = bundle.getStringArrayList("videoNameArray");
        AllVideosPath = bundle.getStringArrayList("videoPathArray");


        screenshotSetup();
        ratioSetup();

    }

    static int clicks;

    private void ratioSetup() {
        Button ratioButton = findViewById(R.id.exo_ratio);
        ratioButton.setBackground(ratio);
        clicks = 0;
        ratioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (clicks) {
                    case 1:
                        playerView.setResizeMode(RESIZE_MODE_FILL);
                        Snackbar.make(v, "FILL", Snackbar.LENGTH_LONG).show();
                        break;
                    case 2:
                        playerView.setResizeMode(RESIZE_MODE_FIT);
                        break;
                    case 3:
                        playerView.setResizeMode(RESIZE_MODE_FIXED_WIDTH);
                        break;
                    case 4:
                        playerView.setResizeMode(RESIZE_MODE_FIXED_HEIGHT);
                        break;
                    case 5:
                        playerView.setResizeMode(RESIZE_MODE_ZOOM);
                        clicks = 0;
                        break;
                }
                clicks++;
            }
        });
    }

    private void screenshotSetup() {
        Button screenshotButton = findViewById(R.id.exo_screenshot);
        screenshotButton.setBackground(screenshot);
        screenshotButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setupEqualizer() {
        Button equalizerButton = findViewById(R.id.exo_equalizer);
        equalizerButton.setBackground(eq);
        equalizerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
                Intent eqPage = new Intent(getApplicationContext(), EquilizerActivity.class);
                getApplicationContext().startActivity(eqPage);
            }
        });
        player.setAudioDebugListener(new AudioRendererEventListener() {

            @Override
            public void onAudioEnabled(DecoderCounters counters) {

            }

            @Override
            public void onAudioSessionId(int audioSessionId) {
                mEqualizer = new Equalizer(1000, audioSessionId);
                mEqualizer.setEnabled(true);
                int current = properties.preferences.getInt("position", 0);
                if (current == 0) {
                    for (short seek_id = 0; seek_id < mEqualizer.getNumberOfBands(); seek_id++) {
                        int progressBar = properties.preferences.getInt("seek_" + seek_id, 1500);
                        short equalizerBandIndex = (short) (seek_id);
                        final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];
                        Log.i("seek_" + seek_id, ":" + progressBar);
                        if (progressBar != 1500) {
                            mEqualizer.setBandLevel(equalizerBandIndex,
                                    (short) (progressBar + lowerEqualizerBandLevel));
                        } else {
                            mEqualizer.setBandLevel(equalizerBandIndex,
                                    (short) (progressBar + lowerEqualizerBandLevel));
                        }
                    }
                } else {
                    mEqualizer.usePreset((short) (current - 1));
                }
            }

            @Override
            public void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

            }

            @Override
            public void onAudioInputFormatChanged(Format format) {

            }

            @Override
            public void onAudioSinkUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {

            }

            @Override
            public void onAudioDisabled(DecoderCounters counters) {

            }
        });
//        Log.i("Session_id",""+player.getAudioSessionId());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
            setupEqualizer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
            properties.edit_preferences.putBoolean("LastPlayed", true).commit();
        }
    }

    private void initializePlayer() {
        if (player == null) {
            // a factory to create an AdaptiveVideoTrackSelection
            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            // using a DefaultTrackSelector with an adaptive video selection factory
            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(adaptiveTrackSelectionFactory),
                    new DefaultLoadControl()
            );
            player.addListener(componentListener);
            player.setVideoDebugListener(componentListener);
            player.setAudioDebugListener(componentListener);
            playerView.setPlayer(player);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);


        }
        bundle = getIntent().getExtras();
        String src = bundle.getString("videoPath");

        /*Making a Playlist of all the videos in playlist*/
        MediaSource[] mediaSources = new MediaSource[AllVideosPath.size()];
        int i;
        for (i = 0; i < AllVideosPath.size(); i++) {
            mediaSources[i] = buildMediaSourceLocal(Uri.fromFile(new File(AllVideosPath.get(i))));
        }
        /* Checking if There are multiple videos in playlist or 1*/
        MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]
                : new ConcatenatingMediaSource(mediaSources);
        /* Playing the currently selected video */
        player.seekTo(AllVideosPath.indexOf(src), 0);
        player.prepare(mediaSource, true, false);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }


    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.removeListener(componentListener);
            player.addVideoListener(null);
            player.setVideoDebugListener(null);
            player.setAudioDebugListener(null);
            player.release();
            player = null;
        }
    }

    /*Used for offline links*/
    private MediaSource buildMediaSourceLocal(Uri uri) {
        DataSource.Factory dataSourceFactory = new FileDataSourceFactory();
        return new ExtractorMediaSource(uri, dataSourceFactory,
                new DefaultExtractorsFactory(), null, null);
    }

    /*Can be used for online links*/
    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory("ua", BANDWIDTH_METER);
        DashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(
                dataSourceFactory);
        return new DashMediaSource(uri, dataSourceFactory, dashChunkSourceFactory, null, null);
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


    private class ComponentListener implements ExoPlayer.EventListener, VideoRendererEventListener,
            AudioRendererEventListener {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            // Do nothing.
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            // Do nothing.
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            // Do nothing.
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            String stateString;
            switch (playbackState) {
                case Player.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE      -";
                    break;
                case Player.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    break;
                case Player.STATE_READY:
                    stateString = "ExoPlayer.STATE_READY     -";
                    break;
                case Player.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    break;
            }
            Log.d(TAG, "changed state to " + stateString + " playWhenReady: " + playWhenReady);
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            // Do nothing.
        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            // Do nothing.
        }

        @Override
        public void onSeekProcessed() {

        }

        @Override
        public void onVideoEnabled(DecoderCounters counters) {

        }

        @Override
        public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

        }

        @Override
        public void onVideoInputFormatChanged(Format format) {

        }

        @Override
        public void onDroppedFrames(int count, long elapsedMs) {

        }

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
//            Log.i(TAG, width + "*" + height + "/" + unappliedRotationDegrees + "/" + pixelWidthHeightRatio);
            activityInfo = new ActivityInfo();
            if (width <= height) {
                setRequestedOrientation(activityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(activityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        @Override
        public void onRenderedFirstFrame(Surface surface) {

        }

        @Override
        public void onVideoDisabled(DecoderCounters counters) {

        }

        @Override
        public void onAudioEnabled(DecoderCounters counters) {

        }

        @Override
        public void onAudioSessionId(int audioSessionId) {

        }

        @Override
        public void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

        }

        @Override
        public void onAudioInputFormatChanged(Format format) {

        }

        @Override
        public void onAudioSinkUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {

        }


        @Override
        public void onAudioDisabled(DecoderCounters counters) {

        }
    }
}
