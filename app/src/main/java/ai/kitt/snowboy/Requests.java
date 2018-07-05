package ai.kitt.snowboy;

/**
 * Created by Ting on 5/11/2018.
 */

import android.os.StrictMode;
import android.util.Log;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;


public class Requests {

    private static final String TAG = "UTILS.REQUESTS";

    private static byte[] convertStreamTobyte(InputStream is) throws IOException{
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }


    public static byte[] Post(String URL, JSONObject DATA) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

//        alert("here request Posted!");

        HttpPost post = new HttpPost(URL);
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");
        post.setHeader("Accept-Encoding", "gzip");
        try{
            post.setEntity(new StringEntity(DATA.toString()));

            HttpResponse response = new DefaultHttpClient().execute(post);
            HttpEntity entity = response.getEntity();

//            alert("TRY!");
            if (entity != null) {
                // Read the content stream
//                alert("Read Content!");
                InputStream instream = entity.getContent();
                Header contentEncoding = response.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    instream = new GZIPInputStream(instream);
                }

                // convert content stream to a String
                byte[] resultString= convertStreamTobyte(instream);
                instream.close();

//                alert(resultString);

                Log.i("", resultString.toString());
                // Transform the String into a JSONObject
                try {
                    return resultString;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static HttpResponse Get(String URL) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            HttpGet get = new HttpGet(URL);
            return new DefaultHttpClient().execute(get);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}