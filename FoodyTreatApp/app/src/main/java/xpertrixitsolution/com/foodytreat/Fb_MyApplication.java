package xpertrixitsolution.com.foodytreat;

/**
 * Created by Dhiraj on 10/24/2015.
 * VVVVVIMP for fb login must enter this key hash in log cat on developer website
 * and must call this in manifest
 */
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseInstallation;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Fb_MyApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        printHashKey();
    }

    public void printHashKey(){
        // Add code to print out the key hash
        Parse.initialize(this, "qRktIz1N4x7GgmR1Urjjyv0Rngh7QjXLsZ2Mcnuw", "kCb9x5hIGz9aEJKoPU7xAnUHqAsQlPPZI7svqnNE");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "xpertrixitpvtsolution.com.foodytreattest",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}
