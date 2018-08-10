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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cyril_rayan.callnow.login.LoginFrActivity;
import com.cyril_rayan.callnow.login.utils.DialogUtil;
import com.cyril_rayan.callnow.login.utils.SharedpreferenceUtility;
import com.cyril_rayan.callnow.login.webservicedetails.APIService;
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

public class PrepareSnowboyActivity extends AppCompatActivity {

    private static final String TAG = PrepareSnowboyActivity.class.getSimpleName();
    private WavRecorder wavRecorder = null;
    private MediaPlayer player = new MediaPlayer();
    private PlaybackThread playbackThread = new PlaybackThread();

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            MsgEnum message = MsgEnum.getMsgEnum(msg.what);
            switch (message){
                case MSG_ACTIVE:
                    try{
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

    ImageButton start_record1, stop_record1, playback1;
    ImageButton start_record2, stop_record2, playback2;
    ImageButton start_record3, stop_record3, playback3;
    Button generate_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_snowboy);


        generate_button = (Button)findViewById(R.id.button_generate);
        generate_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public  void onClick(View v){
                try {
                    generateNewModel();
                    Toast.makeText(PrepareSnowboyActivity.this, "Successfully download!", Toast.LENGTH_LONG).show();
                    Intent intent =new Intent(PrepareSnowboyActivity.this, WelcomeScreen.class);
                    startActivity(intent);
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        start_record1 = (ImageButton)findViewById(R.id.start_record1);
        start_record1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startRecording(Constants.VOICE_FILE1);

                start_record1.setVisibility(View.GONE);
                stop_record1.setVisibility(View.VISIBLE);
                playback1.setVisibility(View.GONE);
            }
        });

        stop_record1 = (ImageButton)findViewById(R.id.stop_record1);
        stop_record1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                stopRecording();
                start_record1.setVisibility(View.VISIBLE);
                stop_record1.setVisibility(View.GONE);
                playback1.setVisibility(View.VISIBLE);
            }
        });

        playback1 = (ImageButton)findViewById(R.id.playback1);
        playback1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                playVoice(Constants.VOICE_FILE1);

                start_record1.setVisibility(View.VISIBLE);
                stop_record1.setVisibility(View.GONE);
//              playback1.setVisibility(View.GONE);
            }
        });



        start_record2 = (ImageButton)findViewById(R.id.start_record2);
        start_record2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startRecording(Constants.VOICE_FILE2);

                start_record2.setVisibility(View.GONE);
                stop_record2.setVisibility(View.VISIBLE);
                playback2.setVisibility(View.GONE);
            }
        });

        stop_record2 = (ImageButton)findViewById(R.id.stop_record2);
        stop_record2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                stopRecording();
                start_record2.setVisibility(View.VISIBLE);
                stop_record2.setVisibility(View.GONE);
                playback2.setVisibility(View.VISIBLE);
            }
        });

        playback2 = (ImageButton)findViewById(R.id.playback2);
        playback2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                playVoice(Constants.VOICE_FILE2);
                start_record2.setVisibility(View.VISIBLE);
                stop_record2.setVisibility(View.GONE);
//              playback2.setVisibility(View.GONE);
            }
        });


        start_record3 = (ImageButton)findViewById(R.id.start_record3);
        start_record3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startRecording(Constants.VOICE_FILE3);

                start_record3.setVisibility(View.GONE);
                stop_record3.setVisibility(View.VISIBLE);
                playback3.setVisibility(View.GONE);
            }
        });

        stop_record3 = (ImageButton)findViewById(R.id.stop_record3);
        stop_record3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                stopRecording();
                start_record3.setVisibility(View.VISIBLE);
                stop_record3.setVisibility(View.GONE);
                playback3.setVisibility(View.VISIBLE);
            }
        });

        playback3 = (ImageButton)findViewById(R.id.playback3);
        playback3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                playVoice(Constants.VOICE_FILE3);
                start_record3.setVisibility(View.VISIBLE);
                stop_record3.setVisibility(View.GONE);
//              playback3.setVisibility(View.GONE);
            }
        });


        checkVoiceFileExist();

    }

    public void startRecording(final String fileName) {
        if (wavRecorder != null)
            stopRecording();

        wavRecorder = new WavRecorder(fileName);
        wavRecorder.startRecording();
    }

    public void stopRecording() {
        if (wavRecorder == null){
            return;
        }
        wavRecorder.stopRecording();
        wavRecorder = null;

    }

    public void playVoice(final String filename){
        if (playbackThread.playing())
            playbackThread.stopPlayback();

        playbackThread.startPlayback(playBackListener, filename);
    }

    public void checkVoiceFileExist(){
        try {
            File file = new File(Constants.VOICE_FILE1);
            if (file.exists())
                playback1.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            File file = new File(Constants.VOICE_FILE2);
            if (file.exists())
                playback2.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            File file = new File(Constants.VOICE_FILE3);
            if (file.exists())
                playback3.setVisibility(View.VISIBLE);
        }catch (Exception e){
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

    private void onEndOfPlayBack() {
        start_record1.setVisibility(View.VISIBLE);
        stop_record1.setVisibility(View.GONE);
        playback1.setVisibility(View.VISIBLE);
        playback1.setEnabled(true);
    }


    public void generateNewModel() throws IOException {
        wavRecorder = new WavRecorder(Constants.VOICE_FILE1);
        final byte[] data1 = playbackThread.readPCMData(Constants.VOICE_FILE1);

        if (data1 == null){
            alertMessage("Check the Voice1 again!");
            return;
        }

        final byte[] data2 = playbackThread.readPCMData(Constants.VOICE_FILE2);
        if (data2 == null){
            alertMessage("Check the Voice2 again!");
            return;
        }
        final byte[] data3 = playbackThread.readPCMData(Constants.VOICE_FILE3);
        if (data3 == null){
            alertMessage("Check the Voice3 again!");
            return;
        }

        String name = "EndCall";

        String vbuf1 = Base64.encodeToString(wavRecorder.AddWaveFileHeadertoBuffer(data1), Base64.NO_WRAP);
        String vbuf2 = Base64.encodeToString(wavRecorder.AddWaveFileHeadertoBuffer(data2), Base64.NO_WRAP);
        String vbuf3 = Base64.encodeToString(wavRecorder.AddWaveFileHeadertoBuffer(data3), Base64.NO_WRAP);

        byte[] response = ApiSnowBoy.requestModel(name, "en", "20_29", "M", "macbook microphone", vbuf1, vbuf2, vbuf3 );

        if (response == null){
            Log.e("Null message ", "Voice model request failed! Try again!");
        }else{
            boolean ret = ApiSnowBoy.writeDataToFile(Constants.MODEL_PATH, Constants.NEW_MODEL, response);
            if (ret){
                ApiSnowBoy.moveFile(Constants.MODEL_PATH, Constants.NEW_MODEL, Constants.DEFAULT_WORK_SPACE);
            }
        }
    }

    public void alertMessage(String msg){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(msg);
        alert.setNeutralButton("OK", null);
        alert.create().show();
    }


}
