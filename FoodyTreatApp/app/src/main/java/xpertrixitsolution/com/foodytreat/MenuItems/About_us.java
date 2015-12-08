package xpertrixitsolution.com.foodytreat.MenuItems;
/**
 * @author: Vrushali Matale created on 15/10/2015
 */
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import xpertrixitsolution.com.foodytreat.MainActivity;
import xpertrixitsolution.com.foodytreat.R;

public class About_us extends Activity implements View.OnClickListener {
    TextView tvAboutUs,tvWebsite;
    TextView tvFeedback;
    private ShareActionProvider mShareActionProvider;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about_us);
        tvAboutUs=(TextView)findViewById(R.id.tvAboutUs);

        ImageView ivFb = (ImageView)findViewById(R.id.ivFb);
        ImageView ivTwitter = (ImageView)findViewById(R.id.ivTwitter);
        ImageView ivGoogleplus = (ImageView)findViewById(R.id.ivGoogleplus);
        ImageView ivLinkedin = (ImageView)findViewById(R.id.ivLinkedin);

        ivFb.setOnClickListener(this);
        ivTwitter.setOnClickListener(this);
        ivGoogleplus.setOnClickListener(this);
        ivLinkedin.setOnClickListener(this);

        TextView textView = (TextView) findViewById(R.id.tvfooter);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.xpertrixit.in/"));
                startActivity(intent);
            }
        });
        tvWebsite=(TextView)findViewById(R.id.tvWebsite);
        tvWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.foodytreat.com/"));
                startActivity(intent);
            }
        });

        tvFeedback=(TextView)findViewById(R.id.tvFeedback);
        tvFeedback.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent email = new Intent(Intent.ACTION_SEND);
                String[] recipients = new String[]{"contactus@foodytreat.com"};
                email.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
                //email.putExtra(Intent.EXTRA_EMAIL, new String[{"mvrushali03@gmail.com"}]);
                email.putExtra(Intent.EXTRA_SUBJECT, "Feedback for FoodyTreat Android app");
                //email.putExtra(Intent.EXTRA_TEXT, "message");
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));

            }
        });

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ivFb:
                Intent i1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/foodytreatpune"));
                startActivity(i1);
                break;

            case R.id.ivGoogleplus:
                Intent i2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/112103738571444877346/posts"));
                startActivity(i2);
                break;

            case R.id.ivLinkedin:
                Intent i3 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://in.linkedin.com/pub/foody-treat/107/2a2/b77"));
                startActivity(i3);
                break;

            case R.id.ivTwitter:
                Intent i4 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/foodytreat"));
                startActivity(i4);
                break;


        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
/** Inflating the current activity's menu with res/menu/items.xml */
        getMenuInflater().inflate(R.menu.menu_about_us, menu);

        /** Getting the actionprovider associated with the menu item whose id is share */
        mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.action_share).getActionProvider();

        /** Setting a share intent */
        mShareActionProvider.setShareIntent(getDefaultShareIntent());

        return super.onCreateOptionsMenu(menu);
    }
    /** Returns a share intent */
    private Intent getDefaultShareIntent(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Download Foody Treat App, Now you get your cake delivered with few clicks.");
        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=xpertrixitsolution.com.foodytreat");
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_main_activity) {
            Intent intent = new Intent(this, MainActivity.class);
            //Start Activity
            startActivity(intent);
            return true;
        }
        return true;
    }
}
