package com.cyril_rayan.callnow.login;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;

import com.cyril_rayan.callnow.login.utils.DialogUtil;
import com.cyril_rayan.callnow.login.utils.Validation;
import com.cyril_rayan.callnow.login.webservicedetails.APIService;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.ButterKnife;

import com.cyril_rayan.callnow.R;

/**
 * SignUpFragment.java - a class for demonstrating the Signup Screen.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @see Fragment
 * @since 21/12/15.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.userId_et)
    EditText mUserIdEt;
    @BindView(R.id.password_et) EditText mPasswordEt;
    @BindView(R.id.backIv)
    ImageView mBackIv;
    @BindView(R.id.signup_btn)
    Button mSignupBtn;
    private String mUserStr, mPassStr;
    Map<String,String> map;
    private String success, error;
    ProgressDialog dialog;
    Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.signup_fragment, null);
        ButterKnife.bind(this,view);
        mBackIv.setOnClickListener(this);
        mSignupBtn.setOnClickListener(this);
        mContext 	= getActivity();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_btn:
                signUpService();
                break;

            case R.id.backIv:
                ((LoginFrActivity)mContext).onBackPressed();
                break;
        }
    }
    private void signUpService() {

        mUserStr = mUserIdEt.getText().toString().trim();
        mPassStr = mPasswordEt.getText().toString().trim();
        if (mUserStr == null || mUserStr.equalsIgnoreCase("")) {
            mUserIdEt.setError("Username Required");
            return;
        } else if (!Validation.isValidEmail(mUserStr)) {
            mUserIdEt.setError("Invalid Email");
            return;
        } else
            mUserIdEt.setError(null);

        if (mPassStr == null || mPassStr.equalsIgnoreCase("")) {
            mPasswordEt.setError("Password Required");
            return;
        } else
            mUserIdEt.setError(null);
       /* String android_id = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);*/
        TelephonyManager tm = (TelephonyManager)getActivity().getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        String android_id= tm.getDeviceId();

        APIService service = APIService.signUpRequest(mContext,LoginFrActivity.mGCMRegId, mUserStr, mPassStr);
        service.getData(true);

        service.setServiceListener(new APIService.APIServiceListener() {
            @Override
            public void onCompletion(APIService service, Object data, Error error) {
                if (error == null && data != null) {
                    JSONObject json = (JSONObject) data;
                    Log.v("JSONObject", json.toString());

                    try {
                        if("200".equalsIgnoreCase(json.getString("responseCode"))){
                            Toast.makeText(mContext, json.getString("message"), Toast.LENGTH_SHORT).show();
                            DialogUtil.showOkListenerDialog(mContext, "Tip Now", "We have sent verification mail to your address. Please check mail", false, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Dialog dialog = (Dialog) v.getTag();
                                    Intent intent = new Intent(mContext, LoginFrActivity.class);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            });
                        }else{
                            DialogUtil.showOkListenerDialog(mContext, "Tip Now", json.getString("message"), false, new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Dialog dialog = (Dialog) v.getTag();
                                    dialog.dismiss();
                                }
                            });
                        }
                    } catch (JSONException e) {		e.printStackTrace();		}
                }
            }
        });


    }
}



