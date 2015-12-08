package xpertrixitsolution.com.foodytreat;
/**
 * @author Vrushali Matale, created on 16/09/2015
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
import android.graphics.Color;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import xpertrixitsolution.com.foodytreat.Filter.SelectCakeFilter;
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

public class SelectCake extends Activity implements CompletionListener {
    private String cust_id, coming_from;
    private  String cake_id;
    private NetworkTask networkTask;
    private ListView lvCakeVendors;
    private ProgressDialog progress;
    private ArrayList<String> CakeArrayList = new ArrayList<String>();
    private ArrayList<String> CakeIdArrayList = new ArrayList<String>();
    private ArrayList<String> CakePhotoArrayList = new ArrayList<String>();
    private ArrayList<String> CakePriceList = new ArrayList<String>();
    private ArrayList<String> CakeTypeBooleanList = new ArrayList<>();
    // private ArrayList<String> CakeTypeFlavourList = new ArrayList<>();
    private ArrayList<String> CakeRateArrayList = new ArrayList<>();
    private ArrayList<String> CakeRatingCountList = new ArrayList<>();
    private ArrayList<String> CakePresentInWishListArrayList = new ArrayList<>();
    private RelativeLayout relativeLayout;
    private Button btnFilters;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_cake);

        relativeLayout=(RelativeLayout)findViewById(R.id.relativeSelectCake);
        String screenSize= Util.getSizeName(this);
        if(screenSize.equals("HDPI")){
            relativeLayout.setBackgroundResource(R.drawable.back_mdpi);
        }else if(screenSize.equals("LDPI")){
            relativeLayout.setBackgroundResource(R.drawable.back_mdpi);
        }else if(screenSize.equals("XHDPI")){
            relativeLayout.setBackgroundResource(R.drawable.back_xhdpi);
        }else if(screenSize.equals("XXHDPI")){
            relativeLayout.setBackgroundResource(R.drawable.back_xxhdpi);
        }else{
            relativeLayout.setBackgroundResource(R.drawable.back_mdpi);
        }



        lvCakeVendors= (ListView)findViewById(R.id.lvCakeVendors);
        progress=new ProgressDialog(this);
        progress.setMessage("Loading");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
        progress.setCanceledOnTouchOutside(false);

        ActionBar ab = getActionBar();

        coming_from = getIntent().getExtras().getString("coming_from");
        getSharedPref();

        if(coming_from.equals("Coming from vendor"))
        {
            ab.setTitle("Select Cake");
            getCakeList();
        }
        else if(coming_from.equals("Coming from wishlist"))
        {
            ab.setTitle("Your Wishlist");
//            btnFilters.setVisibility(View.GONE);
                getwishlist();
        }
        else if (coming_from.equals("Coming from filter"))
        {
            ab.setTitle("Filterd");
            getFilteredCakeList();
        }

    }
    public void getSharedPref(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        cust_id= sharedPreferences.getString("cust_id", "0");

    }
    public void getCakeList(){
        networkTask = new NetworkTask(this, this, false);
        List<NameValuePair> params = getRequestParams();
        networkTask.execute(params, Post_URL.URL_GET_CAKES_LIST, 1);
    }

    public void getFilteredCakeList(){
        networkTask = new NetworkTask(this, this, false);
        List<NameValuePair> params = getRequestParamsCakeListFilter();
        networkTask.execute(params, Post_URL.URL_GET_FILTERED_CAKES_LIST, 4);
    }

    private List<NameValuePair> getRequestParams() {
        // TODO Enter Comments Here ...
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //pass area id to get vendors in that area
        params.add(new BasicNameValuePair("vendor_id",String.valueOf(Util.SelectedVendorId)));
        params.add(new BasicNameValuePair("user_id", String.valueOf(cust_id)));

        return params;
    }

    private List<NameValuePair> getRequestParamsCakeListFilter() {
        // TODO Enter Comments Here ...
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //pass area id to get vendors in that area
        params.add(new BasicNameValuePair("vendor_id",String.valueOf(Util.SelectedVendorId)));
        params.add(new BasicNameValuePair("user_id",String.valueOf(cust_id)));
        if(Util.FlavourFilter!=null&&!Util.FlavourFilter.isEmpty()) {
            params.add(new BasicNameValuePair("flavour", Util.FlavourFilter));
        }else{
            params.add(new BasicNameValuePair("flavour", "0"));
        }

        if(Util.CostFilterUpperLimit!=0) {
            params.add(new BasicNameValuePair("min_cost", String.valueOf(Util.CostFilterLowerLimit)));
            params.add(new BasicNameValuePair("max_cost", String.valueOf(Util.CostFilterUpperLimit)));
        }else{
            Log.e("maxcost","0");
            params.add(new BasicNameValuePair("min_cost","0"));
            params.add(new BasicNameValuePair("max_cost", "0"));
        }
        if(Util.WeightFilter!=null&&!Util.WeightFilter.isEmpty()) {
            params.add(new BasicNameValuePair("weight", Util.WeightFilter));
        }else{
            params.add(new BasicNameValuePair("weight","0"));
        }

        if(Util.OccassionFilter!=null&&!Util.OccassionFilter.isEmpty()) {
            params.add(new BasicNameValuePair("occasion", Util.OccassionFilter));
        }else{
            params.add(new BasicNameValuePair("occasion","0"));
        }
        if(Util.CakeTypeFilter!=null&&!Util.CakeTypeFilter.isEmpty()) {
            params.add(new BasicNameValuePair("cake_type", Util.CakeTypeFilter));
        }else{
            params.add(new BasicNameValuePair("cake_type","0"));
        }
        return params;
    }

    private void handleResponse(JSONObject serverResponse) {
        int success = 0;

        try {
            success = serverResponse.getInt(Responce.TAG_SUCCESS);
            if (success == 1) {
                JSONArray jsonArray = serverResponse.getJSONArray("productlist");
                for (int i = 0; i < jsonArray.length(); i++) {
                    //attach base url with the url you get from webservice
                    CakeIdArrayList.add(jsonArray.getJSONObject(i).getString("cake_id"));
                    CakeArrayList.add(jsonArray.getJSONObject(i).getString("cake_name"));
                    CakePhotoArrayList.add("http://www.foodytreat.com/"+jsonArray.getJSONObject(i).getString("cake_image"));
                    CakePriceList.add(jsonArray.getJSONObject(i).getString("price"));
                    CakeTypeBooleanList.add(jsonArray.getJSONObject(i).getString("type"));
                    CakeRateArrayList.add(jsonArray.getJSONObject(i).getString("rating"));
                    CakeRatingCountList.add(jsonArray.getJSONObject(i).getString("no_of_people"));

                    CakePresentInWishListArrayList.add(jsonArray.getJSONObject(i).getString("flag"));
                }
                setAdapter();
            } else {
                progress.dismiss();
                Util.FlavourFilter="";
                Util.WeightFilter="";
                Util.OccassionFilter="";
                Util.CakeTypeFilter="";
                Util.CostFilterLowerLimit=0;
                Util.CostFilterUpperLimit=0;

                if (coming_from.equals("Coming from filter")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    builder.setMessage("Sorry no cakes available!");
                    builder.setPositiveButton("Try other filters", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent1 = new Intent(SelectCake.this, SelectCakeFilter.class);
                            startActivity(intent1);
                            finish();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent=new Intent(SelectCake.this,SelectCake.class);
                            intent.putExtra("coming_from", "Coming from vendor");
                            startActivity(intent);
                            finish();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

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
                handleResponseAddToWishList(serverResponse);
                break;

            case 3:
                handleResponseGetWishList(serverResponse);
                break;
            case 4:
                handleResponse(serverResponse);
                break;
            default:
        }
    }
    //set adapter to the listview
    private void setAdapter() {
        LazyAdapter lvSelectCake = new LazyAdapter(this,
                CakeArrayList,CakeIdArrayList,CakePhotoArrayList,CakePriceList,
                CakeTypeBooleanList,CakeRateArrayList,CakeRatingCountList,CakePresentInWishListArrayList);
        lvCakeVendors.setAdapter(lvSelectCake);

    }

    public class LazyAdapter extends BaseAdapter {

        ArrayList<String> CakeArrayList = new ArrayList<String>();
        ArrayList<String> CakeIdArrayList = new ArrayList<String>();
        ArrayList<String> CakePhotoArrayList = new ArrayList<String>();
        ArrayList<String> CakePriceList = new ArrayList<String>();

        ArrayList<String> CakeTypeBooleanList = new ArrayList<String>();
        ArrayList<String> CakeRateArrayList = new ArrayList<String>();
        ArrayList<String> CakeRatingCountList = new ArrayList<String>();
        ArrayList<String> CakePresentInWishListArrayList = new ArrayList<String>();

        private Activity activity;
        private LayoutInflater inflater=null;
        public ImageLoader imageLoader;
        private int lastPosition = -1;

        public LazyAdapter(Activity a, ArrayList<String> cakearraylist,ArrayList<String>cakeidarraylist,
                           ArrayList<String>cakephotoarraylist,ArrayList<String> priceList
                , ArrayList<String>caketypebooleanlist, ArrayList<String> cakeratearraylist,ArrayList<String> cakeratingcountlist,
                           ArrayList<String>CakePresentInWishListArrayList) {

            activity = a;
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageLoader=new ImageLoader(activity.getApplicationContext());

            this.CakeArrayList = cakearraylist;
            this.CakeIdArrayList=cakeidarraylist;
            this.CakePhotoArrayList=cakephotoarraylist;
            this.CakePriceList = priceList;
            this.CakeTypeBooleanList=caketypebooleanlist;
            this.CakeRateArrayList=cakeratearraylist;
            this.CakeRatingCountList=cakeratingcountlist;
            this.CakePresentInWishListArrayList=CakePresentInWishListArrayList;

        }

        public int getCount() {
            return CakeArrayList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            View row=convertView;

            if(convertView==null)
                row = inflater.inflate(R.layout.select_cake_list_item, parent, false);
           //list item animation
            Animation animation = AnimationUtils.loadAnimation(getBaseContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            row.startAnimation(animation);
            lastPosition = position;

            final TextView tvCakeListName = (TextView) row.findViewById(R.id.tvCakeListName);
            //   final TextView tvCakeFlavourName = (TextView) row.findViewById(R.id.tvCakeListFlavour);
            final ImageView ivCakePic=(ImageView)row.findViewById(R.id.ivCakePic);
            final TextView tvCakeListPrice = (TextView) row.findViewById(R.id.tvCakeListPrice);
            final ImageView ivEggOrEggLess = (ImageView) row.findViewById(R.id.ivEggOrEggLess);
            final ImageView ivCakeListWishList=(ImageView)row.findViewById(R.id.ivCakeListWishList);

            RatingBar ratingBar=(RatingBar)row.findViewById(R.id.ratingBar);
            TextView tvratingCount=(TextView)row.findViewById(R.id.tvRatingCount);

            ratingBar.setRating((float) 2);

            ratingBar.setRating(Float.parseFloat(CakeRateArrayList.get(position)));
            tvratingCount.setText("("+CakeRatingCountList.get(position)+")");

            tvCakeListName.setText(CakeArrayList.get(position));
            // tvCakeFlavourName.setText(CakeTypeFlavourList.get(position));
            tvCakeListPrice.setText("₹ " + CakePriceList.get(position));
            if(CakeTypeBooleanList.get(position).equals("Egg")){
                ivEggOrEggLess.setImageResource(R.drawable.non_veg);
            }else{
                ivEggOrEggLess.setImageResource(R.drawable.veg_icon);
            }

            if(CakePresentInWishListArrayList.get(position).equals("1")){
                ivCakeListWishList.setImageResource(R.drawable.fav);
            }
            ivCakeListWishList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getSharedPref();
                    if (cust_id.equals("0")) {
                        Util.LoginAlert(activity);
                    } else {
                        ivCakeListWishList.setImageResource(R.drawable.fav);
                        // call method to add "item to wishlist"
                        cake_id = CakeIdArrayList.get(position);
                        getaddtowishlist();
                    }
                }
            });
            progress.dismiss();
            imageLoader.DisplayImage(CakePhotoArrayList.get(position),ivCakePic);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.SelectedCakeId = Integer.parseInt(CakeIdArrayList.get(position));
                    Intent myIntent = new Intent(SelectCake.this,
                            Cake_Details.class);
                    startActivity(myIntent);
                }
            });
            return row;
        }
    }



    /** start webservices of add item to wishlist (2nd webservice) -by Vrushali added on 12/10/2015 */
    private void getaddtowishlist() {
        networkTask = new NetworkTask(this, this, false);
        List<NameValuePair> params = getRequestParamsAddToWishlist();
        networkTask.execute(params, Post_URL.URL_GET_ADD_ITEM_TO_WISHLIST, 2);
    }
    private List<NameValuePair> getRequestParamsAddToWishlist() {
        // TODO Enter Comments Here ...
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", cust_id)); //
        params.add(new BasicNameValuePair("cake_id", cake_id)); //
        return params;
    }
    private void handleResponseAddToWishList(JSONObject serverResponse) {
        int success = 0;
        try {
            success = serverResponse.getInt(Responce.TAG_SUCCESS);
            if (success == 1) {
                Toast.makeText(SelectCake.this, serverResponse.getString(Responce.TAG_MESSAGE), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SelectCake.this, serverResponse.getString(Responce.TAG_MESSAGE), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
        }
    }
    /**finish webservices of "add item to wishlist" (2nd webservice) -by Vrushali added on 12/10/2015 */
    /**start webservices of "get wishlist" (3rd webservice)  -by Vrushali added on 12/10/2015 */
    private void getwishlist() {
        networkTask = new NetworkTask(this, this, false);
        List<NameValuePair> params = getRequestParamsGetWishlist();
        networkTask.execute(params, Post_URL.URL_GET_WISHLIST, 3); // checking
    }
    private List<NameValuePair> getRequestParamsGetWishlist() {
        // TODO Enter Comments Here ...
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", cust_id)); //
        return params;
    }

    private void handleResponseGetWishList(JSONObject serverResponse) {
        int success = 0;
        try {
            success = serverResponse.getInt(Responce.TAG_SUCCESS);
            if (success == 1) {
                JSONArray jsonArray = serverResponse.getJSONArray("get_wishlist");
                for (int i = 0; i < jsonArray.length(); i++) {
                    CakeIdArrayList.add(jsonArray.getJSONObject(i).getString("cake_id"));
                    CakeArrayList.add(jsonArray.getJSONObject(i).getString("cake_name"));
                    CakePhotoArrayList.add("http://www.foodytreat.com/" + jsonArray.getJSONObject(i).getString("cake_img"));
                    CakePriceList.add(jsonArray.getJSONObject(i).getString("price"));
                    CakeTypeBooleanList.add(jsonArray.getJSONObject(i).getString("cake_type"));
                    CakeRateArrayList.add(jsonArray.getJSONObject(i).getString("rating"));
                    CakeRatingCountList.add(jsonArray.getJSONObject(i).getString("no_of_people"));
//                    CakeRateArrayList.add("3.5");
//                    CakeRatingCountList.add("1000");
                    CakePresentInWishListArrayList.add("1");
                }
                setAdapter();
                Toast.makeText(SelectCake.this, serverResponse.getString(Responce.TAG_MESSAGE), Toast.LENGTH_LONG).show();
            } else {
                progress.dismiss();
                Toast.makeText(SelectCake.this, serverResponse.getString(Responce.TAG_MESSAGE), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
        }
    }
    /**finish webservices of "get wishlist" (3rd webservice) -by Vrushali added on 12/10/2015 */



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_cake_filter, menu);

        if(coming_from.equals("Coming from wishlist")){
            MenuItem filter=menu.findItem(R.id.filter);
            filter.setVisible(false);
        }
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
        if (id == R.id.filter) {

            Intent intent1 = new Intent(SelectCake.this, SelectCakeFilter.class);
            startActivity(intent1);
            finish();
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
                    Toast.makeText(SelectCake.this, "Logout successfull", Toast.LENGTH_SHORT).show();
                    editor.commit();
                    Intent inten = new Intent(SelectCake.this, LoginActivity.class);
                    startActivity(inten);
                }
                return true;
            case R.id.menu_viewCart:
                Intent intent = new Intent(SelectCake.this, Cart_Details.class);
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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(SelectCake.this);
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