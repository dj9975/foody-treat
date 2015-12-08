package xpertrixitsolution.com.foodytreat;
/**
 * @author Vrushali Matale, created on 18/09/2015
 * edited by: Dhiraj Devkar
 *
 * last edited on 29/10/2015 , fb logout from program without using fb logout button
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import xpertrixitsolution.com.foodytreat.MenuItems.About_us;
import xpertrixitsolution.com.foodytreat.MenuItems.EditPersonalProfile;
import xpertrixitsolution.com.foodytreat.MenuItems.MenuFAQ;
import xpertrixitsolution.com.foodytreat.MenuItems.MenuItemOrderHistory;
import xpertrixitsolution.com.foodytreat.MenuItems.MenuPolicies;
import xpertrixitsolution.com.foodytreat.network.CompletionListener;
import xpertrixitsolution.com.foodytreat.network.NetworkTask;
import xpertrixitsolution.com.foodytreat.network.Post_URL;
import xpertrixitsolution.com.foodytreat.network.Responce;

import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Cake_Details extends Activity implements View.OnClickListener,CompletionListener {

    private String cust_id,CurrentDate;
    private Button btnSelectWeight, btnAddToCart, btnCakeWeightSelected;
    private String SelectedCakePicUrl;
    private int SelectedCakePrice;
    private float SelectedCakeWeight;
    private ImageView ivCakeDetails,imgRatingStar;
    private EditText etQuantity;
    private TextView tvCakeName, tvCakeFlavour, tvCakeType,tvDate;
    private NetworkTask networkTask;
    private ListView lvAvailWeights;
    private ArrayList<String> CakeAvailWeightArrayList = new ArrayList<String>();
    private ArrayList<String> CakeAvailPriceArrayList = new ArrayList<String>();
    private ProgressDialog progress;
    private int flagWeightSelected = 0;
    int selectedPosition;
    private LinearLayout linearLayout;
    private Button btnSelectTime,btnCakeMsg,btnSaveCakeMsg;
    private DatePickerDialog dbDatePicker;
    private String selected_date,delMode,CakeMsg,qnt;
    private int myear,mmonth,mday,mHour,FlagTimeEntered=0,FlagDateEntered=0;
    int day,month,year,grandweight,shippingCharge,total;
    private Dialog dialogRating;
    private float rating;
    private RatingBar CakeRating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cake_details);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//to disable automatic opening of keyboard


        initviews();
        getsharedPref();
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

        progress = new ProgressDialog(this);
        progress.setMessage("Loading");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
        progress.setCanceledOnTouchOutside(false);
        getCakeDetails();
        getCakeAvailWeight();
        setCurrentDateOnView();

    }

    public void getsharedPref(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        cust_id = sharedPreferences.getString("cust_id", "0");

    }
    private void initviews() {
        linearLayout=(LinearLayout)findViewById(R.id.linearCakeDetails);
        btnSelectWeight = (Button) findViewById(R.id.btnSelectWeight);
        btnSelectWeight.setOnClickListener(this);
        btnAddToCart = (Button) findViewById(R.id.btnAddToCart);
        btnAddToCart.setOnClickListener(this);
        etQuantity = (EditText) findViewById(R.id.etQuantity);
        tvCakeName = (TextView) findViewById(R.id.tvCakeName);
        tvCakeFlavour = (TextView) findViewById(R.id.tvCakeFlavour);
        tvCakeType = (TextView) findViewById(R.id.tvCakeType);
        ivCakeDetails = (ImageView) findViewById(R.id.ivCakeDetails);
        btnCakeMsg=(Button)findViewById(R.id.btnCakeMsg);
        btnCakeMsg.setOnClickListener(this);
        btnSelectTime=(Button)findViewById(R.id.btnSelectTime);
        btnSelectTime.setOnClickListener(this);
        tvDate=(TextView)findViewById(R.id.tvDate);
        tvDate.setOnClickListener(this);
        imgRatingStar = (ImageView) findViewById(R.id.imgRatingStar);
        imgRatingStar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnSelectWeight:
                if(FlagDateEntered==1) {
                    //if user tries to change weight after selecting date and time
                    FlagDateEntered=0;
                    tvDate.setText("Select Delivery Date");
                    tvDate.setBackgroundResource(R.drawable.button_not_selected_border);
                    FlagTimeEntered=0;
                    btnSelectTime.setText("Select Delivery Time");
                    btnSelectTime.setBackgroundResource(R.drawable.button_not_selected_border);
                }
                final Dialog SelectWeight = new Dialog(Cake_Details.this);
                SelectWeight.setContentView(R.layout.popup_btn_select_weight);
                SelectWeight.setTitle("Select weight ");
                btnCakeWeightSelected = (Button) SelectWeight.findViewById(R.id.btnCakeWeightSelected);
                lvAvailWeights = (ListView) SelectWeight.findViewById(R.id.lvAvailWeights);
                setAdapter();
                btnCakeWeightSelected.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flagWeightSelected = 1;
                        if (flagWeightSelected != 0) {
                            SelectWeight.dismiss();
                            Log.e("SelectedCakeWeight", String.valueOf(SelectedCakeWeight));
                            btnSelectWeight.setText(String.valueOf(SelectedCakeWeight) + " kg");
                            btnSelectWeight.setTextColor(Color.BLACK);
                            btnSelectWeight.setBackgroundResource(R.drawable.button_selected_border);
                        } else {
                            Toast.makeText(Cake_Details.this, "Please select weight", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                SelectWeight.show();
                break;
            case R.id.imgRatingStar:
                getsharedPref();
                if(!cust_id.equals("0")) {
                    dialogRating = new Dialog(Cake_Details.this);
                    dialogRating.setContentView(R.layout.activity_rating_cake);
                    dialogRating.setTitle("Rate The Cake");

                    Button btnRatingSave = (Button) dialogRating.findViewById(R.id.btnRatingSave);
                    CakeRating = (RatingBar) dialogRating.findViewById(R.id.CakeRating);

                    btnRatingSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rating = Float.parseFloat(String.valueOf(CakeRating.getRating()));
                            rate_cake();
                        }
                    });
                    dialogRating.show();
                }else{
                    Util.LoginAlert(this);
                }
                break;

            case R.id.tvDate:
                if(flagWeightSelected!=0) {
                    setDateTimeField();
                }else{
                    btnSelectWeight.setError("Please Select Weight First");
                }
                break;
            case R.id.btnSelectTime:
                if(flagWeightSelected!=0&&FlagDateEntered!=0) {
                    final String currentDate = mday + "-" + (mmonth + 1) + "-" + myear;
                    Log.e("current date", currentDate);

                    if (mHour >= 21 && selected_date.equals(currentDate)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setCancelable(false);
                        builder.setMessage("No delivery slots available for today.");
                        builder.setPositiveButton("Select another date", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setDateTimeField();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        final Dialog SelectTimeMode = new Dialog(Cake_Details.this);
                        SelectTimeMode.setContentView(R.layout.popup_btn_select_time_deliverydetailsclass);
                        SelectTimeMode.setTitle("Select Time mode ");

                        final Button btnTimeModeSelected = (Button) SelectTimeMode.findViewById(R.id.btnTimeModeSelected);
                        final RadioButton rbTime1 = (RadioButton) SelectTimeMode.findViewById(R.id.rbTime1);
                        final RadioButton rbTime2 = (RadioButton) SelectTimeMode.findViewById(R.id.rbTime2);
                        final RadioButton rbTime3 = (RadioButton) SelectTimeMode.findViewById(R.id.rbTime3);
                        final RadioButton rbTime4 = (RadioButton) SelectTimeMode.findViewById(R.id.rbTime4);
                        final RadioButton rbTime5 = (RadioButton) SelectTimeMode.findViewById(R.id.rbTime5);
                        final RadioButton rbTime6 = (RadioButton) SelectTimeMode.findViewById(R.id.rbTime6);
                        final RadioButton rbTime7 = (RadioButton) SelectTimeMode.findViewById(R.id.rbTime7);
                        final RadioButton rbTime8 = (RadioButton) SelectTimeMode.findViewById(R.id.rbTime8);

                        if (selected_date.equals(currentDate)) {
                            rbTime1.setVisibility(View.GONE);
                            if (mHour >= 9) {
                                rbTime2.setVisibility(View.GONE);
                            }
                            if (mHour >= 11) {
                                rbTime3.setVisibility(View.GONE);
                            }
                            if (mHour >= 13) {
                                rbTime4.setVisibility(View.GONE);
                            }
                            if (mHour >= 15) {
                                rbTime5.setVisibility(View.GONE);
                            }
                            if (mHour >= 17) {
                                rbTime6.setVisibility(View.GONE);
                            }
                            if (mHour >= 19) {
                                rbTime7.setVisibility(View.GONE);
                            }


                        }
                        btnTimeModeSelected.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (rbTime1.isChecked()) {//12am
                                    delMode = (String) rbTime1.getText();
                                } else if (rbTime2.isChecked()) {//9am-11am
                                    delMode = (String) rbTime2.getText();
                                } else if (rbTime3.isChecked()) {//11-1pm
                                    delMode = (String) rbTime3.getText();

                                } else if (rbTime4.isChecked()) {//1-3pm
                                    delMode = (String) rbTime4.getText();
                                } else if (rbTime5.isChecked()) {//3-5
                                    delMode = (String) rbTime5.getText();
                                } else if (rbTime6.isChecked()) {//5-7
                                    delMode = (String) rbTime6.getText();
                                } else if (rbTime7.isChecked()) {//7-9
                                    delMode = (String) rbTime7.getText();
                                } else if (rbTime8.isChecked()) {//9pm-11pm
                                    delMode = (String) rbTime8.getText();
                                } else {
                                    Toast.makeText(Cake_Details.this, "Please select delivery time", Toast.LENGTH_SHORT).show();
                                }
                                if (delMode != null) {
                                    SelectTimeMode.dismiss();
                                    btnSelectTime.setText(delMode);
                                    btnSelectTime.setTextColor(Color.BLACK);
                                    FlagTimeEntered = 1;
                                    btnSelectTime.setBackgroundResource(R.drawable.button_selected_border);
                                }
                            }
                        });
                        SelectTimeMode.show();
                    }
                }else if(flagWeightSelected==0){
                    btnSelectWeight.setError("Please Select Weight First");
                }else if(FlagDateEntered==0){
                    tvDate.setError("Please Select Date First");
                }
                break;

            case R.id.btnCakeMsg:

                final Dialog EnterMsg = new Dialog(Cake_Details.this);
                EnterMsg.setContentView(R.layout.popup_btn_cake_msg);
                EnterMsg.setTitle("Msg On Cake");

                btnSaveCakeMsg=(Button)EnterMsg.findViewById(R.id.btnSaveCakeMsg);
                final EditText edCakeMsg=(EditText) EnterMsg.findViewById(R.id.edCakeMsg);
                if(CakeMsg!=null && !CakeMsg.isEmpty()){
                    edCakeMsg.setText(CakeMsg);
                }
                btnSaveCakeMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String msg=edCakeMsg.getText().toString();
                        if(!msg.isEmpty()){
                            CakeMsg=msg;
                            EnterMsg.dismiss();
                            btnCakeMsg.setBackgroundResource(R.drawable.button_selected_border);
                            btnCakeMsg.setText(msg);
                        }else{
                            Toast.makeText(Cake_Details.this, "Please enter msg", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                EnterMsg.show();
                break;

            case R.id.btnAddToCart:
                qnt = etQuantity.getText().toString();

                if ((!qnt.isEmpty()) && flagWeightSelected != 0&&FlagDateEntered!=0&&FlagTimeEntered!=0) {
                    if (qnt.equals("0") || qnt.equals("00")) {
                        etQuantity.setError("Please Enter Qnt");
                    } else {
                        //store cake details in util
                        total = 0;
                        int j=0;
                        int flagMatchFound=0;
                        int matchfoundpos=0;

                        //first check cart if proudct alrdy present then we will update only qnt and total in cart
                        for(j=0;j<Util.CakeIdList.size();j++){
                            if(Util.CakeIdList.get(j).equals(String.valueOf(Util.SelectedCakeId))) {
                                Log.e("id same","id same");
                                String wght=Util.CakeWeightList.get(j);
                                String delDate=Util.CakeDeliveryDateList.get(j);
                                String delTime=Util.CakeDeliveryTimeList.get(j);
                                if(wght.equals(String.valueOf(SelectedCakeWeight))&&delDate.equals(selected_date)&&delTime.equals(delMode)){
                                    Log.e("weight also same","weight also same");
                                    flagMatchFound=1;
                                    matchfoundpos=j;
                                }
                            }
                        }
                        if(flagMatchFound==1) {//update qnt and total only

                                String oldQnt=Util.CakeQntList.get(matchfoundpos);
                                oldQnt= String.valueOf(Integer.parseInt(oldQnt)+Integer.parseInt(qnt));
                                        Util.CakeQntList.set(matchfoundpos, String.valueOf(oldQnt));
                                Log.e("updated qnt", Util.CakeQntList.get(matchfoundpos));
                            Float wght= Float.valueOf(Util.CakeWeightList.get(matchfoundpos));

//                            grandweight= (int) oldQnt*Integer.parseInt(wght)*1000;
                            grandweight = (int) (Integer.parseInt(oldQnt) *wght * 1000);
                            Log.e("grand weight", String.valueOf(grandweight));
                            calcShipCharge();
                            total = Integer.parseInt(etQuantity.getText().toString()) * SelectedCakePrice;
                           // total=total+shippingCharge;
                            Util.CakeTotalList.set(matchfoundpos, String.valueOf((Integer.parseInt(Util.CakeTotalList.get(matchfoundpos)) + total)));
                                Log.e("updated total", Util.CakeTotalList.get(matchfoundpos));

                            AlertDialog.Builder build = new AlertDialog.Builder(Cake_Details.this);
                            build.setCancelable(false);
                            build.setMessage("Item with price ₹" + total + " has been successfully added to your cart!");
                            build.setPositiveButton("Continue Shopping", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Cake_Details.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                            build.setNegativeButton("Checkout", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Cake_Details.this, Cart_Details.class);
                                    startActivity(intent);
                                }
                            });
                            AlertDialog alert1 = build.create();
                            alert1.show();
                        }else {//add new product
                            grandweight = (int) (Integer.parseInt(qnt) * (SelectedCakeWeight) * 1000);
                            Log.e("grand weight", String.valueOf(grandweight));
                            calcShipCharge();
                            total = Integer.parseInt(etQuantity.getText().toString()) * SelectedCakePrice;
                            //total = total + shippingCharge;
                            Log.e("price*qnt", String.valueOf(total));
                            AlertDialog.Builder builder = new AlertDialog.Builder(Cake_Details.this);
                            builder.setMessage("Shipping charges ₹" + shippingCharge + "will be extra.Total amount is ₹" + String.valueOf(total+shippingCharge));
                            builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    addToCart();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();

                        }
                    }
                } else if (flagWeightSelected == 0) {
                    btnSelectWeight.setError("Please Select Weight");
                } else if (FlagDateEntered == 0) {
                    tvDate.setError("Please Select Delivery Date");
                } else if (FlagTimeEntered == 0) {
                    btnSelectTime.setError("Please Select Delivery Time");
                } else {
                    etQuantity.setError("Please Enter Qnt");
                }
                break;
            default:
                break;
        }
    }

    public void calcShipCharge(){
        if(grandweight<=1000){
            shippingCharge=49;
        }else if(grandweight>1000&&grandweight<=3000){
            shippingCharge=99;
        }else{
            shippingCharge=199;
        }
        if(delMode.equals("11:45PM-12.30AM")){
            shippingCharge=199;
        }
        Log.e("shipping charges", String.valueOf(shippingCharge));

    }

    public void addToCart(){
        Util.CakeNameArrayList.add(tvCakeName.getText().toString());
        Util.CakeRateList.add(String.valueOf(SelectedCakePrice));
        Log.e("test cake price", String.valueOf(SelectedCakePrice));
        Util.CakeWeightList.add(String.valueOf(SelectedCakeWeight));
        Util.CakeQntList.add(qnt);
        Util.CakeIdList.add(String.valueOf(Util.SelectedCakeId));
        Util.CakePicList.add(String.valueOf(SelectedCakePicUrl));
        //new added
        Util.CakeDeliveryDateList.add(selected_date);
        Util.CakeDeliveryTimeList.add(delMode);
        Util.CakeMsgList.add(CakeMsg);

        Util.CakeShippingChargeList.add(String.valueOf(shippingCharge));

        Util.CakeTotalList.add(String.valueOf(total));

        AlertDialog.Builder build = new AlertDialog.Builder(Cake_Details.this);
        build.setCancelable(false);
        build.setMessage("Item with price ₹" + String.valueOf(total+shippingCharge) + " has been successfully added to your cart!");
        build.setPositiveButton("Continue Shopping", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Cake_Details.this, MainActivity.class);
                startActivity(intent);
            }
        });
        build.setNegativeButton("Checkout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Cake_Details.this, Cart_Details.class);
                startActivity(intent);
            }
        });
        AlertDialog alert1 = build.create();
        alert1.show();
    }
    // get current date
    public void setCurrentDateOnView() {

        final Calendar c = Calendar.getInstance();
        myear = c.get(Calendar.YEAR);
        mmonth = c.get(Calendar.MONTH);
        mday = c.get(Calendar.DAY_OF_MONTH);
        mHour=c.get(Calendar.HOUR_OF_DAY);
        CurrentDate=mday+"-"+(mmonth+1)+"-"+myear;
    }

    private void setDateTimeField() {
        try {

                final Calendar newCalendar = Calendar.getInstance();
            if(SelectedCakeWeight>2.0&&SelectedCakeWeight<=4.0) {
                newCalendar.add(Calendar.DATE,1);//set next day (delivery mode 24 hours applied)
            }else if(SelectedCakeWeight>4.0){
                newCalendar.add(Calendar.DATE,2);//set min date 2 days after(delivery mode 48 hours applied)
            }
            //set min date in calender as current date so that if user is placing 3.0kg order
            // at 10am on 23rd he will be able to select date and time after 24 hours ie 24th and time 11am onwards
            myear = newCalendar.get(Calendar.YEAR);
            mmonth = newCalendar.get(Calendar.MONTH);
            mday = newCalendar.get(Calendar.DAY_OF_MONTH);
            CurrentDate=mday+"-"+(mmonth+1)+"-"+myear;

            dbDatePicker = new DatePickerDialog(Cake_Details.this,
                        new DatePickerDialog.OnDateSetListener() {

                            public void onDateSet(DatePicker view, int yearSelected,
                                                  int monthOfYear, int dayOfMonth) {
                                newCalendar.set(myear, monthOfYear, dayOfMonth);
                                year = yearSelected;
                                month = monthOfYear;
                                day = dayOfMonth;
                                // Set the Selected Date in Select date Button
                                tvDate.setText("Date selected : " + day + "-" + (month+1) + "-" + year);
                                tvDate.setBackgroundResource(R.drawable.button_selected_border);
                                selected_date=day+"-"+(month+1)+"-"+year;
                                FlagDateEntered=1;
                            }

                        }, newCalendar.get(Calendar.YEAR),
                        newCalendar.get(Calendar.MONTH),
                        newCalendar.get(Calendar.DAY_OF_MONTH));

                dbDatePicker.getDatePicker().setMinDate(
                        newCalendar.getTime().getTime());

            dbDatePicker.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getCakeDetails() {
        networkTask = new NetworkTask(this, this, false);
        List<NameValuePair> params = getRequestParams();
        networkTask.execute(params, Post_URL.URL_GET_CAKES_DETAILS, 1);
    }

    public void getCakeAvailWeight() {
        networkTask = new NetworkTask(this, this, false);
        List<NameValuePair> params = getRequestParams();
        networkTask.execute(params, Post_URL.URL_GET_CAKES_AVAIL_WEIGHT, 2);
    }

    private void rate_cake() {
        NetworkTask networkTaskFavourite = new NetworkTask(this, this, false);
        /**
         * Network Work
         */
        List<NameValuePair> params = getRequestParamsRateCake();
        networkTaskFavourite.execute(params, Post_URL.URL_RATE_CAKE, 3);
    }

    private List<NameValuePair> getRequestParams() {
        // TODO Enter Comments Here ...
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //pass area id to get vendors in that area
        params.add(new BasicNameValuePair("cake_id", String.valueOf(Util.SelectedCakeId)));
        return params;
    }

    private List<NameValuePair> getRequestParamsRateCake()
    {
        // TODO Enter Comments Here ...
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("rate", String.valueOf(rating)));
        //  params.add(new BasicNameValuePair("customer_id", cust_id));
        params.add(new BasicNameValuePair("cake_id", String.valueOf(Util.SelectedCakeId)));
        return params;
    }
    private void handleResponse(JSONObject serverResponse) {
        int success = 0;
        try {
            success = serverResponse.getInt(Responce.TAG_SUCCESS);
            if (success == 1) {
                JSONArray jsonArray = serverResponse.getJSONArray("cake_details");
                for (int i = 0; i < jsonArray.length(); i++) {
                    tvCakeName.setText(jsonArray.getJSONObject(i).getString("cake_name"));
                    tvCakeFlavour.setText(jsonArray.getJSONObject(i).getString("flavour_name"));
                    tvCakeType.setText(jsonArray.getJSONObject(i).getString("type"));
                    SelectedCakePicUrl = ("http://www.foodytreat.com/" + jsonArray.getJSONObject(i).getString("cake_image"));
                    Picasso.with(Cake_Details.this)
                            .load(SelectedCakePicUrl)
                            .placeholder(R.drawable.placeholder)
                            .into(ivCakeDetails);
                }

            } else {
                progress.dismiss();
                Toast.makeText(Cake_Details.this, "Sorry no details available!", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {

        }
    }

    private void handleResponseCakeWeight(JSONObject serverResponse) {
        int success = 0;

        try {
            success = serverResponse.getInt(Responce.TAG_SUCCESS);
            if (success == 1) {
                JSONArray jsonArray = serverResponse.getJSONArray("cake_weights");
                SelectedCakePrice = Integer.parseInt(jsonArray.getJSONObject(0).getString("price"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    CakeAvailWeightArrayList.add(jsonArray.getJSONObject(i).getString("weight"));
                    CakeAvailPriceArrayList.add(jsonArray.getJSONObject(i).getString("price"));
                }
                //use first weight in the list as selected weight even if user is not selecting any weight
                SelectedCakeWeight = Float.parseFloat(jsonArray.getJSONObject(0).getString("weight"));
                SelectedCakePrice=Integer.parseInt(jsonArray.getJSONObject(0).getString("price"));
                progress.dismiss();
            } else {
                progress.dismiss();
                Toast.makeText(Cake_Details.this, "Sorry no details available!", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {

        }

    }
    private void handleResponseRateCake(JSONObject serverResponse) {
        int success = 0;
        try {
            success = serverResponse.getInt("Success");

            if (success == 1) {
                dialogRating.dismiss();
                Toast.makeText(Cake_Details.this, serverResponse.getString(Responce.TAG_MESSAGE), Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(Cake_Details.this, serverResponse.getString(Responce.TAG_MESSAGE), Toast.LENGTH_LONG).show();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onComplete(JSONObject serverResponse, int RESPONSE_IDENTIFIER_FLAG) throws JSONException {
        switch (RESPONSE_IDENTIFIER_FLAG) {
            case 1:
                handleResponse(serverResponse);
                break;
            case 2:
                handleResponseCakeWeight(serverResponse);
                break;
            case 3:
                handleResponseRateCake(serverResponse);
                break;
            default:
        }
    }

    //set adapter to the listview
    private void setAdapter() {
        MyAdapter lvAdapter = new MyAdapter(this,
                android.R.layout.simple_list_item_1, CakeAvailWeightArrayList, CakeAvailPriceArrayList);
        lvAvailWeights.setAdapter(lvAdapter);

    }

    //adapter for popup of select weight
    class MyAdapter extends ArrayAdapter<String> {
        ArrayList<String> CakeAvailWeightArrayList = new ArrayList<String>();
        ArrayList<String> CakeAvailPriceArrayList = new ArrayList<String>();

        public MyAdapter(Context context, int textViewResourceId, ArrayList<String> CakeAvailWeightArrayList,
                         ArrayList<String> CakeAvailPriceArrayList) {
            super(context, textViewResourceId, CakeAvailWeightArrayList);
            this.CakeAvailWeightArrayList = CakeAvailWeightArrayList;
            this.CakeAvailPriceArrayList = CakeAvailPriceArrayList;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View row = inflater.inflate(R.layout.select_cake_weight_list_item, parent, false);
            final RadioButton rbSelectWeight = (RadioButton) row.findViewById(R.id.rbSelectWeight);

            rbSelectWeight.setText(CakeAvailWeightArrayList.get(position) + " kg  ( ₹" + CakeAvailPriceArrayList.get(position) + " )");
            rbSelectWeight.setChecked(position == selectedPosition);
            rbSelectWeight.setTag(position);

           // SelectedCakePrice = 0;
//            SelectedCakePrice = Integer.parseInt(CakeAvailPriceArrayList.get(0));
            rbSelectWeight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition = (Integer) v.getTag();
                    notifyDataSetChanged();
                    if (CakeAvailPriceArrayList.get(position).equals("0")) {
                        rbSelectWeight.setChecked(false);
                        Toast.makeText(getContext(), "Sorry not available in this weight.Try another category", Toast.LENGTH_SHORT).show();
                    } else {

                        rbSelectWeight.setChecked(true);
                        SelectedCakeWeight = Float.parseFloat(CakeAvailWeightArrayList.get(position));
                        SelectedCakePrice = 0;
                        SelectedCakePrice = Integer.parseInt(CakeAvailPriceArrayList.get(position));
                        Log.e("SelectedWeight", String.valueOf(SelectedCakeWeight));
                        Log.e("SelectedCakePrice", String.valueOf(SelectedCakePrice));
                        flagWeightSelected = 1;
                    }
                }
            });
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            return row;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

            case R.id.menu_logout:
                getsharedPref();
                if (cust_id.equals("0")) {

                } else {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("cust_id", "0");
                    editor.putString("cust_email_id", "");
                    Toast.makeText(Cake_Details.this, "Logout successfull", Toast.LENGTH_SHORT).show();
                    editor.commit();
                    Intent inten = new Intent(Cake_Details.this, LoginActivity.class);
                    startActivity(inten);
                }
                return true;
            case R.id.menu_viewCart:
                Intent intent = new Intent(Cake_Details.this, Cart_Details.class);
                startActivity(intent);
                return true;

            case R.id.menu_wishlist:
                getsharedPref();
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
                getsharedPref();
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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Cake_Details.this);
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
                getsharedPref();
                if (cust_id.equals("0")) {
                    Util.LoginAlert(this);
                } else {
                    Intent profile = new Intent(this, EditPersonalProfile.class);
                    startActivity(profile);
                }
                break;
            case R.id.menu_faq:
                Intent menu_faq = new Intent(this, MenuFAQ.class);
                startActivity(menu_faq);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
