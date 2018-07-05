package com.cyril_rayan.callnow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class SelectContactsActivity extends AppCompatActivity {
    public static final java.lang.String USE_SYSTEM_CONTACTS = "USE_SYSTEM_CONTACTS";
    //config
    final boolean useLimit = false;
    final int contactsLimit = 10;
    final boolean addContactsWithoutPhones = false;

    final int freeSelectContactsCount = 5;

    private ListView mListView;
    ArrayAdapter<String> adapter;
    Cursor cursor;
    int counter;
    ArrayList<String> contactStringsList;
    private Handler updateBarHandler;
    private ProgressDialog pDialog;

    RelativeLayout listLayout;
    Button cancelSelect;
    Button confirmSelect;
    Button prev;
    Button next;

    EditText search_text;
    ImageButton search_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contacts);

        mListView = (ListView) findViewById(R.id.list);
        updateBarHandler = new Handler();

        // Set onclicklistener to the list item.
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                StaticMemory.getInstance().contactClassList.get(position).cheched = !StaticMemory.getInstance().contactClassList.get(position).cheched;
                adapter.notifyDataSetChanged();
            }
        });

        listLayout = (RelativeLayout) findViewById(R.id.listLayout);

        prev = (Button) findViewById(R.id.prev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPrevInContacts();
            }
        });
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextInContacts();
            }
        });

        cancelSelect = (Button) findViewById(R.id.cancelButton);
        cancelSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        confirmSelect = (Button) findViewById(R.id.confirmSelection);
        confirmSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticMemory.getInstance().selectedContactClassList = new ArrayList<ContactInfo>();

                for (ContactInfo item : StaticMemory.getInstance().contactClassList){
                    if (item.cheched)
                        StaticMemory.getInstance().selectedContactClassList.add(item);
                }

                setResult(RESULT_OK);
                finish();
            }
        });

        Bundle b = getIntent().getExtras();
        boolean useSystemContacts = b.getBoolean(USE_SYSTEM_CONTACTS);

        search_text = (EditText) findViewById(R.id.search_text);
        search_text.setVisibility(View.GONE);
        search_button = (ImageButton) findViewById(R.id.search_button);
        search_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search_text.getVisibility() == View.GONE) {
                    search_text.setVisibility(View.VISIBLE);
                    search_button.setImageDrawable(getResources().getDrawable(
                        R.drawable.zm_tip_right_arrow));
                } else {
                    search_text.setVisibility(View.GONE);
                    search_button.setImageDrawable(getResources().getDrawable(
                        R.drawable.zm_ic_search));
                    search_text.setText("");
                }
            }
        });

        search_text.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                performSearch(text);
            }
        });

        if (useSystemContacts)
            initListFromOSContacts();
        else
            runUserSelectContacts();
    }

    void runGetContacts(final Uri CONTENT_URI){
        runGetContacts(CONTENT_URI, null);
    }

    void initListFromOSContacts(){
        runGetContacts(ContactsContract.Contacts.CONTENT_URI, new Runnable() {
            @Override
            public void run() {
                runUserSelectContacts();
            }
        });
    }

    void performSearch(String text) {
        if (text.length() > 1) {
            contactsAdapter.setFilter(text);
            contactsAdapter.notifyDataSetChanged();
        } else {
            contactsAdapter.setFilter(null);
            contactsAdapter.notifyDataSetChanged();
        }
    }

    void runUserSelectContacts(){
        runFillContactsListView();
    }

    void runGetContacts(final Uri CONTENT_URI, final Runnable runnable){

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Reading contacts...");
        pDialog.setCancelable(false);
        pDialog.show();

        // Since reading contacts takes more time, let's run it on a separate thread.
        new Thread(new Runnable() {
            @Override
            public void run() {
                getContacts(CONTENT_URI);
                if (runnable != null)
                    runnable.run();
            }
        }).start();
    }

    public void getContacts(Uri CONTENT_URI) {
        contactStringsList = new ArrayList<String>();
        StaticMemory.getInstance().contactClassList = new ArrayList<ContactInfo>();

        //Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;
        StringBuffer output;
        ContentResolver contentResolver = getContentResolver();

        cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

        // Iterate every contact in the phone
        if (cursor.getCount() > 0) {
            counter = 0;
            int currentNumbers = 0;

            while (cursor.moveToNext()) {

                if (useLimit) {
                    currentNumbers++;
                    if (currentNumbers > contactsLimit)
                        break;
                }

                output = new StringBuffer();
                // Update the progress message
                updateBarHandler.post(new Runnable() {
                    public void run() {
                        pDialog.setMessage(getString(R.string.reading_contacts_from_os) + counter++ +"/"+cursor.getCount());
                    }
                });
                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));

                ArrayList<String> phoneNumberArray = null;
                String email = null;

                boolean useMultiPhone = true;
                boolean phoneFinded = false;

                if (useMultiPhone) {
                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                    if (hasPhoneNumber > 0) {
                        output.append("\n First Name:" + name);
                        //This is to read multiple phone numbers associated with the same contact
                        Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                        while (phoneCursor.moveToNext()) {
                            if (phoneNumberArray == null)
                                phoneNumberArray = new ArrayList<>();

                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                            phoneNumberArray.add(phoneNumber);
                            phoneFinded = true;
                            output.append("\n Phone number:" + phoneNumber);
                        }
                        phoneCursor.close();
                        // Read every email id associated with the contact
                        Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);
                        while (emailCursor.moveToNext()) {
                            email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                            output.append("\n Email:" + email);
                        }
                        emailCursor.close();
                    }
                }

                if (!phoneFinded) {

                    int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                    if (column != -1) {
                        if (phoneNumberArray == null)
                            phoneNumberArray = new ArrayList<>();

                        String phoneNumber = cursor.getString(column);
                        phoneNumberArray.add(phoneNumber);
                    }
                }

                if (phoneNumberArray != null) {
                    StaticMemory.getInstance().contactClassList.add(new ContactInfo(name, phoneNumberArray));
                    contactStringsList.add(output.toString());
                } else if (addContactsWithoutPhones) {
                    if (phoneNumberArray == null)
                        phoneNumberArray = new ArrayList<>();
                    StaticMemory.getInstance().contactClassList.add(new ContactInfo(name, phoneNumberArray));
                    contactStringsList.add(output.toString());
                }
            }

            Collections.sort(StaticMemory.getInstance().contactClassList);

            // Dismiss the progressbar after 500 millisecondds
            updateBarHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pDialog.cancel();
                }
            }, 500);
        }
    }

    CustomAdapter contactsAdapter;

    void runFillContactsListView() {
        // ListView has to be updated using a ui thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_checked, contactStringsList);
                contactsAdapter = new CustomAdapter(SelectContactsActivity.this);
                mListView.setAdapter(contactsAdapter);
            }
        });
    }


    void onPrevInContacts() {
        mListView.getHeight();
        mListView.smoothScrollBy(-mListView.getMeasuredHeight(), 1000);// scrollBy(0, - mListView.getHeight());
    }

    void onNextInContacts() {
        mListView.getHeight();
        mListView.smoothScrollBy(mListView.getMeasuredHeight(), 1000);
    }

    public class CustomAdapter extends ArrayAdapter{
        Context context;
        String filterText;

        public void setFilter(String filterText) {
            this.filterText = filterText;
        }

        public CustomAdapter(Context context) {
            super(context, R.layout.contact_list_item);
            this.context = context;
        }

        @Override
        public int getCount() {
            return StaticMemory.getInstance().contactClassList.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.contact_list_item, parent, false);
            LinearLayout content = (LinearLayout) convertView.findViewById(R.id.content);
            TextView name = (TextView) convertView.findViewById(R.id.textView1);
            final CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);

            final ContactInfo itemData = StaticMemory.getInstance().contactClassList.get(position);

            if (filterText != null && !itemData.name.toLowerCase().contains(filterText.toLowerCase())) {
                content.setVisibility(View.GONE);
                return convertView;
            } else {
                content.setVisibility(View.VISIBLE);
            }

            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked == true) {
                        if (countOfSelectedIsOver()) {
                            buttonView.setChecked(!isChecked);
                            showToastLimited();
                            return;
                        }
                    }

                    itemData.cheched = isChecked;
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!cb.isChecked()) {
                        if (countOfSelectedIsOver()) {
                            showToastLimited();
                            return;
                        }
                    }

                    cb.setChecked(!cb.isChecked());
                    itemData.cheched = cb.isChecked();
                }
            });

            name.setText(itemData.name);
            if (itemData.cheched)
                cb.setChecked(true);
            else
                cb.setChecked(false);

            if (itemData.phones == null || itemData.phones.isEmpty())
                cb.setEnabled(false);
            else
                cb.setEnabled(true);

            return convertView;
        }
    }

    void showToastLimited() {
        Toast.makeText(SelectContactsActivity.this, String.format(getString(R.string.select_contacts_is_limited), freeSelectContactsCount), Toast.LENGTH_LONG).show();
    }

    boolean countOfSelectedIsOver(){
        if (StaticMemory.getInstance().enableMore5CallsFeature)
            return false;

        int count = 0;
        for (ContactInfo contact : StaticMemory.getInstance().contactClassList){
            if (contact.cheched)
                count++;

            if (count >= freeSelectContactsCount)
                return true;
        }
        return false;
    }
}
