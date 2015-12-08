package xpertrixitsolution.com.foodytreat;
/**
 * @author: Dhiraj devkar
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;

import com.parse.Parse;
import com.parse.ParseInstallation;

public class SplashScreen extends Activity {

    private SharedPreferences sharedPreferences;
    private String customer_id;
    private LinearLayout linearLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** Get the view from splashscreen.xml*/
        setContentView(R.layout.activity_splash_screen);
        linearLayout=(LinearLayout)findViewById(R.id.linearSplash);
        String screenSize= Util.getSizeName(this);
        if(screenSize.equals("HDPI")){
            linearLayout.setBackgroundResource(R.drawable.splash_back_mdpi);
        }else if(screenSize.equals("LDPI")){
            linearLayout.setBackgroundResource(R.drawable.splash_back_mdpi);
        }else if(screenSize.equals("XHDPI")){
            linearLayout.setBackgroundResource(R.drawable.splash_back_xhdpi);
        }else if(screenSize.equals("XXHDPI")){
            linearLayout.setBackgroundResource(R.drawable.splash_back_xxhdpi);
        }else{
            linearLayout.setBackgroundResource(R.drawable.splash_back_mdpi);
        }



        //get customer id after login through shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        customer_id = sharedPreferences.getString("cust_id", "0");//if no customerid found automatically 0 will be used
        /**set the timer for splashscreen using Thread*/
        Thread logoTimer = new Thread() {
            public void run(){
                /**try_catch block to handle InterruptedException*/
                try{
                    int logoTimer = 0;
                    while(logoTimer <2000){
                        sleep(100);
                        logoTimer = logoTimer +100;
                    };

                    // Run next activity
                        Intent intent = new Intent();
                        if (customer_id.equals("0")) {
                            intent.setClass(SplashScreen.this, LoginActivity.class);
                        } else {
                            intent.setClass(SplashScreen.this, MainActivity.class);
                        }
                        startActivity(intent);
                        finish();
                }

                catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                finally{
                    finish();
                }
            }
        };

        logoTimer.start();
    }


}