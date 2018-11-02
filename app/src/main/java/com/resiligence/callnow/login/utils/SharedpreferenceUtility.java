package com.resiligence.callnow.login.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
/**
 * SharedpreferenceUtility.java - a class for demonstrating to store value in Database.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 15/12/15.
 */

public class SharedpreferenceUtility {

	private static SharedPreferences mPref;
	private Editor mEditor;
	private static SharedpreferenceUtility mRef;
	/**Singleton method return the instance**/
	public static SharedpreferenceUtility getInstance(Context context) {
		if (mRef == null) {
			mRef = new SharedpreferenceUtility();
			mPref = context.getApplicationContext().getSharedPreferences(
					"MyPref", 0);
			return mRef;
		}
		return mRef;
	}
	/**Put long value into sharedpreference**/
	public void putLong(String key, long value) {
		try{
		mEditor = mPref.edit();
		mEditor.putLong(key, value);
		mEditor.commit();
	} catch (Exception e) {
		e.printStackTrace();
	}
	}
	/**Get long value from sharedpreference**/
	public long getLong(String key) {
		try{
		long lvalue;
		lvalue=mPref.getLong(key, 0);
		return lvalue;
	} catch (Exception e) {
		e.printStackTrace();
		return 0;
	}
	}
	/**Put int value into sharedpreference**/
	public void putInt(String key, int value) {
		try{
		mEditor = mPref.edit();
		mEditor.putInt(key, value);
		mEditor.commit();
	} catch (Exception e) {
		e.printStackTrace();
	}
	}
	/**Get int value from sharedpreference**/
	public int getInt(String key) {
		try{
		int lvalue;
		lvalue=mPref.getInt(key, 0);
		return lvalue;
	} catch (Exception e) {
		e.printStackTrace();
		return 0;
	}
	}

	/**Put String value into sharedpreference**/
	public void putString(String key, String value) {
		try{
		mEditor = mPref.edit();
		mEditor.putString(key, value);
		mEditor.commit();
	} catch (Exception e) {
		e.printStackTrace();
	}
	}
	/**Get String value from sharedpreference**/
	public String getString(String key) {
		try{
		String lvalue;
		lvalue=mPref.getString(key, "");
		return lvalue;
	} catch (Exception e) {
		e.printStackTrace();
		return "";
	}
	}
	/**Put String value into sharedpreference**/
	public void putBoolean(String key, Boolean value) {
		try {
		mEditor = mPref.edit();
		mEditor.putBoolean(key, value);
		mEditor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**Get String value from sharedpreference**/
	public Boolean getBoolean(String key) {
		try{
		Boolean lvalue;
		lvalue=mPref.getBoolean(key, false);
		return lvalue;
	} catch (Exception e) {
		e.printStackTrace();
		return false;
	}
	}
}
