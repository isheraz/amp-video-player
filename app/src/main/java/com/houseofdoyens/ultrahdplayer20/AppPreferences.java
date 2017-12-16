package com.houseofdoyens.ultrahdplayer20;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.*;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Sheraz on 23-Nov-17.
 */

public class AppPreferences {
    public static SharedPreferences preferences;
    public static Editor edit_preferences;
    static AppPreferences singleton;
    public String app = "Ultra_HD_Preference";

    public AppPreferences() {
    }

    public static AppPreferences getInstance() {
        if (singleton == null) {
            singleton = new AppPreferences();
        }

        return singleton;
    }

    public void setAppTheme(String themeColor, String statusBarColor) {
        edit_preferences.putString("actionBar_color", themeColor).commit();
        edit_preferences.putString("statusBar_color", statusBarColor).commit();
    }
}
