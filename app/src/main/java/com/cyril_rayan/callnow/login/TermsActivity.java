package com.cyril_rayan.callnow.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.cyril_rayan.callnow.R;


/**
 * TermsActivity.java - a class for demonstrating the company privace policy and terms and conditions.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @see Activity
 * @since 15/12/15.
 */
public class TermsActivity extends Activity {
	private WebView mWebView;
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doc_viewer_activity);
		boolean isTrems = getIntent().getExtras().getBoolean("isTrems", false);
		mWebView = (WebView) findViewById(R.id.doc_viewer_wv);
		mWebView.getSettings().setJavaScriptEnabled(true);
		try {
			if(isTrems)
				mWebView.loadUrl("file:///android_asset/terms.html");
			else
				mWebView.loadUrl("file:///android_asset/privacy.html");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
