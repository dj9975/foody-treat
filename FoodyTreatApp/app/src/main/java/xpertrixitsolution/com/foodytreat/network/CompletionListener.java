package xpertrixitsolution.com.foodytreat.network;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Dhiraj Devkar
 */
public interface CompletionListener {

    public void onComplete(JSONObject serverResponse, int Response_From) throws JSONException;

}