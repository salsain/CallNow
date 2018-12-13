package com.common.utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Husain on 15-03-2016.
 */
public class CallnowPrefrences {


    private static final String SHARED_PREFERENCE_NAME = "CallnowSharedPreference";
    private static final String SHARED_PREFERENCE_NAME_TUTORIAL = "InfinitySharedPreferenceTutorial";

    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferencesTutorial;

    public void setClearPrefrence() {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.clear();
        prefsEditor.commit();
    }

    public CallnowPrefrences(Context context) {
//        context = TrackerApplication.getInstance();
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.sharedPreferencesTutorial = context.getSharedPreferences(SHARED_PREFERENCE_NAME_TUTORIAL,
                Context.MODE_PRIVATE);
    }

  

    public void setcontactName(String contactName) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString("contactName", null == contactName ? "" : contactName);
        prefsEditor.commit();
    }

    public String getcontactName() {
        return sharedPreferences.getString("contactName", "");
    }

}
