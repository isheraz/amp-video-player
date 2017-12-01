package com.houseofdoyens.ultrahdplayer20;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

public class FolderVideosActivity extends AppCompatActivity {

    ArrayList<VideoViewInfo> AllVideosData = new ArrayList<>();
    ArrayList<String> videosPath = new ArrayList<>();
    ArrayList<String> videosName = new ArrayList<>();
    ArrayList<String> videosDuration = new ArrayList<>();
    ArrayList<String> videosResulotion = new ArrayList<>();
    private AdView adView;
    GridView gridView;
    GridAdapterVideos adapter;
    RelativeLayout mainLayout;
    /*Shared Preferences    */
    private AppPreferences properties;

    private InterstitialAd fullAd;
//    private static AdManager ads;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_videos);

        fullAd = AdManager.getAd();
        if (fullAd.isLoaded()) {
            fullAd.show();
        } else {
            AdManager.createAd(this);
        }
        adView = (AdView) findViewById(R.id.adView);
        adRequest = new AdRequest.Builder()
                .addTestDevice("B2A4ACD09989A482DEBA3063377FC1F0").build();
        adView.loadAd(adRequest);

        gridView = (GridView) findViewById(R.id.gridView);
        /* Ads Ends Here */
        Bundle bundle = getIntent().getExtras();
        String folderName = bundle.getString("folderName");

        setTitle(folderName);
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);

        String statusBarColor = properties.preferences.getString("statusBar_color", "#303F9F");
        String realThemeColor = properties.preferences.getString("actionBar_color", "#3F51B5");
//        String activityBackgroundColor = properties.preferences.getString("background_color", "#FFFFFF");

        changeTheme(statusBarColor, realThemeColor);

        GetVideos gv = new GetVideos();
        gv.getAllVideosData(FolderVideosActivity.this, AllVideosData);

        for (int i = 0; i < AllVideosData.size(); i++) {

            if (folderName.equals(AllVideosData.get(i).getBucketName())) {
                videosName.add(AllVideosData.get(i).getTitle());
                videosPath.add(AllVideosData.get(i).getFilePath());
                videosDuration.add(AllVideosData.get(i).getDuration());
                videosResulotion.add(AllVideosData.get(i).getResolution());
            }
        }

        String[] path = new String[videosPath.size()];
        videosPath.toArray(path);

        String[] name = new String[videosName.size()];
        videosName.toArray(name);

        String[] duration = new String[videosDuration.size()];
        videosDuration.toArray(duration);

        String[] resolution = new String[videosResulotion.size()];
        videosResulotion.toArray(resolution);

        adapter = new GridAdapterVideos(FolderVideosActivity.this, path, name, duration, resolution);
        gridView.setAdapter(adapter);
        properties.edit_preferences.putString("parent_activity", "FolderVideosActivity").commit();
    }

    public void changeTheme(String statusBarColor, String realThemeColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(statusBarColor));
        }
        ActionBar actionBar = getSupportActionBar();
        ColorDrawable themeColor = new ColorDrawable(Color.parseColor(realThemeColor));
//        mainLayout.setBackground(themeColor);
        actionBar.setBackgroundDrawable(themeColor);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (fullAd.isLoaded()) {
            fullAd.show();
        } else {
            AdManager.createAd(this);
        }
        adView = (AdView) findViewById(R.id.adView);
        adRequest = new AdRequest.Builder()
                .addTestDevice(String.valueOf(R.string.banner_ad_unit_id)).build();
        adView.loadAd(adRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*What Menu item is selected*/
        switch (id) {
            default:
               finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
