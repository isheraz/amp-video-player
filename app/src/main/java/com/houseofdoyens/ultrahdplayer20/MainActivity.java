package com.houseofdoyens.ultrahdplayer20;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    /*Private class variables*/

    private final static int PERMISSIONS_READ_EXTERNAL_STORAGE = 1234;

    private IconDrawable play, settings, amp, search, hamburger;
    private AdView adView;
    private InterstitialAd fullAd;
    //    private static AdManager ads;
    private AdRequest adRequest;
    private Intent page;
    private View mLayout;

    private ArrayList<VideoViewInfo> AllVideosData = new ArrayList<>();
    private ArrayList<String> videosFolderName = new ArrayList<>();
    private GridView gridView;
    private GridAdapterFolders adapter;
    private FloatingActionButton fab;

    AppPreferences properties;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Initializing Components*/
        Iconify.with(new FontAwesomeModule())
                .with(new EntypoModule());
        MobileAds.initialize(this, getResources().getString(R.string.app_id));
        mLayout = getCurrentFocus();

        /*Setting up Layout*/
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*Play Icon for Toolbar*/
        play = new IconDrawable(this, FontAwesomeIcons.fa_play).colorRes(R.color.colorWhite).actionBarSize();
        toolbar.setLogo(play);

        /*Hamburger For Toolbar*/
        //hamburger = new IconDrawable(this, FontAwesomeIcons.fa_bars).colorRes(R.color.colorWhite).actionBarSize();
        //toolbar.setNavigationIcon(hamburger);

        /*Recent Play Floating Button*/
        fab = (FloatingActionButton) findViewById(R.id.recentPlay);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Click Working for recent play
            }
        });


        properties = AppPreferences.getInstance();
        properties.preferences = getSharedPreferences(properties.app, Context.MODE_PRIVATE);
        properties.edit_preferences = properties.preferences.edit();

        /* App Skin setup */
        String statusBarColor = properties.preferences.getString("statusBar_color", "#303F9F");
        String realThemeColor = properties.preferences.getString("actionBar_color", "#3F51B5");
        changeTheme(statusBarColor, realThemeColor);

        /* Ads Bannner */
        adView = (AdView) findViewById(R.id.bannerAd);
        adRequest = new AdRequest.Builder()
                .addTestDevice(getResources().getString(R.string.test_device))
                .build();
        adView.loadAd(adRequest);

        /*Load Ad Interstitial for next page*/
//        ads = AdManager.getInstance();
        AdManager.createAd(this);
        gridView = (GridView) findViewById(R.id.gridView);

        /*Populate Folders after getting permissions*/
        if (!hasReadExternalStoragePermission(this)) {
            requestReadExternalStoragePermission(this);
        } else {
            populateFolders();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /* App Skin setup */
        String statusBarColor = properties.preferences.getString("statusBar_color", "#303F9F");
        String realThemeColor = properties.preferences.getString("actionBar_color", "#3F51B5");
        changeTheme(statusBarColor, realThemeColor);

//        AdManager.createAd(this);
        fullAd = AdManager.getAd();
        if (fullAd.isLoaded()) {
            fullAd.show();
        } else {
            AdManager.createAd(this);
        }
        adView.loadAd(adRequest);
    }

    public void changeTheme(String statusBarColor, String realThemeColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(statusBarColor));
        }
        ActionBar actionBar = getSupportActionBar();
        ColorDrawable themeColor = new ColorDrawable(Color.parseColor(realThemeColor));
        actionBar.setBackgroundDrawable(themeColor);
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled}, // enabled
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_pressed}  // pressed
        };

        int[] colors = new int[]{
                Color.parseColor(realThemeColor),
                Color.RED,
                Color.GREEN,
                Color.parseColor(statusBarColor)
        };
        ColorStateList tint = new ColorStateList(states, colors);
        fab.setBackgroundTintList(tint);
        fab.setBackgroundDrawable(themeColor);
    }

    private void populateFolders() {
        GetVideos gv = new GetVideos();
        gv.getAllVideosData(this, AllVideosData);

        for (int i = 0; i < AllVideosData.size(); i++) {

            int j;
            for (j = 0; j < videosFolderName.size(); j++) {
                if (videosFolderName.get(j).equals(AllVideosData.get(i).getBucketName())) {
                    break;
                }
            }

            if (j == videosFolderName.size()) {
                videosFolderName.add(AllVideosData.get(i).getBucketName());
            }

        }

        String[] bucketName = new String[videosFolderName.size()];
        videosFolderName.toArray(bucketName);
        adapter = new GridAdapterFolders(this, bucketName);
        gridView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu;
        // this adds items to the action bar if it is present.
        /*Icon initialization*/
        settings = new IconDrawable(this, FontAwesomeIcons.fa_cog).colorRes(R.color.colorWhite).actionBarSize();
        amp = new IconDrawable(this, EntypoIcons.entypo_sound_mix).colorRes(R.color.colorWhite).actionBarSize();
        search = new IconDrawable(this, FontAwesomeIcons.fa_search).colorRes(R.color.colorWhite).actionBarSize();

        /*Inflating Menu Items*/
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

        /*What Meny item is selected*/
        switch (id) {
            case R.id.action_amp:
                page = new Intent(this, EquilizerActivity.class);
                this.startActivity(page);
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

    public static void requestReadExternalStoragePermission(Activity activity) {

        String requiredPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        ActivityCompat.requestPermissions(activity, new String[]{requiredPermission}, PERMISSIONS_READ_EXTERNAL_STORAGE);

    }

    public static boolean hasReadExternalStoragePermission(Context context) {

        boolean hasPermission = (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        return hasPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_READ_EXTERNAL_STORAGE:
                //If storage permission is true
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Yay, has Storage permission",
//                            Toast.LENGTH_SHORT).show();
                    populateFolders();
                } else {
                    Toast.makeText(this, "permission denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
