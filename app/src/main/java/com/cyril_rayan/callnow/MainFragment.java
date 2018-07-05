package com.cyril_rayan.callnow;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;

import com.common.utility.PermissionCheckService;
import com.common.utility.ViaAdActivityRunner;

import ai.kitt.snowboy.AppResCopy;
import ai.kitt.snowboy.audio.RecordingThread;
import ai.kitt.snowboy.MsgEnum;

public class MainFragment extends Fragment {

    View runUserSelectContactsButton;
    View runManualAddContactsButton;
    View runImportCRMContactsButton;

    View makeCallsButton;
    View autoConnectToZoom;
    TextView makeCallsText;

    //calling
    static int callContactCurrentIndex = 0;

    static boolean sequenceCalling = false;
    static int callContactSequenceIndex = 0;
    static final int callContactSequenceCount = 5;

    public static final int REQUEST_READ_CONTACTS_PERMISIONS = 10234;
    public static final int REQUEST_CODE_PICK_CONTACT = 10235;
    public static final int REQUEST_MANUAL_ADD_CONTACTS = 10236;
    public static final int REQUEST_IMPORT_SALESFORCE_CONTACTS = 10237;
    public static final int REQUEST_SELECT_CONTACTS = 10238;
    public static final int REQUEST_AUTO_CONNECT_TO_ZOOM = 10239;
    public static final int REQUEST_VOICE_RECOGNITION_CODE = 1240;

    PermissionCheckService mPermissionCheckService;

    private RecordingThread recordingThread;

    private static long activeTimes = 0;

    public Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            MsgEnum message = MsgEnum.getMsgEnum(msg.what);
            switch(message) {
                case MSG_ACTIVE:
                    activeTimes++;
//                    updateLog(" ===> Detected " + activeTimes + " times", "white");
//                        Toast.makeText(getActivity(), "Active "+activeTimes, Toast.LENGTH_SHORT).show();
//                    showToast("KeyWord "+activeTimes);
                    forceEndCall();
                    Log.e("endCallByVoice", "endCallByVoice");
                    break;
                case MSG_INFO:
//                    updateLog(" ----> "+message);
                    break;
                case MSG_VAD_SPEECH:
//                    updateLog(" ----> normal voice", "blue");
                    break;
                case MSG_VAD_NOSPEECH:
//                    updateLog(" ----> no speech", "blue");
                    break;
                case MSG_ERROR:
//                    updateLog(" ----> " + msg.toString(), "red");
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };


    void resetSequence() {
        sequenceCalling = false;
        callContactSequenceIndex = 0;
    }


