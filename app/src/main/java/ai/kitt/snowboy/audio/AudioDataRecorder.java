package ai.kitt.snowboy.audio;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ai.kitt.snowboy.Constants;

/**
 * Created by Ting on 5/9/2018.
 */

public class AudioDataRecorder {

    private static final String TAG = AudioDataRecorder.class.getSimpleName();

    private File saveFile = null;
    private DataOutputStream dataOutputStreamInstance = null;

    public AudioDataRecorder(String fileName) {
        saveFile = new File(fileName);
        saveFile.mkdirs();
        Log.e(TAG, fileName);
    }

    public void start() {
        if(null != saveFile) {
            if (saveFile.exists()) {
                saveFile.delete();
            }
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "IO Exception on creating audio file " + saveFile.toString(), e);
            }

            try {
                BufferedOutputStream bufferedStreamInstance  = new BufferedOutputStream(
                        new FileOutputStream(this.saveFile));
                dataOutputStreamInstance = new DataOutputStream(bufferedStreamInstance);
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("Cannot Open File", e);
            }
        }
    }

    public void onAudioDataReceived(byte[] data, int length) {
        try {
            if(null != dataOutputStreamInstance) {
                dataOutputStreamInstance.write(data, 0, length);
            }
        } catch (IOException e) {
            Log.e(TAG, "IO Exception on saving audio file " + saveFile.toString(), e);
        }
    }

    public void stop() {
        if(null != dataOutputStreamInstance) {
            try {
                dataOutputStreamInstance.close();
            } catch (IOException e) {
                Log.e(TAG, "IO Exception on finishing saving audio file " + saveFile.toString(), e);
            }
            Log.e(TAG, "Recording saved to " + saveFile.toString());
        }
    }

}
