package xpertrixitsolution.com.foodytreat;
/**
 * @author Vrushali Matale, created on 18/09/2015
 * edited by : Dhiraj Devkar
 * last edited on 8/10/2015
 * last edited on 29/10/2015 by Vrushali Matale  */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import xpertrixitsolution.com.foodytreat.network.CompletionListener;
import xpertrixitsolution.com.foodytreat.network.NetworkTask;
import xpertrixitsolution.com.foodytreat.network.Post_URL;
import xpertrixitsolution.com.foodytreat.network.Responce;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;

import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.squareup.picasso.Downloader;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LoginActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, View.OnClickListener, CompletionListener
{

    private NetworkTask networkTask;
    private EditText edUsername, edPassword;
    private Button btnLoginEnter, btnSignUpHere;
    private TextView tvSkip,tvForgotPassword,tvLogin;
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton mSignInButton;
    private boolean mIntentInProgress;

    private ProgressDialog progress;
    private Dialog forgot_password;
    private String ForgotPasswordEmailId,GooglPlusEmail,googleGender,apiId,googleDob;
    private int FlagGoogleLogin=0,FlagFbLogin=0;
    private LinearLayout linearLLogin;

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private LoginButton btnFbLogin;

    private String fbFirstName,fbLastName;
    private String FlagLogoutFb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //*start new activity activity_main.xml/
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.login_user);
        FacebookSdk.sdkInitialize(this);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//to disable automatic opening of keyboard

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        FlagLogoutFb= sharedPreferences.getString("flagFbLogin", "0");
        if(FlagLogoutFb.equals("1")){
            LoginManager.getInstance().logOut();  // used to logout from facebook, vv imp if logout from program
        }

        initviews();
        String screenSize= Util.getSizeName(this);
        if(screenSize.equals("HDPI")){
            linearLLogin.setBackgroundResource(R.drawable.back_mdpi);
        }else if(screenSize.equals("LDPI")){
            linearLLogin.setBackgroundResource(R.drawable.back_mdpi);
        }else if(screenSize.equals("XHDPI")){
            linearLLogin.setBackgroundResource(R.drawable.back_xhdpi);
        }else if(screenSize.equals("XXHDPI")){
            linearLLogin.setBackgroundResource(R.drawable.back_xxhdpi);
        }else{
            linearLLogin.setBackgroundResource(R.drawable.back_mdpi);
        }

        //set typeface lobster
        Typeface myfont= Typeface.createFromAsset(getAssets(),"lobster.ttf");
        tvLogin.setTypeface(myfont);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();


        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                //displayMessage(newProfile);