    void showToast(CharSequence msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    String logString = "";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        runUserSelectContactsButton = getView().findViewById(R.id.runUserSelectContactsButton);
        runUserSelectContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runUserSelectContacts(true);
            }
        });

        makeCallsButton =  getView().findViewById(R.id.makeCallsButton);
        makeCallsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //displayGoogleDialogSpeechRecognizer();
                //showDialogAboutVoiceRecognision();
                logString = "";
                runSequenceCallingFromContactList();
            }
        });

        makeCallsText = (TextView) getView().findViewById(R.id.makeCallsText);


        autoConnectToZoom =  getView().findViewById(R.id.autoConnectToZoom);
        autoConnectToZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runAutoConnectToZoom();
            }
        });


        runManualAddContactsButton = getView().findViewById(R.id.runManualAddContactsButton);
        runManualAddContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runManualAddContacts();
            }
        });

        runImportCRMContactsButton = getView().findViewById(R.id.runImportCRMContactsButton);
        runImportCRMContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runImportCRMContacts();
            }
        });

        requestForSpecificPermission();

        addCallListener();

        AppResCopy.copyResFromAssetsToSD(getContext());

        recordingThread = new RecordingThread(handle, null);//new AudioDataSaver()

    }

    void runNextPhoneFromContactList() {
        if (StaticMemory.getInstance().selectedContactClassList == null)
            return;

        resetSequence();

        if (StaticMemory.getInstance().selectedContactClassList.size() <= callContactCurrentIndex) {
            Toast.makeText(getActivity(), getString(R.string.contacts_list_ended), Toast.LENGTH_LONG).show();
            return;
        }

        ContactInfo currentContact = StaticMemory.getInstance().selectedContactClassList.get(callContactCurrentIndex);
        callContactCurrentIndex++;

        callToContact(currentContact);
    }

    String getContactsListStringForMakeCalls() {
        if (StaticMemory.getInstance().selectedContactClassList == null)
            return null;

        String phonesListString = null;

        for (int i = 0; i < callContactSequenceCount; ++i){
            int indexInArray = callContactCurrentIndex + i;
            if (StaticMemory.getInstance().selectedContactClassList.size() > indexInArray) {
                ContactInfo contact = StaticMemory.getInstance().selectedContactClassList.get(indexInArray);
                if (phonesListString == null) phonesListString = "";
                phonesListString += contact.name + "\n";
            } else {
                break;
            }
        }

        if (phonesListString != null)
            return getString(R.string.we_call_now_to_next_contacts) + phonesListString;
        else
            return null;
    }

    void callToContact(ContactInfo contact) {
        String phoneNumString = contact.phones.get(0);
        String uri = getString(R.string.tel) + phoneNumString.trim();
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(uri));

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), getString(R.string.have_no_activity_for_call), Toast.LENGTH_SHORT).show();
        }
    }

    private TelephonyManager tm;

    private void addCallListener()
    {
        tm = (TelephonyManager) getActivity().getSystemService(Activity.TELEPHONY_SERVICE);
        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE | PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR );
    }

    PhoneCallListener phoneStateListener = new PhoneCallListener();

    private class PhoneCallListener extends PhoneStateListener {

        private boolean isPhoneCalling = false;
        String LOG_TAG = "PhoneCallListener";

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (TelephonyManager.CALL_STATE_RINGING == state) {
                // phone ringing
                Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
            }

            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                // active
                Log.i(LOG_TAG, "OFFHOOK");
                isPhoneCalling = true;
            }

            if (TelephonyManager.CALL_STATE_IDLE == state) {
                // run when class initial and phone call ended, need detect flag
                // from CALL_STATE_OFFHOOK
                Log.i(LOG_TAG, "IDLE");

                if (isPhoneCalling) {
                    Log.i(LOG_TAG, "restart app");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onCallEnded();
                        }
                    }, 200);
                    isPhoneCalling = false;
                }
            }
        }

        @Override
        public void onMessageWaitingIndicatorChanged(boolean mwi) {
            if (mwi) {
                //we call to voicemail it's not success call
                Log.d(LOG_TAG, "onMessageWaitingIndicatorChanged");
                forceEndCall();
            }
        }
    }

    boolean forceEndCall(){
        try {
            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager =
                    (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

            // Get the getITelephony() method
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);

            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

            // Get the endCall method from ITelephony
            Class telephonyInterfaceClass =
                    Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);

        } catch (Exception ex) { // Many things can go wrong with reflection calls
            //Log.d(TAG, "PhoneStateReceiver **" + ex.toString());
            logString += "\nforceEndCall Error:" + ex.toString();
            return false;
        }
        return true;
    }

    private void onCallEnded() {
        //next call if we use sequenceCalling
        if (sequenceCalling) {
            callContactSequenceIndex++;

            if (callContactSequenceIndex < callContactSequenceCount && StaticMemory.getInstance().selectedContactClassList.size() > callContactCurrentIndex) {
                ContactInfo currentContact = StaticMemory.getInstance().selectedContactClassList.get(callContactCurrentIndex);
                callContactCurrentIndex++;

                callToContact(currentContact);
            } else {
                resetSequence();
                if (recordingThread != null) recordingThread.stopRecording();
                Toast.makeText(getActivity(), getString(R.string.call_sequence_ended), Toast.LENGTH_LONG);
                if (AppConfig.showNativeVoiceResults)
                    showDialogAboutVoiceRecognision();
            }
        }
    }

    void runSequenceCallingFromContactList() {
        String listForCallString = getContactsListStringForMakeCalls();
        if (listForCallString == null) {
            Toast.makeText(getActivity(), getString(R.string.empty_call_list_warning), Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.make_calls_cofiration_title))
                .setMessage(listForCallString)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.make_calls_cofiration_positive_button),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                performSequenceCallingFromContactList();
                                if (AppConfig.useNativeGoogleVoiceRecognision)
                                    runSpeechRecognizer();
                            }
                        })
                .setNegativeButton(getString(R.string.cancel), null);

        AlertDialog alert = builder.create();
        alert.show();
    }

    void showDialogAboutVoiceRecognision() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Voice Recognision Log list")
                .setMessage("Please screenshot it \n \n" + logString)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.make_calls_cofiration_positive_button),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                runSpeechRecognizer();
                            }
                        })
                .setNegativeButton(getString(R.string.cancel), null);

        AlertDialog alert = builder.create();
        alert.show();
    }

    void performSequenceCallingFromContactList() {
        if (StaticMemory.getInstance().selectedContactClassList == null){
            if (recordingThread != null) recordingThread.stopRecording();
            return;
        }


        resetSequence();

        if (recordingThread != null) recordingThread.startRecording();
        ContactInfo currentContact = StaticMemory.getInstance().selectedContactClassList.get(callContactCurrentIndex);
        callContactCurrentIndex++;
        callToContact(currentContact);

        sequenceCalling = true;
    }

    private void requestForSpecificPermission() {

        mPermissionCheckService = new PermissionCheckService(getActivity(), new String[]{
                Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALENDAR, Manifest.permission.RECORD_AUDIO});

        mPermissionCheckService.closeAppIfDissalow = true;

        if (!mPermissionCheckService.checkIfAlreadyhavePermissions()) {
            mPermissionCheckService.requestForSpecificPermission();
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (mPermissionCheckService.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return;
        }
    }

    /*@Override
    public void onBackPressed() {
        if (listLayout.getVisibility() == View.VISIBLE) {
            listLayout.setVisibility(View.INVISIBLE);
            return;
        }

        super.onBackPressed();
    }
    */

    void runUserSelectContacts(boolean useSystemContacts){
        ViaAdActivityRunner.runSelectContactsActivity(getActivity(), useSystemContacts);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_MANUAL_ADD_CONTACTS) {
                return;
            } else if (requestCode == REQUEST_IMPORT_SALESFORCE_CONTACTS) {
                runUserSelectContacts(false);
                return;
            } else if (requestCode == REQUEST_SELECT_CONTACTS) {
                callContactCurrentIndex = 0;
                resetSequence();
                return;
            } else if (requestCode == REQUEST_VOICE_RECOGNITION_CODE){
                List<String> results = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                if (results == null || results.isEmpty())
                    return;

                if (AppConfig.useNativeGoogleVoiceRecognision) {
                    String spokenText = results.get(0);
                    doVoiceCommand(spokenText);
                    // Do something with spokenText
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void doVoiceCommand(String spokenText_) {
        if (spokenText_ == null)
            return;

        logString += "\nYou say:" + spokenText_;

        Toast.makeText(getActivity(), "You say:" + spokenText_, Toast.LENGTH_LONG).show();

        String spokenText = spokenText_.toLowerCase();
        if (spokenText.contains("hang up") ||
                spokenText.contains("end call") ||
                spokenText.contains("and call")){
            Toast.makeText(getActivity(), "END CALL DETECTED:", Toast.LENGTH_LONG).show();
            logString += "\nEND CALL DETECTED!";
            forceEndCall();
        }
    }

    private void runImportCRMContacts() {
        ViaAdActivityRunner.runSalesForceActivity(getActivity());
    }

    private void runManualAddContacts() {
        ViaAdActivityRunner.runAddContactActivity(getActivity());
    }

    private void runAutoConnectToZoom() {
        ViaAdActivityRunner.runAutoConnectToZoom(getActivity());
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displayGoogleDialogSpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        getActivity().startActivityForResult(intent, REQUEST_VOICE_RECOGNITION_CODE);
    }

    int volumeBeforeVoice = 0;

    private void runSpeechRecognizer() {
        logString += "\nRun SpeechRecognizer";
        if (StaticMemory.getInstance().selectedContactClassList == null) {
            logString += " canceled selectedContactClassList == null";
            return;
        }

        if (sequenceCalling == false && callContactSequenceIndex == 0) {
            logString += " canceled call sequence ended";
            return;
        }

        AudioManager audio = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        volumeBeforeVoice = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        //audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);


        audio.setStreamVolume(AudioManager.STREAM_MUSIC, volumeBeforeVoice, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    class SpeechListener implements RecognitionListener {
        final String TAG = SpeechListener.class.getName();

        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech");
        }
        public void onBeginningOfSpeech()
        {
            Log.d(TAG, "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB)
        {
            Log.d(TAG, "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer)
        {
            Log.d(TAG, "onBufferReceived");
        }
        public void onEndOfSpeech()
        {
            Log.d(TAG, "onEndofSpeech");
        }

        public void onError(int error)
        {
            Log.e(TAG,  "error " +  error);
            logString += "\nSpeechListener Error:" + error;
            //mText.setText("error " + error);
            Toast.makeText(getActivity(), "SpeechListener error:" + error, Toast.LENGTH_LONG).show();
            runSpeechRecognizer();
        }

        public void onResults(Bundle results)
        {
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for (int i = 0; i < data.size(); i++)
            {
                Log.d(TAG, "result " + data.get(i));
            }

            doVoiceCommand(data.get(0).toString());
            runSpeechRecognizer();
        }

        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            logString += "\nSpeechRecognizer event: " + eventType;
            Toast.makeText(getActivity(), "SpeechListener onEvent:" + eventType, Toast.LENGTH_LONG).show();
            Log.d(TAG, "onEvent " + eventType);
        }
    }
}
