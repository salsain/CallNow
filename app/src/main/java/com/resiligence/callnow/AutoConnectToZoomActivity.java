package com.resiligence.callnow;

import static us.zoom.loginexample.ZoomConstants.ZOOM_APP_KEY;
import static us.zoom.loginexample.ZoomConstants.ZOOM_APP_SECRET;
import static us.zoom.loginexample.ZoomConstants.ZOOM_WEB_DOMAIN;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.resiligence.callnow.ZoomUaUtility.ZoomCallMetaInfo;
import java.util.ArrayList;
import us.zoom.loginexample.ZoomLoginActivity;
import us.zoom.loginexample.ZoomWelcomeActivity;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class AutoConnectToZoomActivity extends AppCompatActivity {

    Button callContactsButton, resetZoomLogin, loginToZoom;
    TextView commentari;
    ArrayList<ZoomCallMetaInfo> zoomCallMetaInfos = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_connect_to_zoom);

        callContactsButton = (Button) findViewById(R.id.callZoomContacts);
        callContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callContact();
            }
        });

        resetZoomLogin = (Button) findViewById(R.id.resetZoomLogin);
        resetZoomLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performResetZoomLogin();
            }
        });

        loginToZoom = (Button) findViewById(R.id.loginToZoom);
        loginToZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLoginToZoom();
            }
        });

        commentari = (TextView) findViewById(R.id.commentari);

        zoomCallMetaInfos = ZoomUaUtility.readCalendarEvent(this);
        updateZoomContactList(zoomCallMetaInfos);
        updatelButtonsView();
    }

    void updateZoomContactList(ArrayList<ZoomCallMetaInfo> zoomCallMetaInfos){
        String text = "List for call \n\n";
        int number = 1;
        for (ZoomCallMetaInfo zoomCallMetaInfo : zoomCallMetaInfos) {
            text += ""+number +") "+ zoomCallMetaInfo.calendarCallTitle + "\n ZoomID = " + zoomCallMetaInfo.zoomMetingId + "\n\n";
            number++;
        }
        commentari.setText(text);
    }

    void updatelButtonsView(){
        if (zoomCallMetaInfos != null && !zoomCallMetaInfos.isEmpty())
            callContactsButton.setEnabled(true);
        else
            callContactsButton.setEnabled(false);
    }

    private void callContact() {
        StaticMemory.getInstance().zoomCallMetaInfos = ZoomUaUtility.fromMetta(zoomCallMetaInfos);
        Intent intent = new Intent(this, ZoomWelcomeActivity.class);
        startActivity(intent);
    }

    private void performResetZoomLogin() {
        final ZoomSDK zoomSDK = ZoomSDK.getInstance();
        zoomSDK.initialize(this, ZOOM_APP_KEY, ZOOM_APP_SECRET, ZOOM_WEB_DOMAIN,
            new ZoomSDKInitializeListener() {
                @Override
                public void onZoomSDKInitializeResult(int i, int i1) {
                    zoomSDK.tryAutoLoginZoom();
                }
            });
    }


    private void performLoginToZoom() {
        final ZoomSDK zoomSDK = ZoomSDK.getInstance();
        zoomSDK.initialize(this, ZOOM_APP_KEY, ZOOM_APP_SECRET, ZOOM_WEB_DOMAIN,
            new ZoomSDKInitializeListener() {
                @Override
                public void onZoomSDKInitializeResult(int i, int i1) {
                    //zoomSDK.tryAutoLoginZoom();
                    Intent intent = new Intent(AutoConnectToZoomActivity.this, ZoomLoginActivity.class);
                    startActivity(intent);
                }
            });
    }

    private void saveContactAndNext() {
        clearForms();
    }

    private void clearForms() {
        commentari.setText("");
    }

    void performCall(boolean showToast){

    }

}
