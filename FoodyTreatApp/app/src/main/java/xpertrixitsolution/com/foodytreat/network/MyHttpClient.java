package xpertrixitsolution.com.foodytreat.network;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URI;

/**
 * @author Dhiraj Devkar
 * To handle connection between website and app we need http client
 */
public class MyHttpClient {
    private static final String TAG = MyHttpClient.class.getSimpleName();
    private static HttpPost httpPost;
    private HttpClient httpClient = null;
    static MyHttpClient myHttpClient;
    static String result = "";

    private MyHttpClient() {
        httpClient = new DefaultHttpClient();
        httpPost = new HttpPost();
    }

    public static MyHttpClient getInstance() {

        if (myHttpClient == null) {
            return new MyHttpClient();
        } else {
            return myHttpClient;
        }
    }

    // public String doPost(String Url, String data) {
    // try {
    //
    // HttpClient httpClient = new DefaultHttpClient();
    //
    // HttpPost httpPost = new HttpPost(Url.trim());
    // httpPost.setEntity(new StringEntity(data));
    //
    // HttpResponse httpResponse = httpClient.execute(httpPost);
    // HttpEntity httpEntity = httpResponse.getEntity();
    //
    // result = EntityUtils.toString(httpEntity);
    // } catch (Exception e) {
    //
    // e.printStackTrace();
    // return "Connection Refused";
    // }
    //
    // return result;
    //
    // }
    public String doPost(String URL, String requestobject) {
        Log.e(TAG, "URL:" + URL);
        Log.e(TAG, "PARAMS:" + requestobject);
        try {
            httpPost.setURI(URI.create(URL));
            httpPost.setHeader("content-type", "application/json");
            // httpPost.addHeader("API_OPINION_DESK_USERNAME","Test_user1");
            // httpPost.addHeader("API_OPINION_DESK_PASSWORD","Test_user1");
            httpPost.setEntity(new StringEntity(requestobject));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            String entityString = EntityUtils
                    .toString(httpResponse.getEntity());

            return entityString;
        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }
}
