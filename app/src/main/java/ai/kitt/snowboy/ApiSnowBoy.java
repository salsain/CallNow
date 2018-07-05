package ai.kitt.snowboy;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ApiSnowBoy {

    static private String url = "https://snowboy.kitt.ai/api/v1/train/";
    static public final String token = "c3d5f104c003560ce5baddd2ed17f3389ea5becc";

    public static byte[] requestModel(String name, String language, String age_group, String gender, String microphone, String data1, String data2, String data3) {
        try{

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("name", name);
            jsonObject.put("language", language);
            jsonObject.put("age_group", age_group);
            jsonObject.put("gender", gender);
            jsonObject.put("microphone", microphone);
            jsonObject.put("token", token);

            JSONArray wavArray = new JSONArray();

            JSONObject jdata1 = new JSONObject();
            jdata1.put("wave", data1);

            JSONObject jdata2 = new JSONObject();
            jdata2.put("wave", data2);

            JSONObject jdata3 = new JSONObject();
            jdata3.put("wave", data3);

            wavArray.put(jdata1);
            wavArray.put(jdata2);
            wavArray.put(jdata3);

            jsonObject.put("voice_samples", wavArray);

            System.out.println(jsonObject.toString());

            byte[] response = Requests.Post(url, jsonObject);
            return response;

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static boolean writeDataToFile(String dirName, String fileName, byte[] data) throws IOException{

        File file0 = new File(dirName);
        if (!file0.exists()) {
            file0.mkdirs();
        }
        Log.e("make director", dirName);

        File file = new File(dirName + File.separatorChar + fileName);

        boolean ret = false;

        boolean fvar = false;

        if(!file.exists()) {
            fvar = file.createNewFile();
        }

        if (fvar){
            System.out.println("File has been created successfully");
        }
        else{
            System.out.println("File already present at the specified location");
        }

        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        if (null != outputStream) {
            if (data.length > 0){
                try {
                    outputStream.write(data);
                    ret = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            try {
                outputStream.close();
                if (ret) return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public static int moveFile(String inputPath, String inputFile, String outputPath) {
        int ret = 0;
        InputStream in = null;
        OutputStream out = null;
        try {
            File src = new File(inputPath + File.separatorChar + inputFile);
            if (!src.exists())
                return Constants.NO_MODEL;
            File dst = new File(outputPath + File.separatorChar + inputFile);

            if (dst.exists()) dst.delete();

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }

            in = new FileInputStream(inputPath + File.separatorChar + inputFile);
            out = new FileOutputStream(outputPath + File.separatorChar + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            src.delete();
            return Constants.MOVE_SUCCESS;
        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
            return Constants.NO_MODEL;
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
            return Constants.MOVE_FAILED;
        }

    }

}

