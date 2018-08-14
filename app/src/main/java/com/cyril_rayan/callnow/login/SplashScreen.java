package com.cyril_rayan.callnow.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.cyril_rayan.callnow.MainActivity;
import com.cyril_rayan.callnow.R;
import com.cyril_rayan.callnow.WelcomeScreen;
import com.cyril_rayan.callnow.login.utils.SharedpreferenceUtility;

/**
 * SplashScreen.java - a class for demonstrating the splash screen.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @see Activity
 * @since 14/12/15.
 */
public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        initializeHandler();
    }

    private void initializeHandler() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(SharedpreferenceUtility.getInstance(SplashScreen.this).getBoolean(Constants.SHRD_KEY_LOGGEDIN)){
                    Intent intent =new Intent(SplashScreen.this, MainActivity.class);
                    SplashScreen.this.startActivity(intent);
                    finish();
                }else{
                    Intent  intent =new Intent(SplashScreen.this,LoginFrActivity.class);
                    SplashScreen.this.startActivity(intent);
                    finish();
                }

            }
        }, 1000);

    }

}