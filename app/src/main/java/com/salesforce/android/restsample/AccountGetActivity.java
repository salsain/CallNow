package com.salesforce.android.restsample;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.common.utility.Clipboard;
import com.cyril_rayan.callnow.ContactInfo;
import com.cyril_rayan.callnow.R;
import com.cyril_rayan.callnow.StaticMemory;

public class AccountGetActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.newlist);
        runGetAccountData();
    }

    void runGetAccountData() {
        new Thread(new Runnable() {
            public void run() {
                getAccountData();
            }
        }).start();
    }


    private void getAccountData() {
        OAuthTokens myTokens = StaticMemory.getInstance().getAccessTokens();

        DefaultHttpClient client = new DefaultHttpClient();
        String url = myTokens.get_instance_url() + "/services/data/v20.0/query/?q=";
        //String soqlQuery = "Select Id, Name, BillingStreet, BillingCity, BillingState From Account limit 20";//original
        //String soqlQuery = "Select Id, Name, Phone From Account limit 100";
        String soqlQuery = "Select Id, Name, (select Id, Name, Phone from Contacts) from Account limit 100";

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
                    String accountName = record.getString("Name");

                    if (!record.has("Contacts") || record.get("Contacts") == null) continue;

                    JSONObject contactsNode = record.getJSONObject("Contacts");

                    if (!contactsNode.has("records") || contactsNode.get("records") == null)
                        continue;

                    JSONArray contactsArray = contactsNode.getJSONArray("records");
                    if (contactsArray == null) continue;

                    for (int j = 0; j < contactsArray.length(); j++)
                    {
                        try
                        {
                            JSONObject contact = (JSONObject) contactsArray.get(j);
                            String contactName = contact.getString("Name");
                            String contactPhone = contact.getString("Phone");

                            addNewContact(StaticMemory.getInstance().contactClassList, contactName + "(" + accountName + ")", contactPhone);
                        } catch (Exception e) {
                        }
                    }
                } catch (Exception e) {
                }
            }
            Collections.sort(StaticMemory.getInstance().contactClassList);

        } catch (final Exception e) {
            final String jsonFinal = json;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Clipboard clipboard = new Clipboard();
                    clipboard.copyToClipboard(AccountGetActivity.this, jsonFinal);
                    Toast.makeText(AccountGetActivity.this, getString(R.string.crm_salesforce_failed_get_accounts) + e.toString(), Toast.LENGTH_LONG).show();
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
