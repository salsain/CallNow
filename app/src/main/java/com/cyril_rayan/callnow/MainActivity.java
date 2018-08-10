package com.cyril_rayan.callnow;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.cyril_rayan.callnow.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    TextView userActivityTextView;
    private MainFragment mainFragment;
    private InfoFragment mInfoFragment;
    private ModelFragment mModelFragment;

    android.support.v4.app.FragmentManager mFragmentManager;

    private AdView mAdView;
    private TextView callNowTab, infoTab, modelTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentManager = this.getSupportFragmentManager();
        mainFragment = new MainFragment();
        mInfoFragment = new InfoFragment();
        mModelFragment = new ModelFragment();

        mFragmentManager.beginTransaction().add(R.id.mainLl, mInfoFragment).commit();

        callNowTab = (TextView) findViewById(R.id.callNowTab);
        infoTab = (TextView) findViewById(R.id.infoTab);
        modelTab = (TextView) findViewById(R.id.modelTab);

        infoTab.setSelected(true);
        callNowTab.setOnClickListener(this);
        infoTab.setOnClickListener(this);
        modelTab.setOnClickListener(this);

        userActivityTextView = (TextView) findViewById(R.id.userActivityTextView);

        initGoogleApiUpdateActivity();

        prepareAd();
    }

    void prepareAd() {
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
    }

    void updateAdVisiblity() {
        if (mAdView != null)
            mAdView.setVisibility(StaticMemory.getInstance().isPayVersion() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        updateAdVisiblity();
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private void initGoogleApiUpdateActivity() {
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();
    }

    @Override
    public void onClick(View v) {
        //disableText();
        Fragment currentFragment = mFragmentManager.findFragmentById(R.id.mainLl);
        callNowTab.setSelected(false);
        infoTab.setSelected(false);
        modelTab.setSelected(false);
        switch (v.getId()) {
            case R.id.callNowTab:
                if (!(currentFragment instanceof MainFragment)) {
                    callNowTab.setSelected(true);
                    mFragmentManager.beginTransaction().replace(R.id.mainLl, mainFragment).commit();
                }
                break;

            case R.id.infoTab:
                if (!(currentFragment instanceof InfoFragment)) {
                    infoTab.setSelected(true);
                    mFragmentManager.beginTransaction().replace(R.id.mainLl, mInfoFragment).commit();
                }
                break;
            case R.id.modelTab:
                if (!(currentFragment instanceof ModelFragment)) {
                    modelTab.setSelected(true);
                    mFragmentManager.beginTransaction().replace(R.id.mainLl, mModelFragment).commit();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment currentFragment = mFragmentManager.findFragmentById(R.id.mainLl);
        currentFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Fragment currentFragment = mFragmentManager.findFragmentById(R.id.mainLl);
        currentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public GoogleApiClient mApiClient;
    int ActivityRecognisionPereod = 2000;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(this, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, ActivityRecognisionPereod, pendingIntent);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("DetectedActivityUpdates"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("Status");
            Bundle b = intent.getBundleExtra("DetectedActivity");

            DetectedActivity detectedActivity = (DetectedActivity) b.getParcelable("DetectedActivity");
            UpdateActivityTextView(detectedActivity);
        }
    };

    int index = 0;

    private void UpdateActivityTextView(DetectedActivity activity) {
        String activityText = null;

        switch (activity.getType()) {
            case DetectedActivity.IN_VEHICLE: {
                activityText = "IN_VEHICLE";
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                activityText = "ON_BICYCLE";
                break;
            }
            case DetectedActivity.ON_FOOT: {
                activityText = "ON_FOOT";
                break;
            }
            case DetectedActivity.RUNNING: {
                activityText = "RUNNING";
                break;
            }
            case DetectedActivity.STILL: {
                activityText = "STILL";
                break;
            }
            case DetectedActivity.TILTING: {
                activityText = "TILTING";
                break;
            }
            case DetectedActivity.WALKING: {
                activityText = "WALKING";
                break;
            }
            case DetectedActivity.UNKNOWN: {
                activityText = "UNKNOWN";
                break;
            }
        }

        userActivityTextView.setText(activityText + "(" + activity.getConfidence() + "%)" + "\nupdate:" + index++);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

}
