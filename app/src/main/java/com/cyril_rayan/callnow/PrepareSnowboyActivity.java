package com.cyril_rayan.callnow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.common.utility.CountDownAnimation;
import com.cyril_rayan.callnow.login.LoginFrActivity;
import com.cyril_rayan.callnow.login.utils.DialogUtil;
import com.cyril_rayan.callnow.login.utils.SharedpreferenceUtility;
import com.cyril_rayan.callnow.login.webservicedetails.APIService;
import com.skyfishjy.library.RippleBackground;
import com.zipow.videobox.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ai.kitt.snowboy.ApiSnowBoy;
import ai.kitt.snowboy.Constants;
import ai.kitt.snowboy.MsgEnum;
//import ai.kitt.snowboy.audio.ApiShowBoyRequests;
import ai.kitt.snowboy.audio.AudioDataReceivedListener;
import ai.kitt.snowboy.audio.AudioDataSaver;
import ai.kitt.snowboy.audio.PlaybackThread;
import ai.kitt.snowboy.audio.RecordingThread;
import ai.kitt.snowboy.audio.WavRecorder;
import butterknife.OnClick;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;

public class PrepareSnowboyActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = PrepareSnowboyActivity.class.getSimpleName();
    private WavRecorder wavRecorder = null;
    private MediaPlayer player = new MediaPlayer();
    private PlaybackThread playbackThread = new PlaybackThread();
    private RippleBackground rippleBackground;
    private TextView mRecordOne, mRecordTwo, mRecordThree;
    private TextView countdown_tv;
    private ImageView genModel;
    private TextView record_file;
    private int currentRecording = 1;
    private String currentFilePath = Constants.VOICE_FILE1;    /**/

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            MsgEnum message = MsgEnum.getMsgEnum(msg.what);
            switch (message) {
                case MSG_ACTIVE:
                    try {
                        player.reset();
                        player.setDataSource(Constants.DEFAULT_WORK_SPACE + File.separatorChar + "ding.wav");
                        player.prepare();
                    } catch (IOException e) {
                        Log.e(TAG, "Playing ding sound error", e);
                    }
                    Log.e("endCallByVoice", "endCallByVoice");
                    break;
                case MSG_INFO:
                    break;
                case MSG_VAD_SPEECH:
                    break;
                case MSG_VAD_NOSPEECH:
                    break;
                case MSG_ERROR:
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };


    ImageView generate_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_snowboy);
        initialize();
        generate_button = (ImageView) findViewById(R.id.rightIv);
        generate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    generateNewModel();
                    Toast.makeText(PrepareSnowboyActivity.this, "Successfully download!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(PrepareSnowboyActivity.this, WelcomeScreen.class);
                    startActivity(intent);
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        (findViewById(R.id.skiptv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrepareSnowboyActivity.this, WelcomeScreen.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void initialize() {
        rippleBackground = (RippleBackground) findViewById(R.id.content);
        rippleBackground.startRippleAnimation();
        record_file = (TextView) findViewById(R.id.record_file);
        record_file.setText(setFileName(currentRecording));
        countdown_tv = (TextView) findViewById(R.id.countdown_tv);
        countdown_tv.setVisibility(View.GONE);
        genModel = (ImageView) findViewById(R.id.modelGen);
        genModel.setVisibility(View.VISIBLE);
        genModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCountDown();
            }
        });
        mRecordOne = (TextView) findViewById(R.id.recode_one);
        mRecordTwo = (TextView) findViewById(R.id.recode_two);
        mRecordThree = (TextView) findViewById(R.id.recode_three);
        mRecordOne.setVisibility(View.GONE);
        mRecordTwo.setVisibility(View.GONE);
        mRecordThree.setVisibility(View.GONE);
        mRecordOne.setOnClickListener(this);
        mRecordTwo.setOnClickListener(this);
        mRecordThree.setOnClickListener(this);
        checkVoiceFileExist();
    }

    private String setFileName(int currentRecording) {
        String fileName = "";
        switch (currentRecording) {
            case 1:
                fileName = "Record 1";
                currentFilePath = Constants.VOICE_FILE1;
                break;
            case 2:
                fileName = "Record 2";
                currentFilePath = Constants.VOICE_FILE2;
                break;
            case 3:
                fileName = "Record 3";
                currentFilePath = Constants.VOICE_FILE3;
                break;
            default:

        }
        return fileName;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recode_one:
                playVoice(Constants.VOICE_FILE1, mRecordOne);
                break;
            case R.id.recode_two:
                playVoice(Constants.VOICE_FILE2, mRecordTwo);
                break;
            case R.id.recode_three:
                playVoice(Constants.VOICE_FILE3, mRecordThree);
                break;
        }
    }

    public void playVoice(final String filename, final TextView textView) {
        textView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.pause, 0, 0, 0);
        if (playbackThread.playing())
            playbackThread.stopPlayback();

        playbackThread.startPlayback(new AudioTrack.OnPlaybackPositionUpdateListener() {
            @Override
            public void onMarkerReached(AudioTrack track) {
                int audio = track.getAudioFormat();
                playbackThread.stopPlayback();
                textView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.play, 0, 0, 0);
            }

            @Override
            public void onPeriodicNotification(AudioTrack track) {
                //int audio = track.getAudioFormat();
            }
        }, filename);
    }

    private void startCountDown() {
        countdown_tv.setVisibility(View.VISIBLE);
        genModel.setVisibility(View.GONE);
        countdown_tv.setText("Ready");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startRecording(currentFilePath);
                CountDownAnimation countDownAnimation = new CountDownAnimation(countdown_tv, 5);
                countDownAnimation.setCountDownListener(new CountDownAnimation.CountDownListener() {
                    @Override
                    public void onCountDownEnd(CountDownAnimation animation) {
                        stopRecording();
                        if ((currentRecording + 1) <= 3) {
                            ++currentRecording;
                            record_file.setText(setFileName(currentRecording));
                        } else {
                            currentRecording = 1;
                            record_file.setText(setFileName(currentRecording));
                        }
                        genModel.setVisibility(View.VISIBLE);
                    }
                });
                countDownAnimation.start();

            }
        }, 1000);
