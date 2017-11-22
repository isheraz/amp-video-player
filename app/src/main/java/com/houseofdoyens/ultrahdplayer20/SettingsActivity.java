package com.houseofdoyens.ultrahdplayer20;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

public class SettingsActivity extends AppCompatActivity {


    private IconDrawable sun, vol, skin, mics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        vol = new IconDrawable(this, FontAwesomeIcons.fa_volume_up).colorRes(R.color.colorAccent).sizeDp(25);
        sun = new IconDrawable(this, FontAwesomeIcons.fa_sun_o).colorRes(R.color.colorAccent).sizeDp(25);
        SeekBar brightBar = (SeekBar) findViewById(R.id.BrightnessBar);
        SeekBar volBar = (SeekBar) findViewById(R.id.volumeBar);

        brightBar.setThumb(sun);
        volBar.setThumb(vol);
    }
}
