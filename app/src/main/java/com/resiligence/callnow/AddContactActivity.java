package com.resiligence.callnow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddContactActivity extends AppCompatActivity {

    Button saveContactButton;
    Button saveContactAndNextButton;
    EditText phoneNumberEditText;
    EditText contactNameEditText;
    TextView noteTextView;

    static final String prefixForNoName = "NoName";
    int curretIndexForNoName = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        saveContactButton = (Button) findViewById(R.id.saveContactButton);
        saveContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContact();
            }
        });

        saveContactAndNextButton = (Button) findViewById(R.id.saveContactAndNextButton);
        saveContactAndNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContactAndNext();
            }
        });

        phoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEditText);
        phoneNumberEditText.addTextChangedListener(textChangeListener);
        contactNameEditText = (EditText) findViewById(R.id.contactNameEditText);
        contactNameEditText.addTextChangedListener(textChangeListener);

        noteTextView = (TextView) findViewById(R.id.noteTextView);

    }

    private void saveContact() {
        performSaveContact(false);
        setResult(RESULT_OK);
        finish();
    }

    private void saveContactAndNext() {
        performSaveContact(true);
        clearForms();
    }

    private void clearForms() {
        contactNameEditText.setText("");
        phoneNumberEditText.setText("");
        updateAvailable();
    }

    void performSaveContact(boolean showToast){
        String contactName = contactNameEditText.getText().toString();
        String contactPhone = phoneNumberEditText.getText().toString();

        if (contactName == null || contactName.isEmpty()) {
            contactName = prefixForNoName + (curretIndexForNoName++);
        }

        ArrayList<String> phoneNumberArray = new ArrayList<>();
        phoneNumberArray.add(contactPhone);

        if (StaticMemory.getInstance().selectedContactClassList == null)
            StaticMemory.getInstance().selectedContactClassList = new ArrayList<>();

        StaticMemory.getInstance().selectedContactClassList.add(new ContactInfo(contactName, phoneNumberArray));

        if (showToast)
            Toast.makeText(this, getString(R.string.new_contact_ready_for_call) + contactName + "("+ contactPhone +")", Toast.LENGTH_LONG).show();
    }

    void updateAvailable() {
        String contactName = contactNameEditText.getText().toString();
        String contactPhone = phoneNumberEditText.getText().toString();

        if (contactPhone == null || contactPhone.isEmpty()) {
            noteTextView.setText(getString(R.string.enter_phone_num_warning));
            saveContactAndNextButton.setEnabled(false);
            saveContactButton.setEnabled(false);
            return;
        } else if (!Patterns.PHONE.matcher(contactPhone).matches()) {
            noteTextView.setText(getString(R.string.enter_valid_phone_warning));
            saveContactAndNextButton.setEnabled(false);
            saveContactButton.setEnabled(false);
        } else {
            noteTextView.setText("");
            saveContactAndNextButton.setEnabled(true);
            saveContactButton.setEnabled(true);

            if (contactName == null || contactName.isEmpty()) {
                noteTextView.setText(getString(R.string.note_name_will_be) + prefixForNoName + curretIndexForNoName +"\"");
            }
        }
    }


    TextWatcher textChangeListener = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateAvailable();
        }
    };
}
