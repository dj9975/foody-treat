package xpertrixitsolution.com.foodytreat;
/**
 * @author: Dhiraj devkar
 * edited by: Vrushali Matale
 *
 * last edited on 29/10/2015 , fb logout from program without using fb logout button
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import xpertrixitsolution.com.foodytreat.MenuItems.About_us;
import xpertrixitsolution.com.foodytreat.MenuItems.EditPersonalProfile;
import xpertrixitsolution.com.foodytreat.MenuItems.MenuFAQ;
import xpertrixitsolution.com.foodytreat.MenuItems.MenuItemOrderHistory;
import xpertrixitsolution.com.foodytreat.MenuItems.MenuPolicies;

public class Cart_Details extends Activity implements View.OnClickListener{
    private Button btnProceedToPayment;
    private TextView tvCartTotal,tvCartShipTotal;
    private ListView lvCart;
    private String cust_id;
    int grandTotal=0,grandWeight=0,grandshippingCharge=0;
    private LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart__details);
        initviews();
        getSharedPref();
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

        if(Util.CakeIdList.isEmpty()){
            cartEmptyAlert();
        }else {
            MyAdapter lvCakeListAdapter = new MyAdapter(this,
                    android.R.layout.simple_list_item_1,
                    R.id.tvCartTotal, Util.CakeNameArrayList, Util.CakeRateList, Util.CakeWeightList,
                    Util.CakeQntList, Util.CakeTotalList, Util.CakePicList);
            lvCart.setAdapter(lvCakeListAdapter);
        }
        CalcGrandTotal();

    }

    public void getSharedPref(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        cust_id= sharedPreferences.getString("cust_id", "0");

    }
    private void initviews() {
        linearLayout=(LinearLayout)findViewById(R.id.linearCartDetails);
        btnProceedToPayment=(Button)findViewById(R.id.btnProceedToPayment);
        btnProceedToPayment.setOnClickListener(this);
        tvCartTotal=(TextView)findViewById(R.id.tvCartTotal);
        tvCartShipTotal=(TextView)findViewById(R.id.tvCartShipCharge);
        lvCart=(ListView)findViewById(R.id.lvCartDetails);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.btnProceedToPayment:
                getSharedPref();
                if(!cust_id.equals("0")) {
                    if(Util.CakeIdList.isEmpty()){
                        cartEmptyAlert();
                    }else {
                        Intent myIntent = new Intent(Cart_Details.this,
                                Delivery_Details.class);
                        startActivity(myIntent);
                    }
                }else{
                   Util.LoginAlert(this);
                }
                break;

            default: break;
        }
    }

    public void cartEmptyAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your cart is empty!!");
        builder.setPositiveButton("Shop Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Cart_Details.this,MainActivity.class);
                startActivity(intent);
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }

    public void CalcGrandTotal(){
        int i;
        grandTotal=0;
        grandshippingCharge=0;
        for(i=0;i<Util.CakeTotalList.size();i++){

            grandTotal= grandTotal+Integer.parseInt(Util.CakeTotalList.get(i));
            grandshippingCharge=grandshippingCharge+Integer.parseInt(Util.CakeShippingChargeList.get(i));
            Log.e("grand total", String.valueOf(grandTotal));
        }
        tvCartShipTotal.setText("₹" +grandshippingCharge);
        Util.GrandTotal=grandTotal;
        grandTotal=grandTotal+grandshippingCharge;
        tvCartTotal.setText("₹" + grandTotal);

    }
    /**
     * Array adapter class for cart list view
     */
    private class MyAdapter extends ArrayAdapter<String> {

        ArrayList<String> CakeNameArrayList = new ArrayList<String>();
        ArrayList<String> CakeRateList = new ArrayList<String>();
        ArrayList<String> CakeWeightList = new ArrayList<String>();
        ArrayList<String> CakeQntList = new ArrayList<String>();
        ArrayList<String> CakeTotalList = new ArrayList<String>();
        ArrayList<String> CakePicList = new ArrayList<String>();

        public MyAdapter(Context context, int resource, int textViewResourceId, ArrayList<String> cakenamelist,
                         ArrayList<String> cakeratelist,ArrayList<String> cakeweightlist,
                         ArrayList<String> cakeqntList,ArrayList<String> caketotallist,ArrayList<String> CakePicList)

        {
            super(context, resource, textViewResourceId, cakenamelist);
            this.CakeNameArrayList = cakenamelist;
            this.CakeRateList = cakeratelist;
            this.CakeWeightList = cakeweightlist;
            this.CakeQntList = cakeqntList;
            this.CakeTotalList = caketotallist;
            this.CakePicList=CakePicList;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View row = inflater.inflate(R.layout.cart_list_item, parent, false);

            final TextView tvCartItemName = (TextView) row.findViewById(R.id.tvCartItemName);
            final TextView tvCartItemWeight = (TextView) row.findViewById(R.id.tvCartItemRate);
            final TextView tvCartItemQnt = (TextView) row.findViewById(R.id.tvCartItemQnt);
            final TextView tvCartItemTotal = (TextView) row.findViewById(R.id.tvCartItemTotal);
            final ImageView ivCartItemList=(ImageView)row.findViewById(R.id.ivCartCakePicItemList);
            final ImageView ivCartItemRemove=(ImageView)row.findViewById(R.id.ivDeleteItemFromCart);

            Picasso.with(Cart_Details.this)
                    .load(Util.CakePicList.get(position))
                    .placeholder(R.drawable.placeholder)
                    .into(ivCartItemList);

            tvCartItemName.setText(CakeNameArrayList.get(position));
            tvCartItemWeight.setText(CakeWeightList.get(position)+"kg");
            tvCartItemQnt.setText("₹"+CakeRateList.get(position)+" * "+CakeQntList.get(position));
            tvCartItemTotal.setText("₹"+CakeTotalList.get(position));

            ivCartItemRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.CakeNameArrayList.remove(position);
                    Util.CakeWeightList.remove(position);
                    Util.CakeQntList.remove(position);
                    Util.CakeTotalList.remove(position);
                    Util.CakePicList.remove(position);
                    Util.CakeRateList.remove(position);
                    Util.CakeIdList.remove(position);
                    Util.CakeShippingChargeList.remove(position);
                    Util.CakeDeliveryTimeList.remove(position);
                    Util.CakeDeliveryDateList.remove(position);
                    Util.CakeMsgList.remove(position);
                    notifyDataSetChanged();
                    CalcGrandTotal();
                    Toast.makeText(Cart_Details.this,"Item has been removed from your cart!",Toast.LENGTH_SHORT).show();
                    if (Util.CakeIdList.isEmpty()){
                        cartEmptyAlert();
                    }
                }
            });

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.SelectedCakeId= Integer.parseInt(Util.CakeIdList.get(position));
                    Intent intent=new Intent(Cart_Details.this,Cake_Details.class);
                    startActivity(intent);
                }
            });

            return row;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart__details, menu);
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
                    Toast.makeText(Cart_Details.this, "Logout successfull", Toast.LENGTH_SHORT).show();
                    editor.commit();
                    Intent inten = new Intent(Cart_Details.this, LoginActivity.class);
                    startActivity(inten);

                }
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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Cart_Details.this);
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
