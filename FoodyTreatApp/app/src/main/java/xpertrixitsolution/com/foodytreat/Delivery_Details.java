package xpertrixitsolution.com.foodytreat;
/**
 * @author Vrushali Matale, created on 03/10/2015
 *
 * last edited on 29/10/2015 , fb logout from program without using fb logout button
 *
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Delivery_Details extends Activity implements View.OnClickListener,CompletionListener {

    private Button btnSelectTime,btnPlaceOrder,btnCakeMsg,btnSaveCakeMsg;
    public static Button btnEnterAddress;
    private String delMode,Address,CurrentDate,SelectedDate;
    private TextView tvDisplayDate;
    private int myear;
    private int mmonth;
    private int mday,mHour,otp;
    private int FlagAddressEntered=0,flagValidationDone=0;
    private NetworkTask networkTask;
    private String cust_id,SelectedPaymentMode,mobileno,CakeMsg;
    private EditText edBillFirstName,edBillLastName,edBillEmailId,edBillContactNo;
    private  ProgressDialog progress;

    private RadioButton rbNetBanking,rbCod,rbSwipeCard;
    private int grandWeight,grandshippingCharge=0;
    private JSONArray jsonArray;
    private JSONObject jsonRecord;

    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_details);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//to disable automatic opening of keyboard

        getSharedPref();
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

        progress=new ProgressDialog(this);
        progress.setMessage("Loading");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
        progress.setCanceledOnTouchOutside(false);

        getPersonalDetails();


        int i;
        jsonArray=new JSONArray();
        try {
             for(i=0;i<Util.CakeNameArrayList.size();i++){
                jsonRecord = new JSONObject();
                 jsonRecord.put("cake_id",Util.CakeIdList.get(i));
                 jsonRecord.put("cake_name",Util.CakeNameArrayList.get(i));
                jsonRecord.put("quantity",Util.CakeQntList.get(i));
                jsonRecord.put("price", Util.CakeRateList.get(i));
                jsonRecord.put("total",Integer.parseInt(Util.CakeQntList.get(i))*Integer.parseInt(Util.CakeRateList.get(i)));
                 jsonRecord.put("delivery_date", Util.CakeDeliveryDateList.get(i));
                 jsonRecord.put("delivery_time", Util.CakeDeliveryTimeList.get(i));
                 jsonRecord.put("msg", Util.CakeMsgList.get(i));
                 jsonRecord.put("shipping_charge", Util.CakeShippingChargeList.get(i));
                 jsonArray.put(jsonRecord);
                 grandshippingCharge= grandshippingCharge+ Integer.parseInt(Util.CakeShippingChargeList.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Util.grandShipCharge=grandshippingCharge;
    }

    public void getSharedPref(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        cust_id= sharedPreferences.getString("cust_id", "0");

    }
    private void initviews() {
        scrollView=(ScrollView)findViewById(R.id.scrollViewDeliveryDetails);
        btnEnterAddress=(Button)findViewById(R.id.btnEnterAddress);
        btnEnterAddress.setOnClickListener(this);
        edBillFirstName=(EditText)findViewById(R.id.edBillFirstName);
        edBillLastName=(EditText)findViewById(R.id.edBillLastName);
        edBillEmailId=(EditText)findViewById(R.id.edBillEmailId);
        edBillContactNo=(EditText)findViewById(R.id.edBillContactNo);
        btnPlaceOrder=(Button)findViewById(R.id.btnPlaceOrder);
        btnPlaceOrder.setOnClickListener(this);
        rbNetBanking=(RadioButton)findViewById(R.id.rbNetBanking);
        rbCod=(RadioButton)findViewById(R.id.rbCod);
        rbSwipeCard=(RadioButton)findViewById(R.id.rbSwipeCard);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnEnterAddress:
                        if(Address.isEmpty()) {
                            FlagAddressEntered = 1;
                            Intent intent = new Intent(Delivery_Details.this, AddressDetails.class);
                            startActivity(intent);
                            btnEnterAddress.setBackgroundResource(R.drawable.button_selected_border);
                        }else{
                            final Dialog savedAddress = new Dialog(Delivery_Details.this);
                            savedAddress.setContentView(R.layout.popup_saved_address);
                            savedAddress.setTitle("Your Address");

                            final Button btnUseSavedAddress=(Button)savedAddress.findViewById(R.id.btnUseSavedAddress);
                            final TextView tvSavedAddress=(TextView) savedAddress.findViewById(R.id.tvSavedAddress);
                            final TextView tvAddNew=(TextView) savedAddress.findViewById(R.id.tvAddNew);
                            tvSavedAddress.setText(Address);
                            btnUseSavedAddress.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Util.BillAddress = Address;
                                    FlagAddressEntered=1;
                                    savedAddress.dismiss();
                                    btnEnterAddress.setBackgroundResource(R.drawable.button_selected_border);
                                    btnEnterAddress.setText(Address);
                                }
                            });
                            tvAddNew.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FlagAddressEntered = 1;
                                    savedAddress.dismiss();
                                    btnEnterAddress.setBackgroundResource(R.drawable.button_selected_border);
                                    Intent intent = new Intent(Delivery_Details.this, AddressDetails.class);
                                    startActivity(intent);

                                }
                            });
                            savedAddress.show();
                        }
                break;
            case R.id.btnPlaceOrder:
                validate();
                getPaymentMethodSelected();

                if(flagValidationDone==1&&FlagAddressEntered==1){
                    new TaskExample(mobileno,"otp").execute();
                    progress.dismiss();
                    final Dialog dialogOTP = new Dialog(Delivery_Details.this);
                    dialogOTP.setContentView(R.layout.otp_popup);
                    final EditText edEnteredOtp = (EditText) dialogOTP.findViewById(R.id.edOtp);

                    Button btnConfirmOtp = (Button) dialogOTP.findViewById(R.id.btnConfirmOtp);
                    dialogOTP.setTitle("OTP");
                    //edEnteredOtp.setText(String.valueOf(otp));
                    btnConfirmOtp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String enteredotp = edEnteredOtp.getText().toString();
                            if(enteredotp.isEmpty()){
                                edEnteredOtp.setError("Please enter OTP.");
                            }else if (Integer.valueOf(enteredotp) == Util.otp) {//if entered otp is same as generated otp
                                dialogOTP.dismiss();
                                        progress = new ProgressDialog(Delivery_Details.this);
                                        progress.setMessage("Loading");
                                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                        progress.setIndeterminate(true);
                                        progress.setProgress(0);
                                        progress.show();
                                        progress.setCanceledOnTouchOutside(false);
                                        //  method to place order
                                        sendOrder();

                            } else {
                                edEnteredOtp.setError("Please enter correct OTP.");
                            }
                        }
                    });
                    dialogOTP.show();
                }else if(FlagAddressEntered==0){
                    btnEnterAddress.setError("Please enter address");
                }
                break;
            default:
                break;
        }
    }

    private void getPaymentMethodSelected() {
        if(rbNetBanking.isChecked()){
            SelectedPaymentMode=rbNetBanking.getText().toString();
        }else if(rbSwipeCard.isChecked()){
            SelectedPaymentMode=rbSwipeCard.getText().toString();
        }else{
            SelectedPaymentMode=rbCod.getText().toString();
        }
    }



    public void getPersonalDetails(){//get personal details if available
        networkTask = new NetworkTask(this, this, false);
        List<NameValuePair> params = getRequestParams();
        networkTask.execute(params, Post_URL.URL_GET_PERSONAL_DETAILS, 1);
    }

    public void sendOrder(){//place order
        networkTask = new NetworkTask(this, this, false);
        List<NameValuePair> params = getRequestParamsSendOrders();
        networkTask.execute(params, Post_URL.URL_SEND_ORDER, 2);
    }

    private List<NameValuePair> getRequestParams() {
        // TODO Enter Comments Here ...
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", cust_id));
        return params;
    }

    private List<NameValuePair> getRequestParamsSendOrders() {
        // TODO Enter Comments Here ...
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", cust_id));
        params.add(new BasicNameValuePair("area_id",String.valueOf(Util.SelectedAreaId)));
        params.add(new BasicNameValuePair("area_name",Util.SelectedAreaName));

        Util.bill_customer_name=edBillFirstName.getText().toString()+" "+edBillLastName.getText().toString();
        params.add(new BasicNameValuePair("first_name",edBillFirstName.getText().toString()));
        params.add(new BasicNameValuePair("last_name",edBillLastName.getText().toString()));

        Util.bill_contact_no=edBillContactNo.getText().toString();
        params.add(new BasicNameValuePair("contact_no",edBillContactNo.getText().toString()));

        params.add(new BasicNameValuePair("email_id",edBillEmailId.getText().toString()));
        params.add(new BasicNameValuePair("payment_method",SelectedPaymentMode));

//        Util.bill_delivery_date=selected_date;
//        params.add(new BasicNameValuePair("delivery_date",selected_date));

//        Util.bill_delivery_time=btnSelectTime.getText().toString();
//        params.add(new BasicNameValuePair("delivery_time",btnSelectTime.getText().toString()));

        params.add(new BasicNameValuePair("city","Pune"));
        params.add(new BasicNameValuePair("state","Maharashtra"));
        params.add(new BasicNameValuePair("billing_address",Util.BillAddress));
        //params.add(new BasicNameValuePair("cake_msg",CakeMsg));
        if(Util.FlagShippAddSame==0){
            params.add(new BasicNameValuePair("shipping_address",Util.ShippAddress));
        }else{
            params.add(new BasicNameValuePair("shipping_address",Util.BillAddress));
        }
        params.add(new BasicNameValuePair("cart_items",jsonArray.toString()));
        params.add(new BasicNameValuePair("final_total",String.valueOf(Util.GrandTotal)));
        params.add(new BasicNameValuePair("shipping_charges",String.valueOf(grandshippingCharge)));
        int final_due=Util.GrandTotal+grandshippingCharge;
        Util.bill_amount=final_due;
        params.add(new BasicNameValuePair("final_due",String.valueOf(final_due)));
        return params;
    }

    private void handleResponse(JSONObject serverResponse) {
        int success = 0;

        try {
            success = serverResponse.getInt(Responce.TAG_SUCCESS);
            if (success == 1) {
                JSONArray jsonArray = serverResponse.getJSONArray("customer_detail");
                for (int i = 0; i < jsonArray.length(); i++) {
                    edBillFirstName.setText(jsonArray.getJSONObject(i).getString("firstname"));
                    edBillLastName.setText(jsonArray.getJSONObject(i).getString("lastname"));
                    edBillContactNo.setText(jsonArray.getJSONObject(i).getString("contact_no"));
                    edBillEmailId.setText(jsonArray.getJSONObject(i).getString("email"));
                    Address=jsonArray.getJSONObject(i).getString("address");
                }
                progress.dismiss();
            } else {
                progress.dismiss();
            }
        } catch (JSONException e) {

        }
    }

    private void handleResponseSendOrder(JSONObject serverResponse) {
        int success = 0;

        try {
            success = serverResponse.getInt(Responce.TAG_SUCCESS);
            if (success == 1) {
                Util.bill_order_id=serverResponse.getString("order_id");
//                Toast.makeText(Delivery_Details.this,serverResponse.getString("order_id"),Toast.LENGTH_SHORT).show();
                progress.dismiss();
                Intent intent= new Intent(Delivery_Details.this,Generate_Bill.class);
                startActivity(intent);
            } else {
                progress.dismiss();
                Toast.makeText(Delivery_Details.this,"Something went wrong. Please try again!",Toast.LENGTH_SHORT).show();
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
                handleResponseSendOrder(serverResponse);
                break;
            default:
        }
    }

    public void validate(){
        String firstname= edBillFirstName.getText().toString();
        String lasname= edBillLastName.getText().toString();
        mobileno=edBillContactNo.getText().toString();
        String email=edBillEmailId.getText().toString();
        if (firstname.length() == 0) {
            progress.dismiss();
            edBillFirstName.requestFocus();
            edBillFirstName.setError("FIELD CANNOT BE EMPTY");
        } else if (lasname.length() == 0) {
            progress.dismiss();
            edBillLastName.requestFocus();
            edBillLastName.setError("FIELD CANNOT BE EMPTY");
        } else if (mobileno.length() == 0) {
            progress.dismiss();
            edBillContactNo.requestFocus();
            edBillContactNo.setError("FIELD CANNOT BE EMPTY");
        } else if (email.length() == 0) {
            progress.dismiss();
            edBillEmailId.requestFocus();
            edBillEmailId.setError("FIELD CANNOT BE EMPTY");
        }else{
            flagValidationDone=1;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_delivery_details, menu);
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
                    Toast.makeText(Delivery_Details.this, "Logout successfull", Toast.LENGTH_SHORT).show();
                    editor.commit();
                    Intent inten = new Intent(Delivery_Details.this, LoginActivity.class);
                    startActivity(inten);

                }
                return true;
            case R.id.menu_viewCart:
                Intent intent = new Intent(Delivery_Details.this, Cart_Details.class);
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

            case R.id.menu_legal:
                Intent legal = new Intent(this, MenuPolicies.class);
                startActivity(legal);
                break;

            case R.id.menu_about_us:
                Intent about_us = new Intent(this, About_us.class);
                startActivity(about_us);
                break;

            case R.id.menu_trackorder:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Delivery_Details.this);
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
