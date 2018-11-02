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
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class FollowUpGetActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.newlist);
        runGetAccountData();
    }

    void runGetAccountData() {
        new Thread(new Runnable() {
            public void run() {
                getTodayFollowUpData();
            }
        }).start();
    }


    private void getTodayFollowUpData() {
        OAuthTokens myTokens = StaticMemory.getInstance().getAccessTokens();

        DefaultHttpClient client = new DefaultHttpClient();
        String url = myTokens.get_instance_url() + "/services/data/v20.0/query/?q=";
        String soqlQuery = "Select Id, Subject, AccountId, ActivityDate from Task WHERE ActivityDate = TODAY";

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

            try {
                for (int t = 0; t < records.length(); t++)
                {
                    try {
                        JSONObject record = (JSONObject) records.get(t);
                        String subject = record.getString("Subject");
                        String accountId = record.getString("AccountId");

                        JSONObject accountResult = getAccountDataByID(accountId);
                        JSONArray accountRecords = accountResult.getJSONArray("records");

                        for (int a = 0; a < records.length(); a++)
                        {
                            try {
                                JSONObject accountRecord = (JSONObject) accountRecords.get(t);
                                String accountName = accountRecord.getString("Name");

                                JSONObject contactsNode = accountRecord.getJSONObject("Contacts");
                                JSONArray contactsArray = contactsNode.getJSONArray("records");
                                if (contactsArray == null) continue;

                                for (int c = 0; c < contactsArray.length(); c++)
                                {
                                    try {
                                        JSONObject contact = (JSONObject) contactsArray.get(c);
                                        String contactName = contact.getString("Name");
                                        String contactPhone = contact.getString("Phone");

                                        addNewContact(StaticMemory.getInstance().contactClassList, String.format(getString(R.string.follow_up_contact_format), contactName, accountName, subject), contactPhone);
                                    } catch (Exception e) {
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) {
            }
        } catch (final Exception e) {
            final String jsonFinal = json;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Clipboard clipboard = new Clipboard();
                    clipboard.copyToClipboard(FollowUpGetActivity.this, jsonFinal);
                    Toast.makeText(FollowUpGetActivity.this, getString(R.string.crm_salesforce_failed_get_accounts) + e.toString(), Toast.LENGTH_LONG).show();
                }
            });

            e.printStackTrace();
            finishError();
            return;
        }

        finishOk();
    }

    private JSONObject getAccountDataByID(String accountID) throws IOException, JSONException {
        OAuthTokens myTokens = StaticMemory.getInstance().getAccessTokens();

        DefaultHttpClient client = new DefaultHttpClient();
        String url = myTokens.get_instance_url() + "/services/data/v20.0/query/?q=";
        String soqlQuery = "Select Id, Name, (select Id, Name, Phone from Contacts) from Account WHERE id = '" + accountID + "'";

        url += URLEncoder.encode(soqlQuery, "UTF-8");

        HttpGet getRequest = new HttpGet(url);
        getRequest.addHeader("Authorization", "OAuth " + myTokens.get_access_token());

        String json = null;

        HttpResponse response = client.execute(getRequest);
        String result = EntityUtils.toString(response.getEntity());
        JSONObject object = (JSONObject) new JSONTokener(result).nextValue();

        return object;
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
