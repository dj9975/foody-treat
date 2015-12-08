package xpertrixitsolution.com.foodytreat;
/**
 * @author Dhiraj Devkar
 * last edited: 16/10/2015
 *
 * last edited on 29/10/2015 , fb logout from program without using fb logout button
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import xpertrixitsolution.com.foodytreat.MenuItems.About_us;
import xpertrixitsolution.com.foodytreat.MenuItems.EditPersonalProfile;
import xpertrixitsolution.com.foodytreat.MenuItems.MenuFAQ;
import xpertrixitsolution.com.foodytreat.MenuItems.MenuItemOrderHistory;
import xpertrixitsolution.com.foodytreat.MenuItems.MenuPolicies;

public class Generate_Bill extends Activity {
    private String cust_id;
    private TextView tvBillOrderId,tvBillPrice,tvBillShipCharge,tvBillCustomerName,tvBillContactNo,tvBillAmount,tvBillEmail;
    private Button btnContinueShopping;
    private LinearLayout layoutBill;
    private Animation animation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate__bill);

     getSharedPref();

        initviews();

        Util.CakeNameArrayList.clear();
        Util.CakeRateList.clear();
        Util.CakeWeightList.clear();
        Util.CakeQntList.clear();
        Util.CakeTotalList.clear();
        Util.CakeIdList.clear();
        Util.CakePicList.clear();
        Util.CakeShippingChargeList.clear();
        Util.CakeDeliveryDateList.clear();
        Util.CakeDeliveryTimeList.clear();
        Util.CakeMsgList.clear();

        layoutBill.setAnimation(animation);
        tvBillOrderId.setText(Util.bill_order_id);
        tvBillCustomerName.setText(Util.bill_customer_name);
        tvBillContactNo.setText(Util.bill_contact_no);
        tvBillPrice.setText("₹"+Util.GrandTotal);
        tvBillShipCharge.setText("₹"+Util.grandShipCharge);
        tvBillAmount.setText("₹"+String.valueOf(Util.bill_amount));
        new TaskExample(Util.bill_contact_no,"order").execute();

        tvBillEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                String[] recipients = new String[]{"contactus@foodytreat.com"};
                email.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
                //email.putExtra(Intent.EXTRA_EMAIL, new String[{"mvrushali03@gmail.com"}]);
                email.putExtra(Intent.EXTRA_SUBJECT, "Order Assistance for android app");
                //email.putExtra(Intent.EXTRA_TEXT, "message");
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });
        btnContinueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Generate_Bill.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    public void getSharedPref(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        cust_id= sharedPreferences.getString("cust_id", "0");

    }
    private void initviews() {
        layoutBill=(LinearLayout)findViewById(R.id.layoutBill);
        animation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        tvBillAmount=(TextView)findViewById(R.id.tvBillAmount);
        tvBillPrice=(TextView)findViewById(R.id.tvBillPrice);
        tvBillShipCharge=(TextView)findViewById(R.id.tvBillShipCharge);
        tvBillOrderId=(TextView)findViewById(R.id.tvBillOrderId);
        tvBillCustomerName=(TextView)findViewById(R.id.tvBillCustomerName);
        tvBillContactNo=(TextView)findViewById(R.id.tvBillContactNo);
        btnContinueShopping=(Button)findViewById(R.id.btnBillPlaceOrder);
        tvBillEmail=(TextView)findViewById(R.id.tvBillEmail);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_generate__bill, menu);
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
                    Toast.makeText(Generate_Bill.this, "Logout successfull", Toast.LENGTH_SHORT).show();
                    editor.commit();
                    Intent inten = new Intent(Generate_Bill.this, LoginActivity.class);
                    startActivity(inten);

                    LoginManager.getInstance().logOut();// used to logout from facebook, vv imp if logout from program
                }
                return true;
            case R.id.menu_viewCart:
                Intent intent = new Intent(Generate_Bill.this, Cart_Details.class);
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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Generate_Bill.this);
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
