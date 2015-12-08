package xpertrixitsolution.com.foodytreat;

/**
 * Created by Dhiraj on 10/26/2015.
 * Class used to send otps and order placed sms to the user
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;

/**
 * Class created to send order details via sms to the user
 * asyncTask is used coz you cant execute networking operations on main thread
 */
public class TaskExample extends AsyncTask<Void,Void,Void> {
    int otp;
    String Contact_no,sendMsgAs,message;
    public TaskExample(String contact_no,String sendMsgAs) {//send msg as otp or order placed msg
        super();
        // do stuff
        this.Contact_no=contact_no;
        this.sendMsgAs=sendMsgAs;
    }

    protected Void doInBackground(Void... params) {
//            //random code generator
            int min = 1000;
            int max = 10000;
            //min inclusive max exclusive
            Random r = new Random();
            otp = r.nextInt(max - min + 1) + min;

        String authkey = "rutul";
//Multiple mobiles numbers separated by comma
        String mobiles = Contact_no;
        Log.e("Mobile No",Contact_no);
//Sender ID,While using route4 sender id should be 6 characters long.
        String senderId = "FOODYT";

        if(sendMsgAs.equals("otp")) {
            message = "Hii,Your OTP with Foody Treat is " + otp + ".\n Happy Ordering!";
        }else{//order placed msg
            message="Hii "+Util.bill_customer_name+", Your Foody Treat order with order id "+Util.bill_order_id+"" +
                    " has been placed successfully.\n Happy Ordering!";
        }
        Util.otp=otp;
        Log.e("otp", String.valueOf(otp));
        URLConnection myURLConnection = null;
        URL myURL = null;
        BufferedReader reader = null;

        String encoded_message = URLEncoder.encode(message);

        String mainUrl = "http://www.smsjust.com/sms/user/urlsms.php?";

        StringBuilder sbPostData = new StringBuilder(mainUrl);
        sbPostData.append("username=" + authkey);
        sbPostData.append("&pass=" + "123456");
        sbPostData.append("&senderid=" + senderId);
        sbPostData.append("&dest_mobileno=" + mobiles);
        sbPostData.append("&message=" + encoded_message);
//final string
        mainUrl = sbPostData.toString();
        try {
            //prepare connection
            myURL = new URL(mainUrl);
            myURLConnection = myURL.openConnection();
            myURLConnection.connect();
            reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

            //reading response
            String response;
            while ((response = reader.readLine()) != null)
                //print response
                Log.d("RESPONSE", "" + response);
            //finally close connection
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}