//
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        btnFbLogin.setReadPermissions("public_profile");
       // btnFbLogin.registerCallback(callbackManager, callback);
        btnFbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.e("fb login result", loginResult.toString());
                AccessToken accessToken = loginResult.getAccessToken();
                apiId = accessToken.getUserId();
                Log.e("fb api id", apiId);

                login();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


    }

    private void initviews() {
        linearLLogin=(LinearLayout)findViewById(R.id.linearLLogin);
        edUsername = (EditText) findViewById(R.id.edUserName);
        edPassword = (EditText) findViewById(R.id.edPassword);
        btnLoginEnter = (Button) findViewById(R.id.btnLoginEnter);
        btnLoginEnter.setOnClickListener(this);
        btnSignUpHere = (Button) findViewById(R.id.btnSignUpHere);
        btnSignUpHere.setOnClickListener(this);
        tvSkip = (TextView) findViewById(R.id.tvSkip);
        tvSkip.setOnClickListener(this);
        tvLogin=(TextView)findViewById(R.id.tvLogin);
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(this);
        tvForgotPassword=(TextView)findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(this);
        btnFbLogin=(LoginButton)findViewById(R.id.btnFb_login);
        btnFbLogin.setOnClickListener(this);



    }


        @Override
        public void onClick(View v) {

            switch(v.getId()){

                case R.id.btnLoginEnter:
                    progress=new ProgressDialog(this);
                    progress.setMessage("Loading");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setIndeterminate(true);
                    progress.setProgress(0);
                    progress.show();
                    progress.setCanceledOnTouchOutside(false);
                    login();
                    break;
                case R.id.btnSignUpHere:
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    break;
                case R.id.tvSkip:
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setCancelable(false);
                    builder.setMessage("Unless you login, You will not be able to place any order!");
                    builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("Login Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    break;
                case R.id.sign_in_button:
                        //mSignInProgress = STATE_SIGN_IN;
                    mGoogleApiClient = new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(Plus.API)
                            .addScope(Plus.SCOPE_PLUS_LOGIN)
                            .addScope(Plus.SCOPE_PLUS_PROFILE)
                            .build();

                        mGoogleApiClient.connect();

                    break;
                case R.id.tvForgotPassword:
                        forgotpassword();
                    break;
                case R.id.btnFb_login:
                    FlagFbLogin=1;
                    accessTokenTracker.startTracking();
                    profileTracker.startTracking();
                    btnFbLogin.setReadPermissions("public_profile");
//                    btnFbLogin.registerCallback(callbackManager, callback);
                    // Callback registration


                    break;
                default: break;
            }
        }


    private void login() {
        String Email = edUsername.getText().toString();
        String Password = edPassword.getText().toString();
        if(FlagGoogleLogin==0&&FlagFbLogin==0) {
            boolean emailcheck = checkEmail(Email);
            if (!emailcheck) {
                progress.dismiss();
                edUsername.setError("Email Id is not valid");
            } else if (Email.length() == 0) {
                edUsername.requestFocus();
                progress.dismiss();
                edUsername.setError("FIELD CANNOT BE EMPTY");
            } else if (Password.length() == 0) {
                edPassword.requestFocus();
                progress.dismiss();
                edPassword.setError("FIELD CANNOT BE EMPTY");
            } else {
                networkTask = new NetworkTask(this, this, false);
                /**
                 * Network Work
                 */
                List<NameValuePair> params = getRequestParams();
                networkTask.execute(params, Post_URL.URL_LOGIN, 1);
            }
        }else{//if google plus login is used then no need of validation

            progress=new ProgressDialog(this);
            progress.setMessage("Loading");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
            progress.setCanceledOnTouchOutside(false);
            networkTask = new NetworkTask(this, this, false);
            List<NameValuePair> params = getRequestParams();
            networkTask.execute(params, Post_URL.URL_API_LOGIN, 3);
        }
    }

    //webservice
    /** start webservices to send forget password  -by Vrushali added on 12/10/2015 */
    private void getforgetpassword() {
        networkTask = new NetworkTask(this, this, false);
        List<NameValuePair> params = getRequestParamsForgetPassword();
        networkTask.execute(params, Post_URL.URL_GET_FORGET_PASSWORD, 2);
    }

    private List<NameValuePair> getRequestParams() {
    // TODO Enter Comments Here ...
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if(FlagGoogleLogin==0&&FlagFbLogin==0) {
            params.add(new BasicNameValuePair("email_id", edUsername.getText().toString().trim()));
            params.add(new BasicNameValuePair("password", edPassword.getText().toString().trim()));

        }else if(FlagGoogleLogin==1){
            params.add(new BasicNameValuePair("first_name", ""));
            params.add(new BasicNameValuePair("last_name", ""));
            params.add(new BasicNameValuePair("dob", ""));
            params.add(new BasicNameValuePair("contact_no",""));
            params.add(new BasicNameValuePair("email_id", GooglPlusEmail));
            params.add(new BasicNameValuePair("api_id",apiId));
            if(googleGender=="0"){
                params.add(new BasicNameValuePair("gender","Male"));
            }else{
                params.add(new BasicNameValuePair("gender","Female"));
            }
        }else if(FlagFbLogin==1){
            if(fbFirstName!=null&&fbLastName!=null) {
                params.add(new BasicNameValuePair("first_name", fbFirstName));
                params.add(new BasicNameValuePair("last_name", fbLastName));
                params.add(new BasicNameValuePair("dob", ""));
                params.add(new BasicNameValuePair("contact_no",""));
                params.add(new BasicNameValuePair("email_id",""));
                params.add(new BasicNameValuePair("gender",""));

            }
            params.add(new BasicNameValuePair("api_id",apiId));
        }
                return params;
    }


    private List<NameValuePair> getRequestParamsForgetPassword() {
        // TODO Enter Comments Here ...
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email_id", ForgotPasswordEmailId));
        return params;
    }

    private void handleResponse(JSONObject serverResponse) {
        int success = 0;
        try {
            success = serverResponse.getInt(Responce.TAG_SUCCESS);
                    if (success == 1) {
                        progress.dismiss();
                        //after login we want to store user's id into shared preferences
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("cust_id", serverResponse.getString("CustomerId"));
                        editor.commit();

                        Toast.makeText(LoginActivity.this, serverResponse.getString((Responce.TAG_MESSAGE)), Toast.LENGTH_LONG).show();
                        if(Util.FlagComingFromActivity==0) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }else {//if items are present in cart redirect user to the cart details
                            finish();
                        }
                    } else {
                        progress.dismiss();
                        //Util.log(TAG, serverResponse.getString(Responce.TAG_ERROR));
                        Toast.makeText(LoginActivity.this, serverResponse.getString(Responce.TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
            } catch (JSONException e) {
                    // Util.log(TAG, e.getMessage());
                }

        }

    private void handleResponseForgetPassword(JSONObject serverResponse) {
        int success = 0;
        try {
            success = serverResponse.getInt(Responce.TAG_SUCCESS);

            if (success == 1) {
                Toast.makeText(LoginActivity.this, serverResponse.getString((Responce.TAG_MESSAGE)), Toast.LENGTH_LONG).show();
                forgot_password.dismiss();
            } else {
                Toast.makeText(LoginActivity.this, serverResponse.getString(Responce.TAG_MESSAGE), Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {

        }

    }

    private void handleResponseApiLogin(JSONObject serverResponse) {
        int success = 0;
        try {
            success = serverResponse.getInt(Responce.TAG_SUCCESS);
            if (success == 1) {
                progress.dismiss();
                //after login we want to store user's id into shared preferences
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("cust_id", serverResponse.getString("CustomerId"));
                if(FlagFbLogin==1){
                    editor.putString("flagFbLogin","1" );
                }
                editor.commit();

                Toast.makeText(LoginActivity.this, serverResponse.getString((Responce.TAG_MESSAGE)), Toast.LENGTH_LONG).show();
                if(Util.FlagComingFromActivity==0) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{//if items are present in cart redirect user to the cart details
                    finish();                }
            } else {
                progress.dismiss();
                Toast.makeText(LoginActivity.this, serverResponse.getString(Responce.TAG_MESSAGE), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            // Util.log(TAG, e.getMessage());
        }

    }

    @Override
    public void onComplete(JSONObject serverResponse, int RESPONSE_IDENTIFIER_FLAG) throws JSONException {
        switch (RESPONSE_IDENTIFIER_FLAG) {
            case 1:
                handleResponse(serverResponse);
                break;
            case 2:
                handleResponseForgetPassword(serverResponse);
                break;
            case 3:
                handleResponseApiLogin(serverResponse);
                    default:
        }

            }


    private void forgotpassword() {
        forgot_password = new Dialog(LoginActivity.this);
        forgot_password.setContentView(R.layout.forgot_password);

        final EditText address = (EditText) forgot_password.findViewById(R.id.edEmailIdForgotPassword);
        Button send_password = (Button) forgot_password.findViewById(R.id.btnSend_password);
        forgot_password.setTitle("Forgot Password");
        send_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPasswordEmailId = address.getText().toString();
                boolean email_check = checkEmail(ForgotPasswordEmailId);

                if (!email_check) {
                    address.setError("Email Id is not valid");
                } else {
                    getforgetpassword();
                }
            }
        });
        forgot_password.show();

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                finishAffinity();//to kill app completely with finish u can only close the current screen
            }
        });
        builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
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

    /**
     * methods for google plus login
     */
    protected void onStart() {
                super.onStart();

            }

            protected void onStop() {
                super.onStop();
                if(FlagFbLogin==1){
                    Log.e("fb on stop","fb on stop");
                    accessTokenTracker.stopTracking();
                    profileTracker.stopTracking();
                }else {
                    if (mGoogleApiClient.isConnected()) {
                        mGoogleApiClient.disconnect();
                    }
                }
            }

            public void onConnectionFailed(ConnectionResult result) {
                if (!mIntentInProgress && result.hasResolution()) {
                    try {
                        mIntentInProgress = true;
                        startIntentSenderForResult(result.getResolution().getIntentSender(),
                                RC_SIGN_IN, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        // The intent was canceled before it was sent.  Return to the default
                        // state and attempt to connect to get an updated ConnectionResult.
                        mIntentInProgress = false;
                        mGoogleApiClient = new GoogleApiClient.Builder(this)
                                .addConnectionCallbacks(this)
                                .addOnConnectionFailedListener(this)
                                .addApi(Plus.API)
                                .addScope(Plus.SCOPE_PLUS_LOGIN)
                                .addScope(Plus.SCOPE_PLUS_PROFILE)
                                .build();

                        mGoogleApiClient.connect();
                    }
                }
            }

            public void onConnected(Bundle connectionHint) {
                // We've resolved any connection errors.  mGoogleApiClient can be used to
                // access Google APIs on behalf of the user.
                if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                    Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                    //String personName = currentPerson.getDisplayName();
                    apiId=currentPerson.getId();
                    GooglPlusEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
                    googleGender= String.valueOf(currentPerson.getGender());
                    FlagGoogleLogin=1;
                    login();
                }
            }
            protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
                if (FlagFbLogin == 1) {
//                    accessTokenTracker.startTracking();
//                    profileTracker.startTracking();
//                    btnFbLogin.setReadPermissions("public_profile");
//                    btnFbLogin.registerCallback(callbackManager, callback);
                    super.onActivityResult(requestCode, responseCode, intent);
                    callbackManager.onActivityResult(requestCode, responseCode, intent);

                } else {
                    if (requestCode == RC_SIGN_IN) {
                        mIntentInProgress = false;

                        if (!mGoogleApiClient.isConnecting()) {
                            mGoogleApiClient = new GoogleApiClient.Builder(this)
                                    .addConnectionCallbacks(this)
                                    .addOnConnectionFailedListener(this)
                                    .addApi(Plus.API)
                                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                                    .addScope(Plus.SCOPE_PLUS_PROFILE)
                                    .build();

                            mGoogleApiClient.connect();
                        }
                    }
                }
            }
            public void onConnectionSuspended(int cause) {
                mGoogleApiClient.connect();
            }
//
//    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
//        @Override
//        public void onSuccess(LoginResult loginResult) {
//            AccessToken accessToken = loginResult.getAccessToken();
//
//            Profile profile = Profile.getCurrentProfile();
//
/////* make the API call */
////            new GraphRequest(
////                    AccessToken.getCurrentAccessToken(),
////                    "/{user-id}",
////                    null,
////                    HttpMethod.GET,
////                    new GraphRequest.Callback() {
////                        public void onCompleted(GraphResponse response) {
////            /* handle the result */
////                        }
////                    }
////            ).executeAsync();
//
//
////
////                fbFirstName = profile.getFirstName();
////                fbLastName = profile.getLastName();
////                //Log.e("dob",profile.get);
////                Log.e("fb first name",fbFirstName);
////                Log.e("fb last name",fbLastName);
////                apiId=profile.getId();
////                Log.e("fb api id",apiId);
////                login();
//
//        }
//
//        @Override
//        public void onCancel() {
//            Log.e("cancle fb","cancle fb");
//        }
//
//        @Override
//        public void onError(FacebookException e) {
//            Log.e("error fb", String.valueOf(e));
//        }
//    };

}
