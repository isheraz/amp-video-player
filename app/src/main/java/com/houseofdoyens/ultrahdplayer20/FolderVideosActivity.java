package com.houseofdoyens.ultrahdplayer20;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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


    AdView adView ;


    GridView gridView;
    GridAdapterVideos adapter;

    ActionBar ab;

    RelativeLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_videos);

        AdManager adManager = AdManager.getInstance();
        InterstitialAd ad =  adManager.getAd();

        if (ad.isLoaded()) {
            ad.show();
        }
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(String.valueOf(R.string.banner_ad_unit_id)).build();
        adView.loadAd(adRequest);

        gridView = (GridView) findViewById(R.id.gridView);
        /* Ads Ends Here */
        Bundle bundle = getIntent().getExtras();
        String folderName = bundle.getString("folderName");

        setTitle(folderName);

        ab = getSupportActionBar();

//        String statusBarColor = MainActivity.sp.getString("statusBar_color", "#303F9F");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(Color.parseColor(statusBarColor));
//        }

//        String realThemeColor = MainActivity.sp.getString("actionBar_color", "#3F51B5");
//        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor(realThemeColor)));

//        String activityBackgroundColor = MainActivity.sp.getString("background_color", "#FFFFFF");
//        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
//        mainLayout.setBackgroundColor(Color.parseColor(activityBackgroundColor));

        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);


        GetVideos gv = new GetVideos();
        gv.getAllVideosData(FolderVideosActivity.this, AllVideosData);

        for(int i=0; i<AllVideosData.size(); i++) {

            if(folderName.equals(AllVideosData.get(i).getBucketName())) {
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
//        MainActivity.sp_e.putString("parent_activity", "FolderVideosActivity").commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdManager adManager = AdManager.getInstance();
        InterstitialAd ad =  adManager.getAd();
        if (ad.isLoaded()) {
            ad.show();
        }
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(String.valueOf(R.string.banner_ad_unit_id)).build();
        adView.loadAd(adRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*What Meny item is selected*/
        switch (id) {
            default:
                Intent page = new Intent(this, MainActivity.class);
                this.startActivity(page);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
