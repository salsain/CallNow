package com.cyril_rayan.callnow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.utility.CountDownAnimation;
import com.skyfishjy.library.RippleBackground;

import java.io.File;
import java.io.IOException;

import ai.kitt.snowboy.ApiSnowBoy;
import ai.kitt.snowboy.Constants;
import ai.kitt.snowboy.audio.PlaybackThread;
import ai.kitt.snowboy.audio.WavRecorder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ModelFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ModelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ModelFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RippleBackground rippleBackground;
    private TextView mRecordOne, mRecordTwo, mRecordThree;
    private WavRecorder wavRecorder = null;
    private MediaPlayer player = new MediaPlayer();
    private PlaybackThread playbackThread = new PlaybackThread();
    private TextView countdown_tv;
    private ImageView genModel;
    private TextView record_file;
    private int currentRecording = 1;
    private String currentFilePath = Constants.VOICE_FILE1;    /**/


    public ModelFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ModelFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ModelFragment newInstance(String param1, String param2) {
        ModelFragment fragment = new ModelFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (rippleBackground != null) {
            rippleBackground.stopRippleAnimation();
        }
        try {
            stopRecording();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rippleBackground != null) {
            rippleBackground.startRippleAnimation();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_model, container, false);
        TextView headerTv = ((TextView) view.findViewById(R.id.txtToolbarTitle));
        headerTv.setText("Call Now");

        return view;
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


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initialize(view);
            }
        }, 100);

    }

    private void initialize(View view) {
        rippleBackground = (RippleBackground) view.findViewById(R.id.content);
        rippleBackground.startRippleAnimation();
        record_file = (TextView) view.findViewById(R.id.record_file);
        record_file.setText(setFileName(currentRecording));
        countdown_tv = (TextView) view.findViewById(R.id.countdown_tv);
        countdown_tv.setVisibility(View.GONE);
        genModel = (ImageView) view.findViewById(R.id.modelGen);
        genModel.setVisibility(View.VISIBLE);
        genModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCountDown();
            }
        });
        try {
            generateNewModel();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecordOne = (TextView) view.findViewById(R.id.recode_one);
        mRecordTwo = (TextView) view.findViewById(R.id.recode_two);
        mRecordThree = (TextView) view.findViewById(R.id.recode_three);
        mRecordOne.setVisibility(View.GONE);
        mRecordTwo.setVisibility(View.GONE);
        mRecordThree.setVisibility(View.GONE);
        mRecordOne.setOnClickListener(this);
        mRecordTwo.setOnClickListener(this);
        mRecordThree.setOnClickListener(this);
        checkVoiceFileExist();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage(msg);
        alert.setNeutralButton("OK", null);
        alert.create().show();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
