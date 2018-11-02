package com.resiligence.callnow;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

//import com.google.android.gms.location.ActivityRecognitionResult;
//import com.google.android.gms.location.DetectedActivity;

/**
 * Created by Айрат on 16.07.2017.
 */

public class ActivityRecognizedService extends IntentService {

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

//    private void sendMessageToActivity(DetectedActivity detectedActivity, String msg) {
//        Intent intent = new Intent("DetectedActivityUpdates");
//        intent.putExtra("DetectedActivity", "");
//        Bundle b = new Bundle();
//        b.putParcelable("DetectedActivity", detectedActivity);
//        intent.putExtra("DetectedActivity", b);
//
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        if(ActivityRecognitionResult.hasResult(intent)) {
//            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
//            DetectedActivity mostProbableActivity = result.getMostProbableActivity();
//            sendMessageToActivity(mostProbableActivity, "");
//        }
    }
}