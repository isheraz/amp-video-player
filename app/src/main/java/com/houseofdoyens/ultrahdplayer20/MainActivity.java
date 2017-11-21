package com.houseofdoyens.ultrahdplayer20;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.EntypoIcons;
import com.joanzapata.iconify.fonts.EntypoModule;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

public class MainActivity extends AppCompatActivity {

    private IconDrawable play, settings, amp, search, hamburger;
    private AdView adView;
    private InterstitialAd fullAd;
    private AdManager ads;
    private AdRequest adRequest;
    private Intent page;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Iconify.with(new FontAwesomeModule())
                .with(new EntypoModule());
        MobileAds.initialize(this, getResources().getString(R.string.app_id));

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        play = new IconDrawable(this, FontAwesomeIcons.fa_play).colorRes(R.color.colorWhite).actionBarSize();
        toolbar.setLogo(play);

        //        Hamburger For Toolbar
        //        hamburger = new IconDrawable(this, FontAwesomeIcons.fa_bars).colorRes(R.color.colorWhite).actionBarSize();
        //        toolbar.setNavigationIcon(hamburger);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.recentPlay);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Click Working for recent play
//                if (fullAd.isLoaded()) {
//                    fullAd.show();
//                }else{
//                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                }
            }
        });

        /* Ads */
        adView = (AdView) findViewById(R.id.bannerAd);
        adRequest = new AdRequest.Builder()
                .addTestDevice((String.valueOf(R.string.test_device)))
                .build();
        adView.loadAd(adRequest);

        ads = AdManager.getInstance();
        ads.createAd(MainActivity.this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu;
        // this adds items to the action bar if it is present.
        settings = new IconDrawable(this, FontAwesomeIcons.fa_cog).colorRes(R.color.colorWhite).actionBarSize();
        amp = new IconDrawable(this, EntypoIcons.entypo_sound_mix).colorRes(R.color.colorWhite).actionBarSize();
        search = new IconDrawable(this, FontAwesomeIcons.fa_search).colorRes(R.color.colorWhite).actionBarSize();

//        getActionBar().setIcon( play );
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_settings).setIcon(settings);
        menu.findItem(R.id.action_amp).setIcon(amp);
        menu.findItem(R.id.action_search).setIcon(search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_amp:
                break;

            case R.id.action_search:
                break;

            case R.id.action_settings:
                page = new Intent(this, SettingsActivity.class);
                this.startActivity(page);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
