package com.resiligence.callnow.login;

import static com.resiligence.callnow.login.Constants.SHRD_KEY_LOGGEDIN;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import butterknife.BindView;

import com.resiligence.callnow.PrepareSnowboyActivity;
import com.resiligence.callnow.R;

import com.resiligence.callnow.login.utils.DialogUtil;
import com.resiligence.callnow.login.utils.KeyboardUtil;
import com.resiligence.callnow.login.utils.SharedpreferenceUtility;
import com.resiligence.callnow.login.utils.Validation;
import com.resiligence.callnow.login.webservicedetails.APIService;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * LoginFragment.java - a class for demonstrating the Login Screen.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @see Fragment
 * @since 21/12/15.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private String mUserStr, mPassStr;
    //    @BindView(R.id.userId_et)
    EditText mUserIdEt;
    //    @BindView(R.id.password_et)
    EditText mPasswordEt;
    //    @BindView(R.id.login_btn)
    Button mLoginBtn;
    //    @BindView(R.id.signupTv)
    TextView mSignUpTv;
    //  @BindView(R.id.forgetTv)
    TextView mForgetTv;
    protected Context context = getActivity();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, null);
//        ButterKnife.bind(this, view);
        setup(view);
        mLoginBtn.setOnClickListener(this);
        mSignUpTv.setOnClickListener(this);
        mForgetTv.setOnClickListener(this);
        return view;
    }

    private void setup(View view) {
        mUserIdEt = view.findViewById(R.id.userId_et);
        mPasswordEt = view.findViewById(R.id.password_et);
        mLoginBtn = view.findViewById(R.id.login_btn);
        mSignUpTv = view.findViewById(R.id.signupTv);
        mForgetTv = view.findViewById(R.id.forgetTv);

    }

    /**
     * @desc put URL of HTML page
     */
    private void loginService() {
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
        TelephonyManager tm = (TelephonyManager) getActivity().getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String android_id = tm.getDeviceId();
        if (android_id == null) android_id = "AndroidEmulatorDeviceID";
        SharedpreferenceUtility.getInstance(getActivity()).
                putString("device_id", android_id.toString());

        APIService service = APIService.loginRequest(getActivity(), LoginFrActivity.mGCMRegId, mUserStr, mPassStr);
        service.getData(true);
        service.setServiceListener(new APIService.APIServiceListener() {

            @Override
            public void onCompletion(APIService service, Object data, Error error) {
                if (error == null && data != null) {
                    JSONObject json = (JSONObject) data;
                    Log.v("JSONObject", json.toString());

                    try {
                        if ("200".equalsIgnoreCase(json.getString("responseCode"))) {
                            Toast.makeText(getActivity(), "Login successfully", Toast.LENGTH_SHORT).show();
                            SharedpreferenceUtility.getInstance(getActivity()).
                                    putString("username", mUserStr);
                            SharedpreferenceUtility.getInstance(getActivity()).
                                    putString("password", mPassStr);
                            SharedpreferenceUtility.getInstance(getActivity()).putBoolean(SHRD_KEY_LOGGEDIN, true);

                            runRegularActivity();

                        } else {
                            DialogUtil.showOkListenerDialog(getActivity(), "Tip Now", json.getString("message"), false, new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Dialog dialog = (Dialog) v.getTag();
                                    dialog.dismiss();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void runRegularActivity() {
        //Intent intent = new Intent(getActivity(), WelcomeScreen.class);
        Intent intent = new Intent(getActivity(), PrepareSnowboyActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                if (LoginFrActivity.permition == false) {
                    DialogUtil.showOkListenerDialog(getActivity(), "Tip Now", "You need to allow permition", false, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Dialog dialog = (Dialog) view.getTag();
                            dialog.dismiss();
                        }
                    });
                } else {
                    loginService();
                }
                KeyboardUtil.hideSoftKeyboard(mLoginBtn);
                break;
            case R.id.signupTv:
                SignUpFragment signUpFragment = new SignUpFragment();
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.main_login_rl, signUpFragment).addToBackStack(null).commit();
                mUserIdEt.setText("");
                mPasswordEt.setText("");
                break;
            case R.id.forgetTv:
                ForgetPasswordFr forgetPasswordFr = new ForgetPasswordFr();
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.main_login_rl, forgetPasswordFr).addToBackStack(null).commit();
                mUserIdEt.setText("");
                mPasswordEt.setText("");
                break;
        }
    }
}