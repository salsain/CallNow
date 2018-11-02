package com.resiligence.callnow.login.utils;

import android.content.Context;

public class GlobalVariables {

	public static boolean checkInternetConnection(Context mContext) {
		android.net.ConnectivityManager conMgr = (android.net.ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr.getActiveNetworkInfo() != null
				&& conMgr.getActiveNetworkInfo().isAvailable()
				&& conMgr.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}
	}

}
