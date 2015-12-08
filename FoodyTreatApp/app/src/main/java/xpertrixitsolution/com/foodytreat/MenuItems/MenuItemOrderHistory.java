package xpertrixitsolution.com.foodytreat.MenuItems;
/**
 * @author: Vrushali Matale, created on 09/10/2015
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
import android.widget.ListView;
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


public class MenuItemOrderHistory extends Activity implements CompletionListener {

    private String cust_id;
    private NetworkTask networkTask;
    private ListView lvOrderHistory;
    ArrayList<String> OrderIdArrayList = new ArrayList<String>();
    ArrayList<String> OrderDateArrayList = new ArrayList<String>();
    ArrayList<String> PriceArrayList = new ArrayList<String>();
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_item_order_history_list_view);

     getSharedPref();

        initviews();
        //start progress bar
        progress=new ProgressDialog(this);
        progress.setMessage("Loading");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
        progress.setCanceledOnTouchOutside(false);
        getOrderHistory();

    }
    public void getSharedPref(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        cust_id= sharedPreferences.getString("cust_id", "0");

    }
    public void getOrderHistory(){
        networkTask = new NetworkTask(this, this, false);
        List<NameValuePair> params = getRequestParams();
        networkTask.execute(params, Post_URL.URL_GET_ORDER_HISTORY, 1);
    }

    private List<NameValuePair> getRequestParams() {
        // TODO Enter Comments Here ...
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("user_id",cust_id));
        Log.e("orderhistory", params.toString());
        return params;
    }

    private void handleResponse(JSONObject serverResponse) {
        int success = 0;
        try {
            success = serverResponse.getInt(Responce.TAG_SUCCESS);

            if (success == 1) {

                JSONArray jsonArrayNews = serverResponse.getJSONArray("order_history");
                for (int i = 0; i < jsonArrayNews.length(); i++) {
                    OrderIdArrayList.add(jsonArrayNews.getJSONObject(i).getString("order_id"));
                    OrderDateArrayList.add(jsonArrayNews.getJSONObject(i).getString("order_date"));
                    PriceArrayList.add(jsonArrayNews.getJSONObject(i).getString("final_price"));

                }
                MyAdapter adapter = new MyAdapter(this,
                        android.R.layout.simple_list_item_1,
                        R.id.tvOrderIdNumber,OrderIdArrayList,
                        OrderDateArrayList,
                        PriceArrayList);
                lvOrderHistory.setAdapter(adapter);
                progress.dismiss();
            } else {

                progress.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage("No history available");
                builder.setPositiveButton("Order Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MenuItemOrderHistory.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                AlertDialog alert=builder.create();
                alert.show();
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
    /**
     * Array adapter class for order history
     */

    public class MyAdapter extends ArrayAdapter<String>
    {

        //private final Activity context;
        ArrayList<String> OrderIdArrayList = new ArrayList<String>();
        ArrayList<String> OrderDateArrayList = new ArrayList<String>();
        ArrayList<String> PriceArrayList = new ArrayList<String>();


        public MyAdapter(Context context, int resource, int textViewResourceId,
                         ArrayList<String> OrderIdArrayList,
                         ArrayList<String> OrderDateArrayList,
                         ArrayList<String> PriceArrayList)
        {

            super(context, resource, textViewResourceId,OrderIdArrayList);
            // TODO Auto-generated constructor stub
            this.OrderIdArrayList =OrderIdArrayList;
            this.OrderDateArrayList =OrderDateArrayList;
            this.PriceArrayList=PriceArrayList;

        }

        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View row = inflater.inflate(R.layout.order_id_list_item, parent, false);

            final TextView tvOrderIdNumber = (TextView) row.findViewById(R.id.tvOrderIdNumber);
            final TextView tvOrderDateNumber = (TextView) row.findViewById(R.id.tvOrderDateNumber);
            final TextView tvOrderPriceNumber = (TextView) row.findViewById(R.id.tvOrderPriceNumber);

            tvOrderIdNumber.setText(" "+OrderIdArrayList.get(position));
            tvOrderDateNumber.setText(" "+OrderDateArrayList.get(position));
            tvOrderPriceNumber .setText(" "+PriceArrayList.get(position));
            return row;
        }


    }


    private void initviews() {
        lvOrderHistory=(ListView)findViewById(R.id.lvOrderHistory);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_history, menu);
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
                    Toast.makeText(MenuItemOrderHistory.this, "Logout successfull", Toast.LENGTH_SHORT).show();
                    editor.commit();
                    Intent inten = new Intent(MenuItemOrderHistory.this, LoginActivity.class);
                    startActivity(inten);

                }
                return true;
            case R.id.menu_viewCart:
                Intent intent = new Intent(MenuItemOrderHistory.this, Cart_Details.class);
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

            case R.id.menu_legal:
                Intent legal = new Intent(this, MenuPolicies.class);
                startActivity(legal);
                break;

            case R.id.menu_about_us:
                Intent about_us = new Intent(this, About_us.class);
                startActivity(about_us);
                break;

            case R.id.menu_trackorder:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MenuItemOrderHistory.this);
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
