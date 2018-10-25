package com.cyril_rayan.callnow.login.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.cyril_rayan.callnow.R;

/**
 * WhiteProgressDialog.java - a class for Progress Dialog
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 22/12/15.
 */
public class WhiteProgressDialog extends Dialog {

    public WhiteProgressDialog(Context context, int msgResId) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.white_progress_dialog);
    }
}
