package com.resiligence.callnow.login.webservicedetails;

import static com.resiligence.callnow.login.webservicedetails.WebServices.ForgetPassword_Url;
import static com.resiligence.callnow.login.webservicedetails.WebServices.GET_CITY;
import static com.resiligence.callnow.login.webservicedetails.WebServices.GET_COUNTRY;
import static com.resiligence.callnow.login.webservicedetails.WebServices.Login_Url;
import static com.resiligence.callnow.login.webservicedetails.WebServices.SignUp_Url;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.resiligence.callnow.login.utils.GlobalVariables;
import com.resiligence.callnow.login.utils.WhiteProgressDialog;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * APIService.java - a class for call WB and return Response.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @see AsyncTask<RestClient, Integer, String>
 * @since 24/12/15.
 */
public class APIService extends AsyncTask<RestClient, Integer, String> {

	public interface APIServiceListener {
		public abstract void onCompletion(APIService service, Object data,
										  Error error);
	}

	Context context;
	public WhiteProgressDialog dialog;
	public boolean mShowDialog = true;
	public int tag;

	RestClient.RequestMethod methode;
	RestClient client;
	APIServiceListener serviceListener;
	public void setServiceListener(APIServiceListener serviceListener) {
		this.serviceListener = serviceListener;
	}
	public APIService(Context context, RestClient client, RestClient.RequestMethod methode) {
		this.methode = methode;
		this.context = context;
		if (client != null) {
			this.client = client;
		}
	}

	@Override
	protected void onPreExecute() {
		if (mShowDialog == true) {
			dialog = new WhiteProgressDialog(context, 0);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}
	}

	@Override
	protected String doInBackground(RestClient... params) {
		if (params.length > 0) {
			RestClient client = params[0];
			try {
				client.Execute(methode);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
		String response = this.client.getResponse();
		Log.d("APIService", "Request: " + this.client.getUrl());
		if (response != null) {
			Log.d("APIService", "Response: " + response);
		} else {
			Log.d("APIService", "Response: null");
		}
		Error error = null;
		String errorMessage = null;
		Object data = null;
		int responseCode = this.client.getResponseCode();
		if (response != null) {

			try {
				JSONObject json = new JSONObject(response);
				boolean success = false;
				if(json !=null){
					if (!json.isNull("data")) {
						data = json.optJSONArray("data");
						if (data == null) {
							data = json.optJSONObject("data");
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}
		//this.onCompletion(data, error, null);
		if (this.serviceListener != null) {
			this.serviceListener.onCompletion(this, data, error);
		}
	}
	public void getData(boolean isForeground) {
		mShowDialog = isForeground;
		if (GlobalVariables.checkInternetConnection(context))
			this.execute(this.client);
		else
			Toast.makeText(context, "No internet connection found", Toast.LENGTH_SHORT).show();
	}
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
		return isConnected;
	}
	/**.....///......APIS TO USE.........///...
	 * @param mPassStr */
	public static APIService loginRequest(Context context, String regId,String email, String mPassStr) {
		String url = Login_Url;
		RestClient client = new RestClient(url);
		client.AddParam("deviceToken", regId);
		client.AddParam("email", email);
		client.AddParam("password", mPassStr);
		APIService service = new APIService(context, client,RestClient.RequestMethod.POST);
		return service;
	}

	public static APIService signUpRequest(Context context, String regId,String email, String mPassStr) {
		String url = SignUp_Url;
		RestClient client = new RestClient(url);
		client.AddParam("deviceToken", regId);
		client.AddParam("email", email);
		client.AddParam("password", mPassStr);
		APIService service = new APIService(context, client,RestClient.RequestMethod.POST);
		return service;
	}

	public static APIService forgetPassRequest(Context context, String email) {
		String url = ForgetPassword_Url;
		RestClient client = new RestClient(url);
		client.AddParam("email", email);
		APIService service = new APIService(context, client,RestClient.RequestMethod.POST);
		return service;
	}
	public static APIService getCountries(Context context) {
		String url = GET_COUNTRY;
		RestClient client = new RestClient(url);
		APIService service = new APIService(context, client,RestClient.RequestMethod.GET);
		return service;
	}
	public static APIService getCity(Context context, String countryName) {
		String url = GET_CITY;
		RestClient client = new RestClient(url);
		client.AddParam("countryName", countryName);
		APIService service = new APIService(context, client,RestClient.RequestMethod.GET);
		return service;
	}

}
















