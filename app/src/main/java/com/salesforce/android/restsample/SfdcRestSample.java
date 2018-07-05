package com.salesforce.android.restsample;

import java.net.URLDecoder;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.cyril_rayan.callnow.R;
import com.cyril_rayan.callnow.StaticMemory;
import com.cyril_rayan.callnow.SubscriptionsScreen;

import static com.cyril_rayan.callnow.MainFragment.REQUEST_IMPORT_SALESFORCE_CONTACTS;

public class SfdcRestSample extends Activity {

    WebView webview;
    String callbackUrl = "";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sales_force_import_layout);

        if (1<0) {//!StaticMemory.getInstance().enableSalesForceFeature) {
            showNotPaidAlert();
            //Toast.makeText(this, getString(R.string.get_crm_disable_warning_not_paid), Toast.LENGTH_LONG).show();
            return;
        }

        String consumerKey = "3MVG9ROcrWLs7Uy8AUwfYz64.I5QMHXi7NA7C.9.05hcdoKcIg_BsfpAUGFhKFBn1gr_RnrpCmJ_WzmlNeaKh";//this.getResources().getString(R.string.consumerKey).toString();
        String url = this.getResources().getString(R.string.oAuthUrl).toString();
        callbackUrl = this.getResources().getString(R.string.callbackUrl).toString();

        String reqUrl = url + "/services/oauth2/authorize?response_type=token&display=touch&client_id=" + consumerKey + "&redirect_uri=" + callbackUrl;

        webview = (WebView) findViewById(R.id.webview);
        webview.setWebViewClient(new HelloWebViewClient());

        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(reqUrl);

        //FirebaseCrash.report(new Exception("SfdcRestSample runned!"));
    }

    void showNotPaidAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.get_crm_disable_warning_not_paid))
                .setMessage(getString(R.string.get_crm_disable_warning_not_paid_need_subscribe))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.subscriptions),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(SfdcRestSample.this, SubscriptionsScreen.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                .setNeutralButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class HelloWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            // TODO Auto-generated method stub
            super.onReceivedError(view, errorCode, description, failingUrl);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs

            if (url.startsWith(callbackUrl)) {
                final String finalURL = url;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parseToken(finalURL);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                webview.setVisibility(View.INVISIBLE);
                                onSaleforceLoginned(findViewById(R.id.parentLayout));
                            }
                        }, 200);
                    }
                });
            }

            view.loadUrl(url);
            return true;
        }

    }   //HelloWebViewClient-class

    void onSaleforceLoginned(View overflowImageView){
        PopupMenu menu = new PopupMenu(this, overflowImageView);
        menu.inflate(R.menu.import_crm_menu);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.import_all_contacts: {
                        Intent i = new Intent(SfdcRestSample.this, AccountGetActivity.class);
                        startActivityForResult(i, REQUEST_IMPORT_SALESFORCE_CONTACTS);
                        return true;
                    }
                    case R.id.import_follow_up: {
                        Intent i = new Intent(SfdcRestSample.this, FollowUpGetActivity.class);
                        startActivityForResult(i, REQUEST_IMPORT_SALESFORCE_CONTACTS);
                        return true;
                    }
                    case R.id.import_leads_contacts: {
                        Intent i = new Intent(SfdcRestSample.this, LeadsGetActivity.class);
                        startActivityForResult(i, REQUEST_IMPORT_SALESFORCE_CONTACTS);
                        return true;
                    }
                    default:
                        return false;
                }
            }
        });

        MenuPopupHelper menuHelper = new MenuPopupHelper(this, (MenuBuilder) menu.getMenu(), overflowImageView);

        menuHelper.setForceShowIcon(true);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        menu.setGravity(Gravity.CENTER);

        menuHelper.show(width/2, height/2);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMPORT_SALESFORCE_CONTACTS) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
                return;
            } else {
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    static private void parseToken(String url) {
        String temp = url.split("#")[1];
        String[] keypairs = temp.split("&");
        OAuthTokens myTokens = new OAuthTokens();
        for (int i = 0; i < keypairs.length; i++) {
            String[] onepair = keypairs[i].split("=");
            if (onepair[0].equals("access_token")) {
                myTokens.set_access_token(URLDecoder.decode(onepair[1]));
            } else if (onepair[0].equals("refresh_token")) {
                myTokens.set_refresh_token(onepair[1]);
            } else if (onepair[0].equals("instance_url")) {
                myTokens.set_instance_url(onepair[1]);
                myTokens.set_instance_url(myTokens.get_instance_url().replaceAll("%2F", "/"));
                myTokens.set_instance_url(myTokens.get_instance_url().replaceAll("%3A", ":"));
            } else if (onepair[0].equals("id")) {
                myTokens.set_id(onepair[1]);
            } else if (onepair[0].equals("issued_at")) {
                myTokens.set_issued_at(Long.valueOf(onepair[1]));
            } else if (onepair[0].equals("signature")) {
                myTokens.set_signature(onepair[1]);
            }
        }

        StaticMemory.getInstance().setAccessTokens(myTokens);
    }

}