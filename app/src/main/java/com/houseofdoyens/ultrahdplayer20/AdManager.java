package com.houseofdoyens.ultrahdplayer20;

import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by Sheraz on 21-Nov-17.
 */

public class AdManager {

    static AdManager singleton;
    static InterstitialAd interstitialAd;
    static int adCounter = 0;

    public AdManager() {
    }

    /***
     * returns an instance of this class. if singleton is null create an instance
     * else return  the current instance
     * @return
     */
    public static AdManager getInstance() {
        if (singleton == null) {
            singleton = new AdManager();
        }

        return singleton;
    }

    /***
     * Create an interstitial ad
     * @param context
     */
    public static void createAd(Context context) {
        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(String.valueOf(R.string.interstitial_ad_unit_id));
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(String.valueOf(R.string.test_device))
                .build();
        interstitialAd.loadAd(adRequest);
        adCounter++;
    }
    public static void createAd(Context context, AdRequest request) {
        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(String.valueOf(R.string.interstitial_ad_unit_id));
        interstitialAd.loadAd(request);
    }



    /***
     * get an interstitial Ad
     * @return
     */
    public static InterstitialAd getAd() {
        return interstitialAd;
    }
}
