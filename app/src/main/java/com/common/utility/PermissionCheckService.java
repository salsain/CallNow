package com.common.utility;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;

import static android.R.attr.permission;

/**
 * Created Airat Gabitov on 12.06.2017.
 */

public class PermissionCheckService {

    Activity activity;
    static final int REQUEST_ALL_PERMISIONS = 10021;
    static final int REQUEST_PERMISSION_SETTING = 10022;

    String[] permissionArray;

    public boolean closeAppIfDissalow = true;
    public Runnable afterAllowAction = null;

    public PermissionCheckService(Activity activity, String[] permissionArray) {
        this.activity = activity;
        this.permissionArray = permissionArray;
    }

    public boolean checkIfAlreadyhavePermissions() {

        for (String permision : permissionArray) {
            int result = ContextCompat.checkSelfPermission(activity, permision);
            if (result == PackageManager.PERMISSION_GRANTED) {
                continue;
            } else {
                return false;
            }
        }

        return true;
    }

    public void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(activity, permissionArray, REQUEST_ALL_PERMISIONS);
    }


    private boolean allPermisionsGranted(String[] permissionsGranted, int[] grantResults){
        ArrayList<String> permissionsGrantedArray = new ArrayList<>(Arrays.asList(permissionsGranted));

        for (String permision : permissionArray) {
            int findIndex = permissionsGrantedArray.indexOf(permision);
            if (findIndex != -1) {
                if (grantResults[findIndex] != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
        }
        return true;
    }

    boolean mustGoSettings(String[] permissionsGranted, int[] grantResults){
        ArrayList<String> permissionsGrantedArray = new ArrayList<>(Arrays.asList(permissionsGranted));

        for (String permision : permissionArray) {
            int findIndex = permissionsGrantedArray.indexOf(permision);
            if (findIndex != -1) {
                if (grantResults[findIndex] == PackageManager.PERMISSION_DENIED) {
                    boolean showRationale = false;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        showRationale = activity.shouldShowRequestPermissionRationale(permision);
                        if (!showRationale) {
                            // user rejected the permission
                            return true;
                        }
                    }
                    else {
                        break;
                    }

                }
            }
        }
        return false;
    }

    void requestForSpecificPermissionInSettings(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
    }

    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ALL_PERMISIONS:
                if (allPermisionsGranted(permissions, grantResults)) {
                    if (afterAllowAction != null) {
                        afterAllowAction.run();
                    }
                    return true;
                } else if (mustGoSettings(permissions, grantResults)) {
                    showSettingsExplanationAlert();
                }
                else {
                    showExplanationAlert();
                }
                break;
            case REQUEST_PERMISSION_SETTING:
                if (allPermisionsGranted(permissions, grantResults)) {
                    if (afterAllowAction != null) {
                        afterAllowAction.run();
                    }
                    return true;
                } else {
                    showExplanationAlert();
                }
                break;
            default:
                break;
        }
        return false;
    }

    void showSettingsExplanationAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Application needs permission")
                .setMessage("Now you can give it only in application settings")
                .setCancelable(false)
                .setPositiveButton("Give Permission",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                requestForSpecificPermissionInSettings();
                            }
                        })
                .setNegativeButton(closeAppIfDissalow ? "Close app" : "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    void showExplanationAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Application needs permission")
        //builder.setTitle(closeAppIfDissalow ? "Application needs permission" : "Action need permissions")
                //.setMessage(closeAppIfDissalow ? "Please give Permissions for application, it need for correct application work" : "Please give Permissions for this action, it need for perform it")
                .setCancelable(false)
                .setPositiveButton("Give Permission",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                requestForSpecificPermission();
                            }
                        })
                .setNegativeButton(closeAppIfDissalow ? "Close app" : "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (closeAppIfDissalow)
                                    activity.finish();
                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();
    }

}
