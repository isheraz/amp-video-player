package com.houseofdoyens.ultrahdplayer20;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;

import java.util.ArrayList;
import java.util.List;

public class EquilizerActivity extends AppCompatActivity {

    private static final float VISUALIZER_HEIGHT_DIP = 50f;
    private static final int PERMISSION = 1234;

    private MediaPlayer mMediaPlayer;
    private Visualizer mVisualizer;
    private Equalizer mEqualizer;

    private LinearLayout mLinearLayout;
    private VisualizerView mVisualizerView;

    AppPreferences properties;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equilizer);
//        set the device's volume control to control the audio stream we'll be playing
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Create the MediaPlayer
//        you need to put your audio file in the res/raw folder
//        - the filename must be test_audio_file or
//        change it below to match your filename
        mMediaPlayer = MediaPlayer.create(this, R.raw.test_audio_file);
        mMediaPlayer.start();

//        create the equalizer with default priority of 0 & attach to our media player
        mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);

//        set up visualizer and equalizer bars after getting required permissions

        if (!hasAudioSettingsPermission(this)) {
            requestAudioSettingsPermission(this);
        }
        if (hasAudioSettingsPermission(this)) {
            setupVisualizerFxAndUI();
        }
        setupEqualizerFxAndUI();
        mVisualizer.setEnabled(true);

        // listen for when the music stream ends playing
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
//                disable the visualizer as it's no longer needed
                mVisualizer.setEnabled(false);
            }
        });

        properties = AppPreferences.getInstance();
        properties.preferences = getSharedPreferences(properties.app, Context.MODE_PRIVATE);
        properties.edit_preferences = properties.preferences.edit();

        String statusBarColor = properties.preferences.getString("statusBar_color", "#303F9F");
        String realThemeColor = properties.preferences.getString("actionBar_color", "#3F51B5");
        changeTheme(statusBarColor, realThemeColor);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /* shows spinner with list of equalizer presets to choose from
 - updates the seekBar progress and gain levels according
 to those of the selected preset*/
    private void equalizeSound() {
//        set up the spinner
        ArrayList<String> equalizerPresetNames = new ArrayList<String>();
        ArrayAdapter<String> equalizerPresetSpinnerAdapter
                = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                equalizerPresetNames);
        equalizerPresetSpinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner equalizerPresetSpinner = (Spinner) findViewById(R.id.spinner);

//        get list of the device's equalizer presets
        for (short i = 0; i < mEqualizer.getNumberOfPresets(); i++) {
            equalizerPresetNames.add(mEqualizer.getPresetName(i));
        }

        equalizerPresetSpinner.setAdapter(equalizerPresetSpinnerAdapter);

//        handle the spinner item selections
        equalizerPresetSpinner.setOnItemSelectedListener(new AdapterView
                .OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //first list item selected by default and sets the preset accordingly
                mEqualizer.usePreset((short) position);
//                get the number of frequency bands for this equalizer engine
                short numberFrequencyBands = mEqualizer.getNumberOfBands();
//                get the lower gain setting for this equalizer band
                final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];

//                set seekBar indicators according to selected preset
                for (short i = 0; i < numberFrequencyBands; i++) {
                    short equalizerBandIndex = i;
                    SeekBar seekBar = (SeekBar) findViewById(equalizerBandIndex);
//                    get current gain setting for this equalizer band
//                    set the progress indicator of this seekBar to indicate the current gain value
                    seekBar.setProgress(mEqualizer
                            .getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                not used
            }
        });
    }

    /* displays the SeekBar sliders for the supported equalizer frequency bands
     user can move sliders to change the frequency of the bands*/
    private void setupEqualizerFxAndUI() {

//        get reference to linear layout for the seekBars
        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutEqual);

//        equalizer heading
        TextView equalizerHeading = new TextView(this);
        equalizerHeading.setText("Equalizer");
        equalizerHeading.setTextSize(20);
        equalizerHeading.setGravity(Gravity.CENTER_HORIZONTAL);
        mLinearLayout.addView(equalizerHeading);

