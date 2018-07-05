package com.cyril_rayan.callnow.login.webservicedetails;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import com.cyril_rayan.callnow.login.utils.DialogUtil;

/**
 * Created by Canopus InfoSystems pvt. on 20/8/15.
 */
public class NetworkAvailablity {
    public static NetworkAvailablity mRefrence = null ;

    public static NetworkAvailablity getInstance() {
        if( null == mRefrence)
            mRefrence = new NetworkAvailablity();
        return mRefrence ;}
    /**
     * Check network availability
     * */
    public boolean checkNetworkStatus(Context context)
    {
        try {
            boolean HaveConnectedWifi = false;
            boolean HaveConnectedMobile = false;
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo)
            {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        HaveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        HaveConnectedMobile = true;
            }
            if(!HaveConnectedWifi && !HaveConnectedMobile){
                DialogUtil
                    .showOkListenerDialog(context, "Tip Now", "No network available.", false, new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Dialog dialog = (Dialog) v.getTag();
                        dialog.dismiss();

                    }
                });       }
            return HaveConnectedWifi || HaveConnectedMobile;
        } catch (Exception e) {
            return false;
        }
    }
}


