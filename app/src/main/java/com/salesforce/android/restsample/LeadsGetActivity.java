package com.salesforce.android.restsample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.common.utility.Clipboard;
import com.resiligence.callnow.ContactInfo;
import com.resiligence.callnow.R;
import com.resiligence.callnow.StaticMemory;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class LeadsGetActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.newlist);
        runGetAccountData();
    }

    void runGetAccountData() {
        new Thread(new Runnable() {
            public void run() {
                getLeadsData();
            }
        }).start();
    }


    private void getLeadsData() {
        OAuthTokens myTokens = StaticMemory.getInstance().getAccessTokens();

        DefaultHttpClient client = new DefaultHttpClient();
        String url = myTokens.get_instance_url() + "/services/data/v20.0/query/?q=";

        String soqlQuery = "Select Id, Name, Phone, Company, status from Lead";

        try {
            url += URLEncoder.encode(soqlQuery, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }

        HttpGet getRequest = new HttpGet(url);
        getRequest.addHeader("Authorization", "OAuth " + myTokens.get_access_token());

        String json = null;
        try {
            HttpResponse response = client.execute(getRequest);
            String result = EntityUtils.toString(response.getEntity());
            json = result;
            JSONObject object = (JSONObject) new JSONTokener(result).nextValue();
            JSONArray records = object.getJSONArray("records");

            StaticMemory.getInstance().contactClassList = new ArrayList<ContactInfo>();

            for (int i = 0; i < records.length(); i++) {
                try {
                    JSONObject record = (JSONObject) records.get(i);
                    String leadName = record.getString("Name");
                    String leadPhone = record.getString("Phone");
                    String leadCompany = record.getString("Company");

                    addNewContact(StaticMemory.getInstance().contactClassList, leadName + "(" + leadCompany + ")", leadPhone);
                } catch (Exception e) {
                }
            }

        } catch (final Exception e) {
            final String jsonFinal = json;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Clipboard clipboard = new Clipboard();
                    clipboard.copyToClipboard(LeadsGetActivity.this, jsonFinal);
                    Toast.makeText(LeadsGetActivity.this, getString(R.string.crm_salesforce_failed_get_leads) + e.toString(), Toast.LENGTH_LONG).show();
                }
            });

            e.printStackTrace();
            finishError();
            return;
        }

        finishOk();
    }

    void finishOk() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    void finishError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    void addNewContact(ArrayList<ContactInfo> arrayList, String contactName, String contactPhone) {
        ArrayList<String> phoneNumberArray = new ArrayList<>();
        phoneNumberArray.add(contactPhone);
        arrayList.add(new ContactInfo(contactName, phoneNumberArray));
    }
}
