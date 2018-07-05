package com.cyril_rayan.callnow.login.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.cyril_rayan.callnow.R;
import com.cyril_rayan.callnow.login.TermsActivity;

/**
 * DialogUtil.java - a class for show Dialog Box
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 22/12/15.
 */
public class DialogUtil {
	public static Dialog showOkCancelDialog(final Context context, String titleResId, String msgResId, OnClickListener okListener, OnClickListener cancelListener) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		dialog.setContentView(R.layout.ok_cancel_dialog);

		TextView titleTv = (TextView) dialog.findViewById(R.id.title_tv);
		if (titleResId.length() > 0) {
			titleTv.setText(titleResId);
		} else {
			titleTv.setVisibility(View.GONE);
		}
		TextView messageTv = (TextView) dialog.findViewById(R.id.message_tv);
		messageTv.setText(msgResId);
		Button cancelbtn = (Button) dialog.findViewById(R.id.cancel_btn);
		cancelbtn.setTag(dialog);
		cancelbtn.setOnClickListener(cancelListener);

		Button okBtn = (Button) dialog.findViewById(R.id.ok_btn);
		okBtn.setTag(dialog);
		okBtn.setOnClickListener(okListener);

		dialog.show();
		return dialog;
	}
	public static void showOkListenerDialog(final Context context, String title, String message, boolean isBlue, OnClickListener okListener) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		dialog.setContentView(R.layout.ok_dialog);
		dialog.setCanceledOnTouchOutside(false);
		TextView titleTv = (TextView) dialog.findViewById(R.id.title_tv);
		titleTv.setText(title);

		TextView privacyTv = (TextView) dialog.findViewById(R.id.message_tv_privacy);
		privacyTv.setVisibility(View.GONE);
		TextView termsTv = (TextView) dialog.findViewById(R.id.message_tv_terms);
		termsTv.setVisibility(View.GONE);

		TextView messageTv = (TextView) dialog.findViewById(R.id.message_tv);
		messageTv.setText(message);
		if(isBlue){
			messageTv.setTextColor(context.getResources().getColor(R.color.terms_text_blue));
			messageTv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					try {

						Intent intent = new Intent(context, TermsActivity.class);
						context.startActivity(intent);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
		}


		Button okBtn = (Button) dialog.findViewById(R.id.ok_btn);
		okBtn.setTag(dialog);
		okBtn.setOnClickListener(okListener);

		dialog.show();
	}


	public static void showOkListenerDialog(final Context context, String title, String message1,String message, boolean isBlue, OnClickListener okListener) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		dialog.setContentView(R.layout.ok_dialog);
		dialog.setCanceledOnTouchOutside(false);
		TextView titleTv = (TextView) dialog.findViewById(R.id.title_tv);
		titleTv.setText(title);
		//titleTv.setVisibility(View.GONE);
		//titleTv.setVisibility(View.GONE);

		TextView privacyTv = (TextView) dialog.findViewById(R.id.message_tv_terms);
		privacyTv.setText(message);
		if(isBlue){
			privacyTv.setTextColor(context.getResources().getColor(R.color.terms_text_blue));
			privacyTv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						Intent intent = new Intent( context, TermsActivity.class);
						intent.putExtra("isTrems", false);
						context.startActivity(intent);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
		}

		TextView termsTv = (TextView) dialog.findViewById(R.id.message_tv_privacy);
		termsTv.setText(message1);
		if(isBlue){
			termsTv.setTextColor(context.getResources().getColor(R.color.terms_text_blue));
			termsTv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						Intent intent = new Intent(context, TermsActivity.class);
						intent.putExtra("isTrems", true);
						context.startActivity(intent);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
		}


		Button okBtn = (Button) dialog.findViewById(R.id.ok_btn);
		okBtn.setTag(dialog);
		okBtn.setOnClickListener(okListener);

		dialog.show();
	}

}
