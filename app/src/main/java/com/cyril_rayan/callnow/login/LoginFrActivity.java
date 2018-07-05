package com.cyril_rayan.callnow.login;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RelativeLayout;

import com.common.utility.PermissionCheckService;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

import com.cyril_rayan.callnow.R;

/**
 * LoginFrActivity.java - a class for demonstrating the Login and Signup fragment.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @see FragmentActivity
 * @since 16/12/15.
 */
public class LoginFrActivity extends FragmentActivity {
    private RelativeLayout mMainrl;
    private LoginFragment loginFragment;
    private SignUpFragment signUpFragment;
    private ForgetPasswordFr forgetPasswordFr;
    protected Context context;
    FragmentManager mFragmentManager;
    private static final int REQUEST_WRITE_STORAGE = 112;
    public static Boolean permition = true;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    public static String mGCMRegId = "Android Simulator";

    PermissionCheckService permissionCheckService;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.login_activity);
        //if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        //}
        mFragmentManager = this.getSupportFragmentManager();
        mMainrl = (RelativeLayout) findViewById(R.id.main_login_rl);
        loginFragment = new LoginFragment();
        signUpFragment = new SignUpFragment();
        forgetPasswordFr = new ForgetPasswordFr();
        mFragmentManager.beginTransaction().replace(R.id.main_login_rl, loginFragment).commit();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        checkAndRequestPermissions();
        permissionCheckService = new PermissionCheckService(this, new String[]{
                Manifest.permission.READ_PHONE_STATE});
        if (!permissionCheckService.checkIfAlreadyhavePermissions()) {
            permissionCheckService.requestForSpecificPermission();
        }
    }

    /**
     * @desc Check Marshmallow Permitions
     */
    private boolean checkAndRequestPermissions() {
        int permissionAccessFineLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int permitionAccessCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permitionReadPhoneState = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        int permitionWriteExternalStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionAccessFineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permitionAccessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (permitionReadPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (permitionWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 15);
            return false;
        }
        return true;
    }

    // callback for permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissionCheckService.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("LoginFr Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
