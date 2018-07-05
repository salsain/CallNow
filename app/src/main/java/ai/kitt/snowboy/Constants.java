package ai.kitt.snowboy;
import java.io.File;
import android.os.Environment;

public class Constants {
    public static final String ASSETS_RES_DIR = "callnow";
    public static final String DEFAULT_WORK_SPACE = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separatorChar + "callnow";
    public static final String ACTIVE_UMDL1 = "EndCall.pmdl";
    public static final String ACTIVE_UMDL2 = "HangUp.pmdl";
    public static final String ACTIVE_RES = "common.res";
    public static final String SAVE_AUDIO = DEFAULT_WORK_SPACE + File.separatorChar + "recording.pcm";
    public static final int SAMPLE_RATE = 16000;
    public static final String VOICE_FILE1 = DEFAULT_WORK_SPACE + File.separatorChar + "EndCall1.pcm";
    public static final String VOICE_FILE2 = DEFAULT_WORK_SPACE + File.separatorChar + "EndCall2.pcm";
    public static final String VOICE_FILE3 = DEFAULT_WORK_SPACE + File.separatorChar + "EndCall3.pcm";
    public static final String MODEL_PATH = DEFAULT_WORK_SPACE + File.separatorChar + "model";
    public static final String NEW_MODEL = "EndCall.pmdl";
    public static final int NO_MODEL = -1;
    public static final int MOVE_SUCCESS = 0;
    public static final int MOVE_FAILED = 1;
}
