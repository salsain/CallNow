package ai.kitt.snowboy.audio;

//import retrofit2.Call;
//import retrofit2.http.Body;
//import retrofit2.http.Header;
//import retrofit2.http.POST;
//import retrofit2.http.Query;

/**
 * Created by Айрат on 09.03.2018.
 */

public interface ApiShowBoyRequests {

    public static class SendSoundParams {
        public String name;//", name);
        public String language;//", language);//ar (Arabic), zh (Chinese), nl (Dutch), en (English), fr (French), dt (German), hi (Hindi), it (Italian), jp (Japanese), ko (Korean), fa (Persian), pl (Polish), pt (Portuguese), ru (Russian), es (Spanish), ot (Other)
        public String age_group;//", age_group);//0_9, 10_19, 20_29, 30_39, 40_49, 50_59, 60+

        public String gender;//", gender); //"F" "M"
        public String microphone;//", microphone);
        public String[] voice_samples = new String[]{"", "", ""};
    }

    public static class SendSoundResult{
        public Content response;

        public static class Content{
        }
    };

//    @POST("api/v1/train")
//    Call<SendSoundResult> sendSoundsToServe(@Body SendSoundParams sendSoundParams, @Header("Authorization") String token);
}
