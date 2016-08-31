package nammari.reservation.util;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by nammari on 8/30/16.
 * email : nammariahmad@gmail.com
 * phone : +962798939560
 */

public class HttpUtils {

    private static OkHttpClient client = new OkHttpClient();

    public static InputStream executeGet(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().byteStream();
    }




}
