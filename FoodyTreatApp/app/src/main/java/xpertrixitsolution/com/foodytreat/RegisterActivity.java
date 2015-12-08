package xpertrixitsolution.com.foodytreat;
/**
 * @author Vrushali Matale, created on 18/09/2015
 * edited by: Dhiraj Devkar aaaa
 * last edit: 16/10/2015
 */

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import xpertrixitsolution.com.foodytreat.network.CompletionListener;
import xpertrixitsolution.com.foodytreat.network.NetworkTask;
import xpertrixitsolution.com.foodytreat.network.Post_URL;
import xpertrixitsolution.com.foodytreat.network.Responce;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class RegisterActivity extends Activity implements View.OnClickListener,CompletionListener{

    private Button btnLinkToLoginScreen,btnRegister,btnSelectDob;
    private EditText edFirstName,edLastName,edContactNo,edEmailId,edPass,edRepass;
    private NetworkTask networkTask;
    String mobileno;
    private ProgressDialog progress;
    private int otp,FlagGoogleRegister=0,FlagDobEntered=0;
    private int flagValidationDone=0;
    private TextView tvRegister;
    private RadioButton rbMale,rbFemale;

    private DatePickerDialog dbDatePicker;
    int day,month,year;
    private String selected_dob;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register_user);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//to disable automatic opening of keyboard
        initviews();

        String screenSize= Util.getSizeName(this);
        if(screenSize.equals("HDPI")){
            scrollView.setBackgroundResource(R.drawable.back_mdpi);
        }else if(screenSize.equals("LDPI")){
            scrollView.setBackgroundResource(R.drawable.back_mdpi);
        }else if(screenSize.equals("XHDPI")){
            scrollView.setBackgroundResource(R.drawable.back_xhdpi);
        }else if(screenSize.equals("XXHDPI")){
            scrollView.setBackgroundResource(R.drawable.back_xxhdpi);
        }else{
            scrollView.setBackgroundResource(R.drawable.back_mdpi);
        }
        //set typeface lobster
        Typeface myfont= Typeface.createFromAsset(getAssets(),"lobster.ttf");
        tvRegister.setTypeface(myfont);

    }

    private void initviews() {

        scrollView=(ScrollView)findViewById(R.id.scrollView1);
        btnLinkToLoginScreen = (Button) findViewById(R.id.btnLinkToLoginScreen);
        btnRegister=(Button)findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(this);
        btnLinkToLoginScreen.setOnClickListener(this);

        edFirstName=(EditText)findViewById(R.id.edFirstName);
        edLastName=(EditText)findViewById(R.id.edLastName);
        btnSelectDob=(Button)findViewById(R.id.btnDob);
        btnSelectDob.setOnClickListener(this);
        edContactNo=(EditText)findViewById(R.id.edContactNo);
        edEmailId=(EditText)findViewById(R.id.edEmailId);
        edPass=(EditText)findViewById(R.id.edPass);
        edRepass=(EditText)findViewById(R.id.edRePass);
        tvRegister=(TextView)findViewById(R.id.tvRegister);
        rbMale=(RadioButton)findViewById(R.id.rbMale);
        rbFemale=(RadioButton)findViewById(R.id.rbFemale);

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.btnRegister:

                Validatesignup();//validations

                if(flagValidationDone==1) {//if validation is successful
                    progress.dismiss();
                    Dialog dialogOTP = new Dialog(RegisterActivity.this);
                    dialogOTP.setContentView(R.layout.otp_popup);
                    final EditText edEnteredOtp = (EditText) dialogOTP.findViewById(R.id.edOtp);

                    Button btnConfirmOtp = (Button) dialogOTP.findViewById(R.id.btnConfirmOtp);
                    dialogOTP.setTitle("OTP");
                    //edEnteredOtp.setText(String.valueOf(Util.otp));
                    btnConfirmOtp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String enteredotp = edEnteredOtp.getText().toString();
                            if(enteredotp.isEmpty()) {
                                edEnteredOtp.setError("Please enter OTP.");
                            } else if (Integer.parseInt(enteredotp) == Util.otp) {//if entered otp is same as generated otp
                                signUp();
                            } else {
                                edEnteredOtp.setError("Please enter correct OTP.");
                            }
                        }
                    });
                    dialogOTP.show();
                }
                break;

            case R.id.btnLinkToLoginScreen:
                Intent intent1 = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;

            case R.id.btnDob:
                    setDateTimeField();
                break;
            default:
                break;
        }
    }


    private void signUp() {
        progress=new ProgressDialog(this);
        progress.setMessage("Loading");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        networkTask = new NetworkTask(this, this, false);
        List<NameValuePair> params = getRequestParams();
        networkTask.execute(params, Post_URL.URL_SIGNUP, 1);
    }

    public void Validatesignup() {
        String FirstName=edFirstName.getText().toString();
        String LastName=edLastName.getText().toString();
        String Email = edEmailId.getText().toString();
     String Password = edPass.getText().toString();
     String Repassword = edRepass.getText().toString();
     mobileno = edContactNo.getText().toString();
        boolean emailcheck = checkEmail(Email);
        if(FirstName.length()==0){

            edFirstName.requestFocus();
            edFirstName.setError("FIELD CANNOT BE EMPTY");
        }else if(LastName.length()==0){

            edLastName.requestFocus();
            edLastName.setError("FIELD CANNOT BE EMPTY");
        }else if(!emailcheck){

            edEmailId.setError("Email Id is not valid");
        }else if (Email.length() == 0) {

         edEmailId.requestFocus();
         edEmailId.setError("FIELD CANNOT BE EMPTY");
     } else if (Password.length() == 0) {

         edPass.requestFocus();
         edPass.setError("FIELD CANNOT BE EMPTY");
     } else if (Repassword.length() == 0) {

         edRepass.requestFocus();
         edRepass.setError("FIELD CANNOT BE EMPTY");
     } else if ((Password.compareTo(Repassword) != 0)) {

         edRepass.requestFocus();
         edRepass.setError("Password do not match");
     } else if (mobileno.length() == 0) {

         edContactNo.requestFocus();
         edContactNo.setError("FIELD CANNOT BE EMPTY");
     }else if(FlagDobEntered==0){

            btnSelectDob.requestFocus();
            btnSelectDob.setError("FIELD CANNOT BE EMPTY");
        }else{
         flagValidationDone=1;
         progress=new ProgressDialog(this);
         progress.setMessage("Loading");
         progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
         progress.setIndeterminate(true);
         progress.setProgress(0);
         progress.show();
         progress.setCancelable(false);
         progress.setCanceledOnTouchOutside(false);
         new TaskExample(mobileno,"otp").execute();
     }
   }


    private void setDateTimeField() {

        try {
            final Calendar newCalendar = Calendar.getInstance();
            dbDatePicker = new DatePickerDialog(RegisterActivity.this,
                    new DatePickerDialog.OnDateSetListener() {

                        public void onDateSet(DatePicker view, int yearSelected,
                                              int monthOfYear, int dayOfMonth) {
                            newCalendar.set(yearSelected, monthOfYear, dayOfMonth);
                            year = yearSelected;
                            month = monthOfYear;
                            day = dayOfMonth;
                            // Set the Selected Date in Select date Button
                            btnSelectDob.setText("Date selected : " + day + "-" + (month+1) + "-" + year);
                            selected_dob=day+"-"+(month+1)+"-"+year;
                            FlagDobEntered=1;
                        }

                    }, newCalendar.get(Calendar.YEAR),
                    newCalendar.get(Calendar.MONTH),
                    newCalendar.get(Calendar.DAY_OF_MONTH));


            dbDatePicker.show();
        } catch (Exception e) {
            e.printStackTrace();
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

    private List<NameValuePair> getRequestParams() {
        // TODO Enter Comments Here ...
        List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("firstname", edFirstName.getText().toString().trim()));
            params.add(new BasicNameValuePair("lastname", edLastName.getText().toString().trim()));
            params.add(new BasicNameValuePair("dob", selected_dob));
            params.add(new BasicNameValuePair("contact_no", edContactNo.getText().toString().trim()));
            params.add(new BasicNameValuePair("email_id", edEmailId.getText().toString().trim()));
            params.add(new BasicNameValuePair("password", edPass.getText().toString().trim()));
            if(rbFemale.isChecked()){
                params.add(new BasicNameValuePair("gender","Female"));
            }else{
                params.add(new BasicNameValuePair("gender","Male"));
            }

        return params;
    }

    private void handleResponse(JSONObject serverResponse) {
        int success = 0;
        try {
            success = serverResponse.getInt(Responce.TAG_SUCCESS);

            if (success == 1) {
                progress.dismiss();

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("cust_id", serverResponse.getString("CustomerId"));

                    editor.putString("cust_email_id", edEmailId.getText().toString().trim());
                editor.commit();
                Toast.makeText(RegisterActivity.this, serverResponse.getString((Responce.TAG_MESSAGE)), Toast.LENGTH_LONG).show();
//                if (Util.FlagItemsAddedInCart == 0) {
//                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//                    startActivity(intent);
//                } else {//if items are present in cart redirect user to the cart details
//                    Intent intent = new Intent(RegisterActivity.this, Cart_Details.class);
//                    startActivity(intent);
//                }
                finish();
            }
            else{
                    progress.dismiss();
                    Toast.makeText(RegisterActivity.this, serverResponse.getString(Responce.TAG_MESSAGE), Toast.LENGTH_LONG).show();
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
            default:
        }
    }

}
