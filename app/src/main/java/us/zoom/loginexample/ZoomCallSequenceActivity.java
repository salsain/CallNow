package us.zoom.loginexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.cyril_rayan.callnow.R;
import com.cyril_rayan.callnow.StaticMemory;
import com.cyril_rayan.callnow.ZoomUaUtility.ZoomCallInfo;
import us.zoom.sdk.InstantMeetingOptions;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.MeetingError;
import us.zoom.sdk.MeetingEvent;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;

public class ZoomCallSequenceActivity extends Activity implements ZoomConstants, MeetingServiceListener, ZoomSDKAuthenticationListener {

	private final static String TAG = "ZoomSDKExample";

	private final static String DISPLAY_NAME = "ZoomUS SDK";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.zoom_call_sequence_activity);

		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		if(zoomSDK.isInitialized()) {
			zoomSDK.addAuthenticationListener(this);
			MeetingService meetingService = zoomSDK.getMeetingService();
			if(meetingService != null) {
				meetingService.addListener(this);
			}
		}

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				callFirstInListMeetingID();
			}
		}, 100);
	}

	@Override
	protected void onDestroy() {
		ZoomSDK zoomSDK = ZoomSDK.getInstance();
		if(zoomSDK.isInitialized()) {
			zoomSDK.removeAuthenticationListener(this);
			MeetingService meetingService = zoomSDK.getMeetingService();
			meetingService.removeListener(this);
		}

		super.onDestroy();
	}

	@Override
	public void onMeetingEvent(int meetingEvent, int errorCode,
			int internalErrorCode) {

		if (meetingEvent == MeetingEvent.MEETING_DISCONNECTED){
			onCallEnded();
			return;
		}

		Log.i(TAG, "onMeetingEvent, meetingEvent=" + meetingEvent + ", errorCode=" + errorCode
				+ ", internalErrorCode=" + internalErrorCode);

		if(meetingEvent == MeetingEvent.MEETING_CONNECT_FAILED && errorCode == MeetingError.MEETING_ERROR_CLIENT_INCOMPATIBLE) {
			Toast.makeText(this, "Version of ZoomSDK is too low!", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onZoomSDKLoginResult(long result) {
		if(result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
			Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Login failed result code = " + result, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onZoomSDKLogoutResult(long result) {
		if(result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
			Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show();
			showLoginView();
			finish();
		} else {
			Toast.makeText(this, "Logout failed result code = " + result, Toast.LENGTH_SHORT).show();
		}
	}

	public boolean callFirstInListMeetingID() {

		ZoomCallInfo firstInRaw = null;
		for (ZoomCallInfo zoomCallInfo : StaticMemory.getInstance().zoomCallMetaInfos) {
			if (zoomCallInfo.called)
				continue;
			firstInRaw = zoomCallInfo;
		}

		if  (firstInRaw == null)
			return false;

		String meetingNo = firstInRaw.zoomMetingId;
		firstInRaw.called = true;

		if(meetingNo == null || meetingNo.length() == 0) {
			Toast.makeText(this, "You need to enter a scheduled meeting number.", Toast.LENGTH_LONG).show();
			return false;
		}

		ZoomSDK zoomSDK = ZoomSDK.getInstance();

		if(!zoomSDK.isInitialized()) {
			Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
			return false;
		}

		MeetingService meetingService = zoomSDK.getMeetingService();

		StartMeetingOptions opts = new StartMeetingOptions();
		int ret = meetingService.startMeeting(this, meetingNo, opts);

		Log.i(TAG, "onClickBtnLoginUserStart, ret=" + ret);
		return true;
	}

	void onCallEnded(){
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (!callFirstInListMeetingID()) {
					//call ended empty list!
					finish();
				}
			}
		}, 100);

	}

	private void showLoginView() {
		Intent intent = new Intent(this, ZoomLoginActivity.class);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		finish();
		//moveTaskToBack(true);
	}
}
