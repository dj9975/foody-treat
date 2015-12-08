package xpertrixitsolution.com.foodytreat;

/**
 * @author Vrushali Matale,
 *
 * edited by Dhiraj
 *
 * last edited on 29/10/2015 , fb logout from program without using fb logout button
 */
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import xpertrixitsolution.com.foodytreat.MenuItems.About_us;
import xpertrixitsolution.com.foodytreat.MenuItems.EditPersonalProfile;
import xpertrixitsolution.com.foodytreat.MenuItems.MenuFAQ;
import xpertrixitsolution.com.foodytreat.MenuItems.MenuItemOrderHistory;
import xpertrixitsolution.com.foodytreat.MenuItems.MenuPolicies;

public class AddressDetails extends Activity {
    private String cust_id;
    private EditText edBuildingName,edRoadName,edArea,edPincode;
    private EditText edBuildingNameShipping,edRoadNameShipping,edAreaShipping,edPincodeShipping,edEnterState,edEnterCity,edEnterCityShipping,edEnterStateShipping;
    private CheckBox cbShipAddSame;
    private Button btnSaveAddress,btnShippingAddress;
    private int FlagShippAddSame,FlagValidShipAddress,FlagValidBillAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_details);

     getSharedPref();
        initviews();

        cbShipAddSame.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Util.FlagShippAddSame=1;
                    FlagShippAddSame = 1;
                    FlagValidShipAddress = 1;
                    edBuildingNameShipping.setVisibility(View.GONE);
                    edRoadNameShipping.setVisibility(View.GONE);
                    edAreaShipping.setVisibility(View.GONE);
                    edPincodeShipping.setVisibility(View.GONE);
                    edEnterCityShipping.setVisibility(View.GONE);
                    edEnterStateShipping.setVisibility(View.GONE);
                    btnShippingAddress.setVisibility(View.GONE);
                } else {
                    FlagShippAddSame = 0;
                    FlagValidShipAddress = 0;
                    Util.FlagShippAddSame=0;
                    edBuildingNameShipping.setVisibility(View.VISIBLE);
                    edRoadNameShipping.setVisibility(View.VISIBLE);
                    edAreaShipping.setVisibility(View.VISIBLE);
                    edPincodeShipping.setVisibility(View.VISIBLE);
                    edEnterCityShipping.setVisibility(View.VISIBLE);
                    edEnterStateShipping.setVisibility(View.VISIBLE);
                    btnShippingAddress.setVisibility(View.VISIBLE);
                }
            }
        });

        btnSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateBillAddress();
                Util.BillAddress = edBuildingName.getText().toString()+", "+ edRoadName.getText().toString()+", "+ edArea.getText().toString()+", " + edPincode.getText().toString();
                Util.ShippAddress = edBuildingNameShipping.getText().toString()+", "+ edRoadNameShipping.getText().toString()+", "+ edAreaShipping.getText().toString()+", "+ edPincodeShipping.getText().toString();
                if ( Util.BillAddress.isEmpty() && FlagShippAddSame == 1) {
                    validateBillAddress();
                } else if (FlagShippAddSame == 0 && Util.ShippAddress.isEmpty()) {
                    if (Util.BillAddress.isEmpty()) {
                        validateBillAddress();
                    }
                    validateShipAddress();
                } else if (FlagShippAddSame == 0) {
                    validateShipAddress();
                } else if (FlagValidBillAddress == 1 && FlagValidShipAddress == 1) {
                   Delivery_Details.btnEnterAddress.setText(Util.BillAddress);
                    finish();
                }
            }
        });

    }

    public void getSharedPref(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        cust_id= sharedPreferences.getString("cust_id", "0");

    }
    @Override
    public void onBackPressed() {
     validateBillAddress();
        if(FlagShippAddSame==0){
            validateShipAddress();
        }
    }

    private void initviews() {

        btnSaveAddress=(Button)findViewById(R.id.btnSaveAddress);
        edBuildingName=(EditText)findViewById(R.id.edBuildingName);
        edRoadName=(EditText)findViewById(R.id.edRoadName);
        edArea=(EditText)findViewById(R.id.edArea);
        edPincode=(EditText)findViewById(R.id.edPincode);
        edEnterCity=(EditText)findViewById(R.id.edEnterCity);
        edEnterState=(EditText)findViewById(R.id.edEnterState);

        cbShipAddSame=(CheckBox)findViewById(R.id.cbShipAddSame);

        edBuildingNameShipping=(EditText)findViewById(R.id.edBuildingNameShipping);
        edRoadNameShipping=(EditText)findViewById(R.id.edRoadNameShipping);
        edAreaShipping=(EditText)findViewById(R.id.edAreaShipping);
        edPincodeShipping=(EditText)findViewById(R.id.edPincodeShipping);
        edEnterCityShipping=(EditText)findViewById(R.id.edEnterCityShipping);
        edEnterStateShipping=(EditText)findViewById(R.id.edEnterStateShipping);
        btnShippingAddress=(Button)findViewById(R.id.btnShippingAddress);

    }

    public void validateBillAddress(){
        String billBuildingName=edBuildingName.getText().toString();
        String billRoadName=edRoadName.getText().toString();
        String billArea=edArea.getText().toString();
        String billPincode=edPincode.getText().toString();

        if(billBuildingName.isEmpty()){
            edBuildingName.setError("Please enter building name!");
        }else if(billRoadName.isEmpty()){
            edRoadName.setError("Please enter road name");
        }else if(billArea.isEmpty()){
            edArea.setError("Please enter area name");
        }else if(billPincode.isEmpty()){
            edPincode.setError("Please enter pincode");
        }else{
            FlagValidBillAddress=1;
        }

    }

    public void validateShipAddress(){
        String shipBuildingName=edBuildingNameShipping.getText().toString();
        String shipRoadName=edRoadNameShipping.getText().toString();
        String shipArea=edAreaShipping.getText().toString();
        String shipPincode=edPincodeShipping.getText().toString();

        if(shipBuildingName.isEmpty()){
            edBuildingNameShipping.setError("Please enter building name!");
        }else if(shipRoadName.isEmpty()){
            edRoadNameShipping.setError("Please enter road name");
        }else if(shipArea.isEmpty()){
            edAreaShipping.setError("Please enter area name");
        }else if(shipPincode.isEmpty()){
            edPincodeShipping.setError("Please enter pincode");
        }else{
            FlagValidBillAddress=1;
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                getSharedPref();
                if (cust_id.equals("0")) {

                } else {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("cust_id", "0");
                    editor.putString("cust_email_id", "");
                    Toast.makeText(AddressDetails.this, "Logout successfull", Toast.LENGTH_SHORT).show();
                    editor.commit();
                    Intent inten = new Intent(AddressDetails.this, LoginActivity.class);
                    startActivity(inten);

                }
                return true;
            case R.id.menu_viewCart:
                Intent intent = new Intent(AddressDetails.this, Cart_Details.class);
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

            case R.id.menu_faq:
                Intent menu_faq = new Intent(this, MenuFAQ.class);
                startActivity(menu_faq);
                break;

            case R.id.menu_trackorder:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(AddressDetails.this);
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
