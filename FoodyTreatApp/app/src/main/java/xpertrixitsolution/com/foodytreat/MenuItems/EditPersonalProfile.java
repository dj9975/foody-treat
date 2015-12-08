package xpertrixitsolution.com.foodytreat.MenuItems;
/**
 * @author: Dhiraj Devkar
 * 16/10/2015
 *
 * last edited on 29/10/2015 , fb logout from program without using fb logout button
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import xpertrixitsolution.com.foodytreat.Cart_Details;
import xpertrixitsolution.com.foodytreat.LoginActivity;
import xpertrixitsolution.com.foodytreat.MainActivity;
import xpertrixitsolution.com.foodytreat.R;
import xpertrixitsolution.com.foodytreat.SelectCake;
import xpertrixitsolution.com.foodytreat.Util;
import xpertrixitsolution.com.foodytreat.network.CompletionListener;
import xpertrixitsolution.com.foodytreat.network.NetworkTask;
import xpertrixitsolution.com.foodytreat.network.Post_URL;
import xpertrixitsolution.com.foodytreat.network.Responce;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class EditPersonalProfile extends Activity implements CompletionListener{
    private EditText edProfileFirstName,edProfileLastName,edProfileEmailId,edProfileContactNo,edProfileAddress;
    private EditText edOldPassword,edNewPassword,edReNewPassword;
    private Button btnProfileSave,btnProfileCancle,btnChangePassword;
    private  String firstname,lastname,emailid,contactno,address,cust_id;
    private NetworkTask networkTask;
    private ProgressDialog progress;
    private TextView tvChangePassword;
    private LinearLayout llayout;
    private String oldpassword,newpassword,renewpassword;
    private Dialog ChangePassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_profile);
        initviews();

        String screenSize= Util.getSizeName(this);
        if(screenSize.equals("HDPI")){
            llayout.setBackgroundResource(R.drawable.back_mdpi);
        }else if(screenSize.equals("LDPI")){
            llayout.setBackgroundResource(R.drawable.back_mdpi);
        }else if(screenSize.equals("XHDPI")){
            llayout.setBackgroundResource(R.drawable.back_xhdpi);
        }else if(screenSize.equals("XXHDPI")){
            llayout.setBackgroundResource(R.drawable.back_xxhdpi);
        }else{
            llayout.setBackgroundResource(R.drawable.back_mdpi);
        }

        getSharedPref();

        progress=new ProgressDialog(this);
        progress.setMessage("Loading");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
        progress.setCanceledOnTouchOutside(false);
        getPersonalDetails();

        btnProfileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
        btnProfileCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 ChangePassword = new Dialog(EditPersonalProfile.this);
                ChangePassword.setContentView(R.layout.popup_change_password);
                ChangePassword.setTitle("Change Password");

                btnChangePassword=(Button)ChangePassword.findViewById(R.id.btnChangePassword);
                 edOldPassword=(EditText) ChangePassword.findViewById(R.id.edOldPassword);
                edNewPassword=(EditText) ChangePassword.findViewById(R.id.edNewPassword);
                edReNewPassword=(EditText) ChangePassword.findViewById(R.id.edReNewPassword);
                btnChangePassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        oldpassword=edOldPassword.getText().toString();
                        newpassword=edNewPassword.getText().toString();
                        renewpassword=edReNewPassword.getText().toString();
                        if(oldpassword.isEmpty()){
                            edOldPassword.setError("FIELD CANNOT BE EMPTY");
                        }else if(newpassword.isEmpty()){
                            edNewPassword.setError("FIELD CANNOT BE EMPTY");
                        }else if(renewpassword.isEmpty()){
                            edReNewPassword.setError("FIELD CANNOT BE EMPTY");
                        }else if(!newpassword.equals(renewpassword)){
                            edReNewPassword.setError("Password do not match");
                        }else{
                            //call webservice
                            changePassword();
                        }
                    }
                });
                ChangePassword.show();
            }
        });
    }

    public void getSharedPref(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        cust_id= sharedPreferences.getString("cust_id", "0");

    }
    private void initviews() {
        edProfileFirstName=(EditText)findViewById(R.id.edProfileFirstName);
        edProfileLastName=(EditText)findViewById(R.id.edProfileLastName);
        edProfileEmailId=(EditText)findViewById(R.id.edProfileEmailId);
        edProfileContactNo=(EditText)findViewById(R.id.edProfileContactNo);
        edProfileAddress=(EditText)findViewById(R.id.edProfileAddress);
        btnProfileSave=(Button)findViewById(R.id.btnProfileSave);
        btnProfileCancle=(Button)findViewById(R.id.btnProfileCancel);
        tvChangePassword=(TextView)findViewById(R.id.tvChangePassword);
        llayout=(LinearLayout)findViewById(R.id.llayout);
    }

    private void validate() {
        firstname=edProfileFirstName.getText().toString();
        lastname=edProfileLastName.getText().toString();
        emailid=edProfileEmailId.getText().toString();
        contactno=edProfileContactNo.getText().toString();
        address=edProfileAddress.getText().toString();

        boolean emailcheck = checkEmail(emailid);
        if (!emailcheck) {
            edProfileEmailId.setError("Email Id is not valid");
        } else if (emailid.length() == 0) {
            edProfileEmailId.requestFocus();
            edProfileEmailId.setError("FIELD CANNOT BE EMPTY");
        } else if (firstname.length() == 0) {
            edProfileFirstName.requestFocus();
            edProfileFirstName.setError("FIELD CANNOT BE EMPTY");
        } else if (lastname.length() == 0) {
            edProfileLastName.requestFocus();
            edProfileLastName.setError("FIELD CANNOT BE EMPTY");
        }else if (contactno.length() == 0) {
            edProfileContactNo.requestFocus();
            edProfileContactNo.setError("FIELD CANNOT BE EMPTY");
        }else if (address.length() == 0) {
            edProfileAddress.requestFocus();
            edProfileAddress.setError("FIELD CANNOT BE EMPTY");
        } else {
            sendPersonalDetails();
        }
    }

    public void getPersonalDetails(){//get personal details if available
        networkTask = new NetworkTask(this, this, false);
        List<NameValuePair> params = getRequestParams();
        networkTask.execute(params, Post_URL.URL_GET_PERSONAL_DETAILS, 1);
    }

    public void sendPersonalDetails(){//send personal details
        networkTask = new NetworkTask(this, this, false);
        List<NameValuePair> params = getRequestParamsSendDetails();
        networkTask.execute(params, Post_URL.URL_SEND_PERSONAL_DETAILS, 2);
    }

    public void changePassword(){//change password
        networkTask = new NetworkTask(this, this, false);
        List<NameValuePair> params = getRequestParamsChangePass();
        networkTask.execute(params, Post_URL.URL_CHANGE_PASSWORD, 3);
    }

    private List<NameValuePair> getRequestParams() {
        // TODO Enter Comments Here ...
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", cust_id));
        return params;
    }

    private List<NameValuePair> getRequestParamsSendDetails() {
        // TODO Enter Comments Here ...
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", cust_id));
        params.add(new BasicNameValuePair("first_name",edProfileFirstName.getText().toString()));
        params.add(new BasicNameValuePair("last_name", edProfileLastName.getText().toString()));
        params.add(new BasicNameValuePair("email_id", edProfileEmailId.getText().toString()));
        params.add(new BasicNameValuePair("contact_no", edProfileContactNo.getText().toString()));
        params.add(new BasicNameValuePair("address", edProfileAddress.getText().toString()));
        return params;
    }

    private List<NameValuePair> getRequestParamsChangePass() {
        // TODO Enter Comments Here ...
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", cust_id));
        params.add(new BasicNameValuePair("current_password", oldpassword));
        params.add(new BasicNameValuePair("new_password", newpassword));
        return params;
    }

    private void handleResponse(JSONObject serverResponse) {
        int success = 0;

        try {
            success = serverResponse.getInt(Responce.TAG_SUCCESS);
            if (success == 1) {
                JSONArray jsonArray = serverResponse.getJSONArray("customer_detail");
                for (int i = 0; i < jsonArray.length(); i++) {
                    edProfileFirstName.setText(jsonArray.getJSONObject(i).getString("firstname"));
                    edProfileLastName.setText(jsonArray.getJSONObject(i).getString("lastname"));
                    edProfileContactNo.setText(jsonArray.getJSONObject(i).getString("contact_no"));
                    edProfileEmailId.setText(jsonArray.getJSONObject(i).getString("email"));
                    edProfileAddress.setText(jsonArray.getJSONObject(i).getString("address"));
                }
                progress.dismiss();
            } else {
                progress.dismiss();
            }
        } catch (JSONException e) {

        }
    }

    private void handleResponseSendDeatils(JSONObject serverResponse) {
        int success = 0;

        try {
            success = serverResponse.getInt(Responce.TAG_SUCCESS);
            if (success == 1) {
                progress.dismiss();
                finish();
                Toast.makeText(EditPersonalProfile.this,serverResponse.getString(Responce.TAG_MESSAGE),Toast.LENGTH_SHORT).show();
            } else {
                progress.dismiss();
                Toast.makeText(EditPersonalProfile.this,serverResponse.getString(Responce.TAG_MESSAGE),Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {

        }
    }

    private void handleResponseChangePassword(JSONObject serverResponse) {
        int success = 0;

        try {
            success = serverResponse.getInt(Responce.TAG_SUCCESS);
            if (success == 1) {
                progress.dismiss();
                ChangePassword.dismiss();
                Toast.makeText(EditPersonalProfile.this,serverResponse.getString(Responce.TAG_MESSAGE),Toast.LENGTH_SHORT).show();
            } else {
                progress.dismiss();
                Toast.makeText(EditPersonalProfile.this,serverResponse.getString(Responce.TAG_MESSAGE),Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {

        }
    }


    @Override
    public void onComplete(JSONObject serverResponse, int RESPONSE_IDENTIFIER_FLAG) throws JSONException {
        switch (RESPONSE_IDENTIFIER_FLAG) {
            case 1:
                handleResponse(serverResponse);
                break;
            case 2:
                handleResponseSendDeatils(serverResponse);
                break;
            case 3:
                handleResponseChangePassword(serverResponse);
            default:
        }
    }

    /**
     * For EmailId validation
     */
    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    private boolean checkEmail(String email) {
        //returns true if email id is valid
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_personal_profile, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            Intent intentMain = new Intent(this, MainActivity.class);
            //Start Activity
            startActivity(intentMain);
            return true;
        }
        switch (id) {
            case R.id.menu_faq:
                Intent menu_faq = new Intent(this, MenuFAQ.class);
                startActivity(menu_faq);
                break;
            case R.id.menu_logout:
                getSharedPref();
                if (cust_id.equals("0")) {

                } else {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("cust_id", "0");
                    editor.putString("cust_email_id", "");
                    Toast.makeText(EditPersonalProfile.this, "Logout successfull", Toast.LENGTH_SHORT).show();
                    editor.commit();
                    Intent inten = new Intent(EditPersonalProfile.this, LoginActivity.class);
                    startActivity(inten);

                }
                return true;
            case R.id.menu_viewCart:
                Intent intent = new Intent(EditPersonalProfile.this, Cart_Details.class);
                startActivity(intent);
                return true;

            case R.id.menu_wishlist:
                getSharedPref();
                if (!cust_id.equals("0")) {
                    Intent myIntent1 = new Intent(this,
                            SelectCake.class);

                    myIntent1.putExtra("coming_from", "Coming from wishlist");
                    startActivity(myIntent1);
                } else {
                        Util.LoginAlert(this);                }
                break;

            case R.id.menu_orderhistory:
                getSharedPref();
                if (!cust_id.equals("0")) {
                    Intent myIntent2 = new Intent(this,
                            MenuItemOrderHistory.class);
                    startActivity(myIntent2);
                } else {
                    Util.LoginAlert(this);
                }
                break;

            case R.id.menu_legal:
                Intent legal = new Intent(this, MenuPolicies.class);
                startActivity(legal);
                break;

            case R.id.menu_about_us:
                Intent about_us = new Intent(this, About_us.class);
                startActivity(about_us);
                break;

            case R.id.menu_trackorder:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(EditPersonalProfile.this);
                builder1.setMessage("Track Order coming soon!");
                builder1.setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert1 = builder1.create();
                alert1.show();

                break;
            case R.id.menu_rate_app:
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=xpertrixitsolution.com.foodytreat"));
                startActivity(browser);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
