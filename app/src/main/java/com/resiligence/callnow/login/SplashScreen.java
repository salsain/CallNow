package com.resiligence.callnow.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.resiligence.callnow.MainActivity;
import com.resiligence.callnow.R;
import com.resiligence.callnow.login.utils.SharedpreferenceUtility;

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
//        Intent intent =new Intent(SplashScreen.this, PrepareSnowboyActivity.class);
//        SplashScreen.this.startActivity(intent);
//        finish();
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