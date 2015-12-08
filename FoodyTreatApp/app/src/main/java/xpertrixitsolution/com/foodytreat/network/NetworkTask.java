package xpertrixitsolution.com.foodytreat.network;

/**
 *@author Dhiraj
 * To handle network tasks
 */
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import xpertrixitsolution.com.foodytreat.LoginActivity;
import xpertrixitsolution.com.foodytreat.SplashScreen;

public class NetworkTask extends AsyncTask<Object, Integer, JSONObject> {

    private static final String TAG = "NetworkTask";
    private CompletionListener parentActivity;
    private Context context;

    private int RESPONSE_IDENTIFIER_FLAG;
    private Boolean dialogFlag;
    private ProgressDialog pDialog;
    private JSONParser jsonParser = new JSONParser();


    public NetworkTask(CompletionListener parent, Context context,
                       boolean dialogFlag) {
        parentActivity = parent;
        this.context = context;
        this.dialogFlag = dialogFlag;

    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (dialogFlag) {
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Connecting Server ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

    }

    @Override
    protected JSONObject doInBackground(Object... params) {


        List<NameValuePair> params1 = (List<NameValuePair>) params[0];

        String POST_URL = (String) params[1];
        Log.e("POST_URL", POST_URL);

        RESPONSE_IDENTIFIER_FLAG = (Integer) params[2];

        // Posting user data to script
        JSONObject json = jsonParser.makeHttpRequest(POST_URL,
                "POST", params1);
         Log.e("param1", params1.toString());
        return json;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);


        try {
            if (dialogFlag) {
                pDialog.dismiss();
            }
            if (jsonObject != null) {

                Log.e(TAG, "Response:" + jsonObject.toString());

                parentActivity.onComplete(jsonObject, RESPONSE_IDENTIFIER_FLAG);

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                builder.setMessage("Unable to connect with server.Please check your internet connection and try again.");
                builder.setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(new Intent(context, SplashScreen.class));
                    }
                });
                AlertDialog alert=builder.create();
                alert.show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
