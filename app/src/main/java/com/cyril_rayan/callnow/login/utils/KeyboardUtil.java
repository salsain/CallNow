package com.cyril_rayan.callnow.login.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
/**
 * DialogUtil.java - a class for show or Hide Keyborad
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 22/12/15.
 */
public class KeyboardUtil {
	/**
	 * Hide opened soft keyboard
	 * */
	public static void hideSoftKeyboard(View view) {
		if (null != view) {
			InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(
					Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	/**
	 * Show opened soft keyboard
	 * */
	public static void showSoftKeyboard(EditText editText) {
		if (null != editText) {
			InputMethodManager inputMethodManager = (InputMethodManager) editText.getContext().getSystemService(
					Context.INPUT_METHOD_SERVICE);
			inputMethodManager.showSoftInput(editText, 0);
		}
	}
}
