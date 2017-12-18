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
    private static String adID, testDevice;

    public AdManager() {
        adID = String.valueOf(R.string.interstitial_ad_unit_id);
        testDevice = String.valueOf(R.string.test_device);
    }

    /***
     * returns an instance of this class. if singleton is null create an instance
     * else return  the current instance
     * @return AdManager
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
        /*if( adCounter % 2 != 0){
            adID = "ca-app-pub-3882915167442181/3423031112";
            adID = "ca-app-pub-5104299357927424/6726692784";
        }else{
            adID = "ca-app-pub-3882915167442181/1550739378";
//            adID = "ca-app-pub-5104299357927424/7538371067";
        }*/
        adID = "ca-app-pub-5104299357927424/6726692784";
        interstitialAd.setAdUnitId(adID);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(testDevice)
                .build();
        interstitialAd.loadAd(adRequest);
        adCounter++;
    }

    /***
     * get an interstitial Ad
     * @return interstitialAd
     */
    public static InterstitialAd getAd() {
        return interstitialAd;
    }
}
