package xpertrixitsolution.com.foodytreat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Dhiraj on 9/24/2015.
 */
public class Util extends Activity {

    public static ArrayList<String> CakeNameArrayList = new ArrayList<String>();
    public static ArrayList<String> CakeRateList = new ArrayList<String>();
    public static ArrayList<String> CakeWeightList = new ArrayList<String>();
    public static ArrayList<String> CakeQntList = new ArrayList<String>();
    public static ArrayList<String> CakeTotalList = new ArrayList<String>();
    public static ArrayList<String> CakePicList = new ArrayList<String>();
    public static ArrayList<String> CakeIdList = new ArrayList<String>();
    public static ArrayList<String> CakeMsgList = new ArrayList<String>();
    public static ArrayList<String> CakeDeliveryDateList = new ArrayList<String>();
    public static ArrayList<String> CakeDeliveryTimeList = new ArrayList<String>();
    public static ArrayList<String> CakeShippingChargeList = new ArrayList<String>();

    public static int SelectedAreaId;
    public static String SelectedAreaName;
    public static int SelectedVendorId;
    public static int FlagComingFromActivity=0;//to redirect user to prev activity after login
    public static int SelectedCakeId;

    public static int GrandTotal;//total without shipping charges

    //to generate bill after placing order
    public static String bill_order_id;
    public static int grandShipCharge;
    public static String bill_customer_name;
    public static String bill_contact_no;
    public static int bill_amount;

    public static String BillAddress;
    public static String ShippAddress;
    public static int FlagShippAddSame=1;
    public static int otp;
//for lazy loading

    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

    public static String getSizeName(Context context) {
        int density = context.getResources().getDisplayMetrics().densityDpi;
        switch (density)
        {

            case DisplayMetrics.DENSITY_HIGH:
                return "HDPI";
            case DisplayMetrics.DENSITY_LOW:
                return "LDPI";
            case DisplayMetrics.DENSITY_XHIGH:
                return "XHDPI";
            case DisplayMetrics.DENSITY_XXHIGH:
                return "XXHDPI";
            default:
                return "Unknown";
        }
    }

    public static void LoginAlert(final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("You need to Login First!");
        builder.setPositiveButton("Login Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Util.FlagComingFromActivity=1;
                context.startActivity(new Intent(context, LoginActivity.class));
            }
        });builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    //filter utils

    public static int CostFilterLowerLimit;

    public static int CostFilterUpperLimit;

    // used in FourthFragment.java
    public static String OccassionFilter;
    public static String CakeTypeFilter;


    // used in FirstFragment.java
    public static String FlavourFilter;

    public static String WeightFilter; // used in third filter , added on 13/10/2015
}
