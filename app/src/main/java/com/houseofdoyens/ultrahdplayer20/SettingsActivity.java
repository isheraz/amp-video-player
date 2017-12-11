package com.houseofdoyens.ultrahdplayer20;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

public class SettingsActivity extends AppCompatActivity
        implements View.OnClickListener {

    /* class variables and attributes */
    private IconDrawable sun, vol, skin, mics;
    private ImageView blue_default, red, pink, blue, green, yellow;
    private String themeColor = null;
    private String statusBarColor = null;
    private Button rate_us, share;
    private SeekBar brightBar, volumeBar;

    AppPreferences properties;

    private InterstitialAd fullAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        /*Full page Ads*/
        AdManager.createAd(this);
        fullAd = AdManager.getAd();
        if (fullAd.isLoaded()) {
            fullAd.show();
        } else {
            AdManager.createAd(this);
        }

        /* Setting up icons and seek bar */
        vol = new IconDrawable(this, FontAwesomeIcons.fa_music).colorRes(R.color.colorAccent).sizeDp(25);
        sun = new IconDrawable(this, FontAwesomeIcons.fa_sun_o).colorRes(R.color.colorAccent).sizeDp(25);
        brightBar = (SeekBar) findViewById(R.id.BrightnessBar);
        volumeBar = (SeekBar) findViewById(R.id.volumeBar);

//        rate_us = (Button) findViewById(R.id.rate_us);
//        share = (Button) findViewById(R.id.share_app);
        brightBar.setThumb(sun);
        volumeBar.setThumb(vol);
        /* Brightness and Volume */
        setBrightness();
        setVolumeBar();
        /*Setting up themes and adding on click listener*/
        blue_default = (ImageView) findViewById(R.id.blue_default);
        red = (ImageView) findViewById(R.id.red);
        pink = (ImageView) findViewById(R.id.pink);
        blue = (ImageView) findViewById(R.id.blue);
        green = (ImageView) findViewById(R.id.green);
        yellow = (ImageView) findViewById(R.id.yellow);

        blue_default.setOnClickListener(this);
        red.setOnClickListener(this);
        pink.setOnClickListener(this);
        blue.setOnClickListener(this);
        green.setOnClickListener(this);
        yellow.setOnClickListener(this);
        properties = AppPreferences.getInstance();

        /* App Skin setup */
        String statusBarColor = properties.preferences.getString("statusBar_color", "#303F9F");
        String realThemeColor = properties.preferences.getString("actionBar_color", "#3F51B5");
        changeTheme(statusBarColor, realThemeColor);
        requestReadWriteSystemPermission(this);
    }

    private void requestReadWriteSystemPermission(Activity activity) {
        String requiredPermission = Manifest.permission.WRITE_SETTINGS;
        if (!(ContextCompat.checkSelfPermission(this, requiredPermission) == PackageManager.PERMISSION_GRANTED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(this)) {

                } else {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + this.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }
//        ActivityCompat.requestPermissions(activity, new String[]{requiredPermission}, PERMI);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /* App Skin setup */
        String statusBarColor = properties.preferences.getString("statusBar_color", "#303F9F");
        String realThemeColor = properties.preferences.getString("actionBar_color", "#3F51B5");
        changeTheme(statusBarColor, realThemeColor);

        if (fullAd.isLoaded()) {
            fullAd.show();
        } else {
            AdManager.createAd(this);
        }
    }

    private void setBrightness() {
        brightBar.setMax(255);
        float curBrightnessValue = 0;
        try {
            curBrightnessValue = android.provider.Settings.System.getInt(
                    getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        int screen_brightness = (int) curBrightnessValue;
        brightBar.setProgress(screen_brightness);

        brightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue,
                                          boolean fromUser) {
                progress = progresValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                android.provider.Settings.System.putInt(getContentResolver(),
//                        android.provider.Settings.System.SCREEN_BRIGHTNESS,
//                        progress);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                Log.i("Brightness", progress+" ");
                android.provider.Settings.System.putInt(getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS,
                        progress);

                ContentResolver cResolver = getContentResolver();
                Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, progress);
            }
        });
    }


    /*Volume Settings*/
    private void setVolumeBar() {

        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int a = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int c = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
        volumeBar.setMax(a);
        volumeBar.setProgress(c);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, arg1, 0);
            }
        });
    }

    public void changeTheme(String statusBarColor, String realThemeColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(statusBarColor));
        }
        ColorDrawable themeColor = new ColorDrawable(Color.parseColor(realThemeColor));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(themeColor);
