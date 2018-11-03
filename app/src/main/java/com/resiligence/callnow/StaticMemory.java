package com.resiligence.callnow;

import com.resiligence.callnow.ZoomUaUtility.ZoomCallInfo;
import com.salesforce.android.restsample.OAuthTokens;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Айрат on 04.06.2017.
 */

public class StaticMemory {
    public boolean enableMore5CallsFeature = false;
    public boolean enableSalesForceFeature = false;

    public volatile ArrayList<ContactInfo> contactClassList;
    public volatile ArrayList<ContactInfo> selectedContactClassList;
    public volatile ArrayList<ZoomCallInfo> zoomCallMetaInfos;

    private JSONObject selectedAccount;
    private OAuthTokens accessTokens;

    public OAuthTokens getAccessTokens() {
        return accessTokens;
    }

    public void setAccessTokens(OAuthTokens accessTokens) {
        this.accessTokens = accessTokens;
    }

    public JSONObject getSelectedAccount() {
        return selectedAccount;
    }

    public void setSelectedAccount(JSONObject selectedAccount) {
        this.selectedAccount = selectedAccount;
    }

    public static StaticMemory getInstance() {
        //we add static instance to main context thread.
        //Do not remove it from MainApplication because it is main context and this instance will be live with any activity
        if (MainApplication.staticMemory == null)
            MainApplication.staticMemory = new StaticMemory();

        return MainApplication.staticMemory;
    }
//    public boolean isPayVersion() {
//        return (enableMore5CallsFeature || enableSalesForceFeature);
//    }

    public boolean isPaySalesForceVersion() {
        return ( enableSalesForceFeature);
    }

    public boolean isPayContactVersion() {
        return (enableMore5CallsFeature);
    }
}
