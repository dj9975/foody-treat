package xpertrixitsolution.com.foodytreat;
/**
 * @author Vrushali Matale, created on 18/09/2015
 * edited by: Dhiraj Devkar
 *
 * last edited on 29/10/2015 , fb logout from program without using fb logout button
 */

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import xpertrixitsolution.com.foodytreat.LazyImageLoading.ImageLoader;
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

public class SelectVendor extends Activity implements CompletionListener {
    private String cust_id;
    private NetworkTask networkTask;
    private TextView tvSelectVendor;
    public static int mSelected = 0;
    private GridView gridview;
    private ProgressDialog progress;
    ArrayList<String> VendorPicsArrayList = new ArrayList<String>();
    ArrayList<String> VendorIdArrayList = new ArrayList<String>();
    ArrayList<String> VendorNameArrayList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_vendor);
            getSharedPref();
        ActionBar ab = getActionBar();
        ab.setTitle(Util.SelectedAreaName);

        tvSelectVendor=(TextView)findViewById(R.id.tvSelectVendor);
        Typeface myfont = Typeface.createFromAsset(getAssets(), "lobster.ttf");
        tvSelectVendor.setTypeface(myfont);
        gridview = (GridView) findViewById(R.id.gridview_view1);


        getVendorsList();
        progress=new ProgressDialog(this);
        progress.setMessage("Loading");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
        progress.setCanceledOnTouchOutside(false);

    }

    public void getSharedPref(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        cust_id= sharedPreferences.getString("cust_id", "0");

    }
    public void getVendorsList(){
        networkTask = new NetworkTask(this, this, false);
        List<NameValuePair> params = getRequestParams();
        networkTask.execute(params, Post_URL.URL_GET_VENDORS_LIST, 1);
    }

    private List<NameValuePair> getRequestParams() {
        // TODO Enter Comments Here ...
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //pass area id to get vendors in that area
        params.add(new BasicNameValuePair("areaid", String.valueOf(Util.SelectedAreaId)));
        return params;
    }
    private void handleResponse(JSONObject serverResponse) {
        int success = 0;
        try {
            success = serverResponse.getInt(Responce.TAG_SUCCESS);
            if (success == 1) {
                JSONArray jsonArray = serverResponse.getJSONArray("venderlist");
                for (int i = 0; i < jsonArray.length(); i++) {

                    VendorPicsArrayList.add("http://www.foodytreat.com/"+jsonArray.getJSONObject(i).getString("img_url"));
                    VendorNameArrayList.add(jsonArray.getJSONObject(i).getString("vendor_name"));
                    VendorIdArrayList.add(jsonArray.getJSONObject(i).getString("vender_id"));
                }
                gridview.setAdapter(new ImageAdapter(this));
                gridview.setSelection(mSelected);
            } else {
                progress.dismiss();
                Toast.makeText(SelectVendor.this, "Sorry no vendors available!", Toast.LENGTH_LONG).show();
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

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        public ImageLoader imageLoader;
        private LayoutInflater inflater=null;
        public ImageAdapter(Context c) {
            mContext = c;
            inflater = ( LayoutInflater )mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        public int getCount() {
            return VendorPicsArrayList.size();
        }
        public Object getItem(int position) {
            return null;
        }
        public long getItemId(int position) {
            return 0;
        }



        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            TextView tvVendorName;
            View row=convertView;
            // Holder holder=new Holder();
            imageLoader=new ImageLoader(getApplicationContext());
            row = inflater.inflate(R.layout.select_vendor_list_item,null);
            imageView=(ImageView)row.findViewById(R.id.ivVendor);
            imageView.setBackgroundResource(R.drawable.select_vendor_img_shape);
            // imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(7, 7, 7, 7);
            //imageView = (ImageView) convertView;
            tvVendorName=(TextView)row.findViewById(R.id.tvVendorName);
            tvVendorName.setText(VendorNameArrayList.get(position));

            imageLoader.DisplayImage(VendorPicsArrayList.get(position),imageView);
            progress.dismiss();
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Util.SelectedVendorId= Integer.parseInt(VendorIdArrayList.get(position));
                    Intent intent=new Intent(SelectVendor.this,SelectCake.class);
                    intent.putExtra("coming_from", "Coming from vendor");
                    startActivity(intent);
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
                    Toast.makeText(SelectVendor.this, "Logout successfull", Toast.LENGTH_SHORT).show();
                    editor.commit();
                    Intent inten = new Intent(SelectVendor.this, LoginActivity.class);
                    startActivity(inten);
                }
                return true;
            case R.id.menu_viewCart:
                Intent intent = new Intent(SelectVendor.this, Cart_Details.class);
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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(SelectVendor.this);
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
            //return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);

    }
}