//        share.setBackground(themeColor);
//        rate_us.setBackground(themeColor);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.blue_default:
                blue_default.setBackgroundColor(Color.parseColor("#BDC0BA"));
                red.setBackgroundColor(Color.TRANSPARENT);
                pink.setBackgroundColor(Color.TRANSPARENT);
                blue.setBackgroundColor(Color.TRANSPARENT);
                green.setBackgroundColor(Color.TRANSPARENT);
                yellow.setBackgroundColor(Color.TRANSPARENT);
                themeColor = "#3F51B5";
                statusBarColor = "#303F9F";
                properties.setAppTheme(themeColor, statusBarColor);
                changeTheme(statusBarColor, themeColor);
                break;

            case R.id.red:
                red.setBackgroundColor(Color.parseColor("#BDC0BA"));
                blue_default.setBackgroundColor(Color.TRANSPARENT);
                pink.setBackgroundColor(Color.TRANSPARENT);
                blue.setBackgroundColor(Color.TRANSPARENT);
                green.setBackgroundColor(Color.TRANSPARENT);
                yellow.setBackgroundColor(Color.TRANSPARENT);
                themeColor = "#D32F2F";
                statusBarColor = "#B71C1C";
                properties.setAppTheme(themeColor, statusBarColor);
                changeTheme(statusBarColor, themeColor);
                break;

            case R.id.pink:
                pink.setBackgroundColor(Color.parseColor("#BDC0BA"));
                blue_default.setBackgroundColor(Color.TRANSPARENT);
                red.setBackgroundColor(Color.TRANSPARENT);
                blue.setBackgroundColor(Color.TRANSPARENT);
                green.setBackgroundColor(Color.TRANSPARENT);
                yellow.setBackgroundColor(Color.TRANSPARENT);
                themeColor = "#C2185B";
                statusBarColor = "#880E4F";
                properties.setAppTheme(themeColor, statusBarColor);
                changeTheme(statusBarColor, themeColor);
                break;

            case R.id.blue:
                blue.setBackgroundColor(Color.parseColor("#BDC0BA"));
                blue_default.setBackgroundColor(Color.TRANSPARENT);
                red.setBackgroundColor(Color.TRANSPARENT);
                pink.setBackgroundColor(Color.TRANSPARENT);
                green.setBackgroundColor(Color.TRANSPARENT);
                yellow.setBackgroundColor(Color.TRANSPARENT);
                themeColor = "#303F9F";
                statusBarColor = "#1A237E";
                properties.setAppTheme(themeColor, statusBarColor);
                changeTheme(statusBarColor, themeColor);
                break;

            case R.id.green:
                green.setBackgroundColor(Color.parseColor("#BDC0BA"));
                blue_default.setBackgroundColor(Color.TRANSPARENT);
                red.setBackgroundColor(Color.TRANSPARENT);
                blue.setBackgroundColor(Color.TRANSPARENT);
                pink.setBackgroundColor(Color.TRANSPARENT);
                yellow.setBackgroundColor(Color.TRANSPARENT);
                themeColor = "#00796B";
                statusBarColor = "#004D40";
                properties.setAppTheme(themeColor, statusBarColor);
                changeTheme(statusBarColor, themeColor);
                break;

            case R.id.yellow:
                yellow.setBackgroundColor(Color.parseColor("#BDC0BA"));
                blue_default.setBackgroundColor(Color.TRANSPARENT);
                red.setBackgroundColor(Color.TRANSPARENT);
                blue.setBackgroundColor(Color.TRANSPARENT);
                green.setBackgroundColor(Color.TRANSPARENT);
                pink.setBackgroundColor(Color.TRANSPARENT);
                themeColor = "#FFA000";
                statusBarColor = "#FF8F00";
                properties.setAppTheme(themeColor, statusBarColor);
                changeTheme(statusBarColor, themeColor);
                break;
        }
    }
}
