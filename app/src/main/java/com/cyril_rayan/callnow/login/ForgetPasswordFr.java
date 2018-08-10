package com.cyril_rayan.callnow.login;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Toast;
//import butterknife.BindView;
import com.cyril_rayan.callnow.login.utils.DialogUtil;
import com.cyril_rayan.callnow.login.utils.KeyboardUtil;
import com.cyril_rayan.callnow.login.webservicedetails.APIService;
import org.json.JSONException;
import org.json.JSONObject;

//import butterknife.ButterKnife;
import com.cyril_rayan.callnow.R;

import butterknife.BindView;

/**
 * ForgetPasswordFr.java - a class for demonstrating Forget password screen.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @see Fragment
 * @since 21/12/15.
 */
public class ForgetPasswordFr  extends Fragment implements View.OnClickListener {
//    @BindView(R.id.backIv)
    ImageView mBackIv;
//    @BindView(R.id.sendBtn)
    Button mBtnSend;
//    @BindView(R.id.email_et)
    EditText mEmail_et;
    String mUserStr;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.forget_password_fragment, null);
//        ButterKnife.bind(this,view);
        mBackIv.setOnClickListener(this);
        setup(view);
        mBtnSend.setOnClickListener(this);
        return view;
    }

    private void setup(View view) {
        mBackIv = view.findViewById(R.id.backIv);
        mBtnSend = view.findViewById(R.id.sendBtn);
        mEmail_et = view.findViewById(R.id.email_et);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendBtn:
                authentication();
                KeyboardUtil.hideSoftKeyboard(mBtnSend);
                break;

            case R.id.backIv:
                getActivity().onBackPressed();
                break;
        }
    }

    private void authentication() {
        mUserStr = mEmail_et.getText().toString().trim();

        if (mUserStr == null || mUserStr.equalsIgnoreCase("")) {
            mEmail_et.setError("Email Id Required");
            return;
        }
        APIService service = APIService.forgetPassRequest(getActivity(), mUserStr);
        service.getData(true);
        service.setServiceListener(new APIService.APIServiceListener() {

            @Override
            public void onCompletion(APIService service, Object data, Error error) {
                    if (error == null && data != null) {
                    JSONObject json = (JSONObject) data;
                    Log.v("JSONObject", json.toString());
                        try {
                        if("200".equalsIgnoreCase(json.getString("responseCode"))){
                            DialogUtil.showOkListenerDialog(getActivity(), "Tip Now", json.getString("message"), false, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Dialog dialog = (Dialog) v.getTag();
                                    dialog.dismiss();
                                    //Toast.makeText(getContext(), "Reset password request sent, see your email now", Toast.LENGTH_LONG).show();
                                }
                            });
                            ((LoginFrActivity)getActivity()).onBackPressed();
                        }else{
                            DialogUtil.showOkListenerDialog(getActivity(), "Tip Now", json.getString("message"), false, new View.OnClickListener() {

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