//        get number frequency bands supported by the equalizer engine
        short numberFrequencyBands = mEqualizer.getNumberOfBands();

//        get the level ranges to be used in setting the band level
//        get lower limit of the range in milliBels
        final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];
//        get the upper limit of the range in millibels
        final short upperEqualizerBandLevel = mEqualizer.getBandLevelRange()[1];

//        loop through all the equalizer bands to display the band headings, lower
//        & upper levels and the seek bars
        for (short i = 0; i < numberFrequencyBands; i++) {
            final short equalizerBandIndex = i;

//            frequency header for each seekBar
            TextView frequencyHeaderTextview = new TextView(this);
            frequencyHeaderTextview.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            frequencyHeaderTextview.setGravity(Gravity.CENTER_HORIZONTAL);
            frequencyHeaderTextview
                    .setText((mEqualizer.getCenterFreq(equalizerBandIndex) / 1000) + " Hz");
            mLinearLayout.addView(frequencyHeaderTextview);

//            set up linear layout to contain each seekBar
            LinearLayout seekBarRowLayout = new LinearLayout(this);
            seekBarRowLayout.setOrientation(LinearLayout.HORIZONTAL);

//            set up lower level textview for this seekBar
            TextView lowerEqualizerBandLevelTextview = new TextView(this);
            lowerEqualizerBandLevelTextview.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            lowerEqualizerBandLevelTextview.setText((lowerEqualizerBandLevel / 100) + " dB");
//            set up upper level textview for this seekBar
            TextView upperEqualizerBandLevelTextview = new TextView(this);
            upperEqualizerBandLevelTextview.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            upperEqualizerBandLevelTextview.setText((upperEqualizerBandLevel / 100) + " dB");

            //            **********  the seekBar  **************
//            set the layout parameters for the seekbar
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;

//            create a new seekBar
            SeekBar seekBar = new SeekBar(this);
//            give the seekBar an ID
            seekBar.setId(i);

            seekBar.setLayoutParams(layoutParams);
            seekBar.setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);
//            set the progress for this seekBar
            seekBar.setProgress(mEqualizer.getBandLevel(equalizerBandIndex));

//            change progress as its changed by moving the sliders
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    mEqualizer.setBandLevel(equalizerBandIndex,
                            (short) (progress + lowerEqualizerBandLevel));
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                    //not used
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    //not used
                }
            });

//            add the lower and upper band level textviews and the seekBar to the row layout
            seekBarRowLayout.addView(lowerEqualizerBandLevelTextview);
            seekBarRowLayout.addView(seekBar);
            seekBarRowLayout.addView(upperEqualizerBandLevelTextview);

            mLinearLayout.addView(seekBarRowLayout);

            //        show the spinner
            equalizeSound();
        }
    }

    /*displays the audio waveform*/
    private void setupVisualizerFxAndUI() {
        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutVisual);
        // Create a VisualizerView to display the audio waveform for the current settings
        mVisualizerView = new VisualizerView(this);
        mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (VISUALIZER_HEIGHT_DIP * getResources().getDisplayMetrics().density)));
        mLinearLayout.addView(mVisualizerView);

        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                mVisualizerView.updateVisualizer(bytes);
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isFinishing() && mMediaPlayer != null) {
            mVisualizer.release();
            mEqualizer.release();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public static void requestAudioSettingsPermission(Activity activity) {

        String requiredPermission = Manifest.permission.RECORD_AUDIO;
        ActivityCompat.requestPermissions(activity, new String[]{requiredPermission}, PERMISSION);

    }

    public static boolean hasAudioSettingsPermission(Context context) {

        boolean hasPermission = (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
        return hasPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION:
                //If Audio Permission is true
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "permission denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void changeTheme(String statusBarColor, String realThemeColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(statusBarColor));
        }
        ActionBar actionBar = getSupportActionBar();
        ColorDrawable themeColor = new ColorDrawable(Color.parseColor(realThemeColor));
        actionBar.setBackgroundDrawable(themeColor);
        mVisualizerView.setVisualizerColor(themeColor);

    }
}
