package xpertrixitsolution.com.foodytreat.MenuItems;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

/**
 * @author Vrushali Matale, created on 17/10/2015
 *
 * last edited on 29/10/2015 , fb logout from program without using fb logout button
 */
public class MenuPolicies extends Activity implements View.OnClickListener {
    private String cust_id;
    TextView tvCorporateTieUp, tvDisclaimer, tvPrivacyPolicy, tvShippingPolicy,tvCorporateTieUp_text,tvDisclaimer_text;
    TextView tvPrivacyPolicy_text,tvShippingPolicy_text;
    private int Flag=0;
    private LinearLayout linearLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //super.onCreate();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policies);

        linearLayout=(LinearLayout)findViewById(R.id.linearPolicies);
        String screenSize= Util.getSizeName(this);
        if(screenSize.equals("HDPI")){
            linearLayout.setBackgroundResource(R.drawable.back_mdpi);
        }else if(screenSize.equals("LDPI")){
            linearLayout.setBackgroundResource(R.drawable.back_mdpi);
        }else if(screenSize.equals("XHDPI")){
            linearLayout.setBackgroundResource(R.drawable.back_xhdpi);
        }else if(screenSize.equals("XXHDPI")){
            linearLayout.setBackgroundResource(R.drawable.back_xxhdpi);
        }else{
            linearLayout.setBackgroundResource(R.drawable.back_mdpi);
        }

     getSharedPref();

        tvCorporateTieUp = (TextView) findViewById(R.id.tvCorporateTieUp);
        tvDisclaimer = (TextView) findViewById(R.id.tvDisclaimer);
        tvPrivacyPolicy = (TextView) findViewById(R.id.tvPrivacyPolicy);
        tvShippingPolicy = (TextView) findViewById(R.id.tvShippingPolicy);

        tvCorporateTieUp_text = (TextView) findViewById(R.id.tvCorporateTieUp_text);
        tvDisclaimer_text = (TextView) findViewById(R.id.tvDisclaimer_text);
        tvPrivacyPolicy_text = (TextView) findViewById(R.id.tvPrivacyPolicy_text);
        tvShippingPolicy_text = (TextView) findViewById(R.id.tvShippingPolicy_text);

        tvCorporateTieUp.setOnClickListener(this);
        tvDisclaimer.setOnClickListener(this);
        tvPrivacyPolicy.setOnClickListener(this);
        tvShippingPolicy.setOnClickListener(this);

        tvCorporateTieUp_text.setVisibility(View.GONE);
        tvDisclaimer_text.setVisibility(View.GONE);
        tvPrivacyPolicy_text.setVisibility(View.GONE);
        tvShippingPolicy_text.setVisibility(View.GONE);
    }
    public void getSharedPref(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        cust_id= sharedPreferences.getString("cust_id", "0");

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tvCorporateTieUp:
                if (Flag == 0) {
                    tvCorporateTieUp_text.setVisibility(View.VISIBLE);
                    Flag = 1;
                } else
                {
                    Flag = 0;
                    tvCorporateTieUp_text.setVisibility(View.GONE);
                }
                break;

            case R.id.tvDisclaimer:
                if (Flag == 0) {
                    tvDisclaimer_text.setVisibility(View.VISIBLE);
                    Flag = 1;
                } else
                {
                    Flag = 0;
                    tvDisclaimer_text.setVisibility(View.GONE);
                }
                break;

            case R.id.tvPrivacyPolicy:
                if (Flag == 0) {
                    tvPrivacyPolicy_text.setVisibility(View.VISIBLE);
                    Flag = 1;
                } else
                {
                    Flag = 0;
                    tvPrivacyPolicy_text.setVisibility(View.GONE);
                }
                break;

            case R.id.tvShippingPolicy:
                if (Flag == 0) {
                    tvShippingPolicy_text.setVisibility(View.VISIBLE);
                    Flag = 1;
                } else
                {
                    Flag = 0;
                    tvShippingPolicy_text.setVisibility(View.GONE);
                }
                break;

            default:
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
/** Inflating the current activity's menu with res/menu/items.xml */
        getMenuInflater().inflate(R.menu.menu_legal, menu);

        return super.onCreateOptionsMenu(menu);
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
                    Toast.makeText(MenuPolicies.this, "Logout successfull", Toast.LENGTH_SHORT).show();
                    editor.commit();
                    Intent inten = new Intent(MenuPolicies.this, LoginActivity.class);
                    startActivity(inten);
                }
                return true;
            case R.id.menu_viewCart:
                Intent intent = new Intent(MenuPolicies.this, Cart_Details.class);
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
                   Util.LoginAlert(this);
                }
                break;

            case R.id.menu_orderhistory:
                getSharedPref();
                if (!cust_id.equals("0")) {
                    Intent myIntent2 = new Intent(this,
                            MenuItemOrderHistory.class);
                    startActivity(myIntent2);
                } else {
                   Util.LoginAlert(this);                }
                break;

            case R.id.menu_about_us:
                Intent about_us = new Intent(this, About_us.class);
                startActivity(about_us);
                break;

            case R.id.menu_trackorder:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MenuPolicies.this);
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
                if (cust_id.equals("0")) {
                    Util.LoginAlert(this);
                } else {
                    Intent profile = new Intent(this, EditPersonalProfile.class);
                    startActivity(profile);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}