///storage/emulated/0/callnow/EndCall1.pcm
    }

    public void startRecording(final String fileName) {
        if (wavRecorder != null)
            stopRecording();

        wavRecorder = new WavRecorder(fileName);
        wavRecorder.startRecording();
    }

    public void stopRecording() {
        if (wavRecorder == null) {
            return;
        }
        wavRecorder.stopRecording();
        wavRecorder = null;

    }

    public void playVoice(final String filename) {
        if (playbackThread.playing())
            playbackThread.stopPlayback();

        playbackThread.startPlayback(playBackListener, filename);
    }

    public void checkVoiceFileExist() {
        try {
            File file = new File(Constants.VOICE_FILE1);
            if (file.exists())
                mRecordOne.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            File file = new File(Constants.VOICE_FILE2);
            if (file.exists())
                mRecordTwo.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            File file = new File(Constants.VOICE_FILE3);
            if (file.exists())
                mRecordThree.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    AudioTrack.OnPlaybackPositionUpdateListener playBackListener = new AudioTrack.OnPlaybackPositionUpdateListener() {
        @Override
        public void onMarkerReached(AudioTrack track) {
            int audio = track.getAudioFormat();
//            onEndOfPlayBack();
            playbackThread.stopPlayback();
        }

        @Override
        public void onPeriodicNotification(AudioTrack track) {
            //int audio = track.getAudioFormat();
        }
    };


    public void generateNewModel() throws IOException {
        wavRecorder = new WavRecorder(Constants.VOICE_FILE1);
        final byte[] data1 = playbackThread.readPCMData(Constants.VOICE_FILE1);

        if (data1 == null) {
            alertMessage("Check the Voice1 again!");
            return;
        }

        final byte[] data2 = playbackThread.readPCMData(Constants.VOICE_FILE2);
        if (data2 == null) {
            alertMessage("Check the Voice2 again!");
            return;
        }
        final byte[] data3 = playbackThread.readPCMData(Constants.VOICE_FILE3);
        if (data3 == null) {
            alertMessage("Check the Voice3 again!");
            return;
        }

        String name = "EndCall";

        String vbuf1 = Base64.encodeToString(wavRecorder.AddWaveFileHeadertoBuffer(data1), Base64.NO_WRAP);
        String vbuf2 = Base64.encodeToString(wavRecorder.AddWaveFileHeadertoBuffer(data2), Base64.NO_WRAP);
        String vbuf3 = Base64.encodeToString(wavRecorder.AddWaveFileHeadertoBuffer(data3), Base64.NO_WRAP);

        byte[] response = ApiSnowBoy.requestModel(name, "en", "20_29", "M", "macbook microphone", vbuf1, vbuf2, vbuf3);

        if (response == null) {
            Log.e("Null message ", "Voice model request failed! Try again!");
        } else {
            boolean ret = ApiSnowBoy.writeDataToFile(Constants.MODEL_PATH, Constants.NEW_MODEL, response);
            if (ret) {
                ApiSnowBoy.moveFile(Constants.MODEL_PATH, Constants.NEW_MODEL, Constants.DEFAULT_WORK_SPACE);
            }
        }
    }

    public void alertMessage(String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(msg);
        alert.setNeutralButton("OK", null);
        alert.create().show();
    }


}
