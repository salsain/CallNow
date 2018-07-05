package com.common.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.cyril_rayan.callnow.AddContactActivity;
import com.cyril_rayan.callnow.AutoConnectToZoomActivity;
import com.cyril_rayan.callnow.SelectContactsActivity;
import com.cyril_rayan.callnow.StaticMemory;
import com.google.ad.AdxActivity;
import com.salesforce.android.restsample.SfdcRestSample;

import static com.cyril_rayan.callnow.MainFragment.REQUEST_AUTO_CONNECT_TO_ZOOM;
import static com.cyril_rayan.callnow.MainFragment.REQUEST_IMPORT_SALESFORCE_CONTACTS;
import static com.cyril_rayan.callnow.MainFragment.REQUEST_MANUAL_ADD_CONTACTS;
import static com.cyril_rayan.callnow.MainFragment.REQUEST_SELECT_CONTACTS;

/**
 * Created by Айрат on 02.08.2017.
 */

public class ViaAdActivityRunner {

    static final String ACTIVITY_NAME_PARAM = "ACTIVITY_NAME_PARAM";
    static final String ACTIVITY_RESULT_ID_PARAM = "ACTIVITY_RESULT_ID_PARAM";
    static final String ACTIVITY_PARAM1 = "ACTIVITY_PARAM1";

    static Activity activityForRetrieveResult;

    public static void runAddContactActivity(Activity activity) {
        activityForRetrieveResult = activity;
        if (StaticMemory.getInstance().isPayVersion()) {
            startActivityByName(AddContactActivity.class.getName(), REQUEST_MANUAL_ADD_CONTACTS, null);
            return;
        }

        runAdxWithParams(AddContactActivity.class.getName(), REQUEST_MANUAL_ADD_CONTACTS, null);
    }

    public static void runAutoConnectToZoom(Activity activity) {
        activityForRetrieveResult = activity;
        if (StaticMemory.getInstance().isPayVersion()) {
            startActivityByName(AutoConnectToZoomActivity.class.getName(), REQUEST_AUTO_CONNECT_TO_ZOOM, null);
            return;
        }

        runAdxWithParams(AutoConnectToZoomActivity.class.getName(), REQUEST_AUTO_CONNECT_TO_ZOOM, null);
    }

    public static void runSalesForceActivity(Activity activity) {
        activityForRetrieveResult = activity;
        if (StaticMemory.getInstance().isPayVersion()) {
            startActivityByName(SfdcRestSample.class.getName(), REQUEST_IMPORT_SALESFORCE_CONTACTS, null);
            return;
        }

        runAdxWithParams(SfdcRestSample.class.getName(), REQUEST_IMPORT_SALESFORCE_CONTACTS, null);
    }

    public static void runSelectContactsActivity(Activity activity, boolean useSystemContacts) {
        activityForRetrieveResult = activity;
        if (StaticMemory.getInstance().isPayVersion()) {
            startActivityByName(SelectContactsActivity.class.getName(), REQUEST_SELECT_CONTACTS, useSystemContacts ? "true" : "false");
            return;
        }

        runAdxWithParams(SelectContactsActivity.class.getName(), REQUEST_SELECT_CONTACTS, useSystemContacts ? "true" : "false");
    }

    public static void startActivityByName(String classname, int activityResultID, String specialParam1){
        Activity activity = activityForRetrieveResult;
        if (classname.equals(AddContactActivity.class.getName())) {
            Intent intent = new Intent(activity, AddContactActivity.class);
            activity.startActivityForResult(intent, activityResultID);
        } else
        if (classname.equals(SfdcRestSample.class.getName())) {
            Intent intent = new Intent(activity, SfdcRestSample.class);
            activity.startActivityForResult(intent, activityResultID);
        } else
        if (classname.equals(SelectContactsActivity.class.getName())) {
            Intent intent = new Intent(activity, SelectContactsActivity.class);
            if (specialParam1 != null) {
                Bundle b = new Bundle();
                b.putBoolean(SelectContactsActivity.USE_SYSTEM_CONTACTS, specialParam1.equals("true"));
                intent.putExtras(b);
            }
            activity.startActivityForResult(intent, activityResultID);
        } else
        if (classname.equals(AutoConnectToZoomActivity.class.getName())) {
            Intent intent = new Intent(activity, AutoConnectToZoomActivity.class);
            activity.startActivityForResult(intent, activityResultID);
        }
    }

    private static void runAdxWithParams(String name, int requestResultId, String param1) {
        Activity activity = activityForRetrieveResult;
        Intent adx = new Intent(activity, AdxActivity.class);

        Bundle b = new Bundle();
        b.putString(ACTIVITY_NAME_PARAM, name);
        b.putInt(ACTIVITY_RESULT_ID_PARAM, requestResultId);
        b.putString(ACTIVITY_PARAM1, param1);
        adx.putExtras(b);

        activity.startActivity(adx);
    }

    public static void runActivityByBundle(Bundle bundle) {
        String activityName = bundle.getString(ACTIVITY_NAME_PARAM);
        int resultIdParam = bundle.getInt(ACTIVITY_RESULT_ID_PARAM);
        String param1 = bundle.getString(ACTIVITY_PARAM1);

        startActivityByName(activityName, resultIdParam, param1);
    }
}
