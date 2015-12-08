package xpertrixitsolution.com.foodytreat;
/**
 * @author Vrushali Matale, created on 18/09/2015
 * edited by: Dhiraj Devkar
 *
 * last edited on 29/10/2015 , fb logout from program without using fb logout button
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookActivity;
import com.facebook.login.LoginManager;

import xpertrixitsolution.com.foodytreat.MenuItems.About_us;
import xpertrixitsolution.com.foodytreat.MenuItems.EditPersonalProfile;
import xpertrixitsolution.com.foodytreat.MenuItems.MenuFAQ;
import xpertrixitsolution.com.foodytreat.MenuItems.MenuItemOrderHistory;
import xpertrixitsolution.com.foodytreat.MenuItems.MenuPolicies;
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


public class MainActivity extends Activity implements CompletionListener,View.OnClickListener {

    private NetworkTask networkTask;
    ImageView g1, g2, g3;
    AutoCompleteTextView edEnterAreaName;
    Button btnGo;
    TextView tv,tvSteps,tvHomeScreenAnim1,tvHomeScreenAnim2,tvHomeScreenAnim3;
    Animation animation2,animation3;

    ArrayList<String> AreaNameArrayList = new ArrayList<String>();
    ArrayList<String> AreaIdList = new ArrayList<String>();
    private ProgressDialog progress;
    private String cust_id,cust_email;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        boolean isNetAvail=isNetworkAvailable();

        if(!isNetAvail) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(false);
            builder.setMessage("Unable to connect with server. Check your internet connection and try again.");
            builder.setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }else {
            getSharedPref();
            //start progress bar
            progress = new ProgressDialog(this);
            progress.setMessage("Loading");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progress.setIndeterminateDrawable(getResources().getDrawable(R.drawable.fav));
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
            progress.setCanceledOnTouchOutside(false);
            getAreaList();

            //set typeface lobster
            Typeface myfont = Typeface.createFromAsset(getAssets(), "lobster.ttf");
            tv.setTypeface(myfont);
            tvSteps.setTypeface(myfont);
            edEnterAreaName.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    edEnterAreaName.showDropDown();
                    return false;
                }
            });
            edEnterAreaName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Util.SelectedAreaName = String.valueOf(edEnterAreaName.getText());
                    if (AreaNameArrayList.contains(Util.SelectedAreaName)) {
                        //get pos of area entered from array
                        int pos = AreaNameArrayList.indexOf(Util.SelectedAreaName);
                        //use area pos to get corrosponding area id
                        Util.SelectedAreaId = Integer.parseInt(AreaIdList.get(pos));
                        Log.e("Selected area id", String.valueOf(Util.SelectedAreaId));

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edEnterAreaName.getWindowToken(), 0);//close keypad before opening new intent

                        Intent myIntent = new Intent(MainActivity.this,
                                SelectVendor.class);
                        startActivity(myIntent);

                    }
                }
            });

        }

    }

    public void getSharedPref(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        cust_id= sharedPreferences.getString("cust_id", "0");

    }
    private void initviews() {
        scrollView=(ScrollView)findViewById(R.id.scrollViewMainAct);
        g1 =(ImageView)findViewById(R.id.g1);
        g1.setOnClickListener(this);
        g2 =(ImageView)findViewById(R.id.g2);
        g2.setOnClickListener(this);
        g3 =(ImageView)findViewById(R.id.g3);
        g3.setOnClickListener(this);

        tv=(TextView)findViewById(R.id.tv);
        tvSteps=(TextView)findViewById(R.id.tvSteps);
        animation2 = AnimationUtils.loadAnimation(this, R.anim.clock_turn);
        animation3 = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        btnGo = (Button)findViewById(R.id.btnGo);
        btnGo.setOnClickListener(this);
        edEnterAreaName = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
    }

    public void startAnimation(){
        //set animations
        g1.startAnimation(animation2);
        g2.startAnimation(animation2);
        g3.startAnimation(animation2);
    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.btnGo:
                Util.SelectedAreaName = String.valueOf(edEnterAreaName.getText());
                if (Util.SelectedAreaName.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter area name!", Toast.LENGTH_SHORT).show();

                } else if (AreaNameArrayList.contains(Util.SelectedAreaName)) {
                    //get pos of area entered from array
                    int pos = AreaNameArrayList.indexOf(Util.SelectedAreaName);
                    //use area pos to get corrosponding area id
                    Util.SelectedAreaId = Integer.parseInt(AreaIdList.get(pos));
                    Log.e("Selected area id", String.valueOf(Util.SelectedAreaId));

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edEnterAreaName.getWindowToken(), 0);//close keypad before opening new intent

                    Intent myIntent = new Intent(MainActivity.this,
                            SelectVendor.class);
                    startActivity(myIntent);

                } else {
                    Toast.makeText(MainActivity.this, "Sorry we dont deliver in this area yet!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.g1:
                startAnimation();
                break;
            case R.id.g2:
                startAnimation();
                break;
            case R.id.g3:
                startAnimation();
                break;
            default: break;
        }
    }
    public void getAreaList(){
        networkTask = new NetworkTask(this, this, false);
        List<NameValuePair> params = getRequestParams();
        networkTask.execute(params, Post_URL.URL_GET_AREA_LIST, 1);
    }

    private List<NameValuePair> getRequestParams() {
        // TODO Enter Comments Here ...
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("get area", "get area"));

        return params;
    }

    private void handleResponse(JSONObject serverResponse) {
        int success = 0;

        try {
            success = serverResponse.getInt(Responce.TAG_SUCCESS);
            if (success == 1) {
                JSONArray jsonArray = serverResponse.getJSONArray("arealist");
                for (int i = 0; i < jsonArray.length(); i++) {
                    AreaNameArrayList.add(jsonArray.getJSONObject(i).getString("area"));
                    AreaIdList.add(jsonArray.getJSONObject(i).getString("area_id"));
                }
               Log.e("area name list", String.valueOf(AreaNameArrayList));
                Log.e("area id list", String.valueOf(AreaIdList));

               progress.dismiss();
                startAnimation();
                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, AreaNameArrayList);
                edEnterAreaName.setAdapter(adapter);
                edEnterAreaName.setThreshold(1);

            } else {
                progress.dismiss();
                startAnimation();
                Toast.makeText(MainActivity.this, serverResponse.getString(Responce.TAG_MESSAGE), Toast.LENGTH_LONG).show();
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
            default:

        }

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
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
/** Inflating the current activity's menu with res/menu/items.xml */
        getMenuInflater().inflate(R.menu.menu_home_page, menu);


        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_faq:
                Intent menu_faq = new Intent(this, MenuFAQ.class);
                startActivity(menu_faq);
                break;
            case R.id.menu_logout:
                getSharedPref();
                if(cust_id.equals("0")){

                }else {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("cust_id", "0");
                    editor.putString("cust_email_id", "");

                    Toast.makeText(MainActivity.this, "Logout successfull", Toast.LENGTH_SHORT).show();
                    editor.commit();
                    Intent inten = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(inten);
                }
                return true;
            case R.id.menu_viewCart:
                Intent intent =new Intent(MainActivity.this,Cart_Details.class);
                startActivity(intent);
                return true;

            case R.id.menu_wishlist:
                getSharedPref();
                if(!cust_id.equals("0")) {
                    Intent myIntent1 = new Intent(this,
                            SelectCake.class);

                    myIntent1.putExtra("coming_from", "Coming from wishlist");
                    startActivity(myIntent1);
                }else{
                        Util.LoginAlert(this);
                }
                break;

            case R.id.menu_orderhistory:
                getSharedPref();
                if(!cust_id.equals("0")) {
                    Intent myIntent2 = new Intent(this,
                            MenuItemOrderHistory.class);
                    startActivity(myIntent2);
                }else{
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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
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
            case R.id.menu_email_id:
                getSharedPref();
                if(cust_id.equals("0")){
                    Util.LoginAlert(this);
                }else {
                    Intent profile = new Intent(this, EditPersonalProfile.class);
                    startActivity(profile);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}