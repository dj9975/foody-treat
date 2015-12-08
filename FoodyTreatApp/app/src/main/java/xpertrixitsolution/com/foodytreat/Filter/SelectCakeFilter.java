package xpertrixitsolution.com.foodytreat.Filter;
/**
 * @author Dhiraj Devkar
 * edited by: Vrushali Matale
 */

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import xpertrixitsolution.com.foodytreat.R;
import xpertrixitsolution.com.foodytreat.SelectCake;

public class SelectCakeFilter extends Activity implements  ActionBar.TabListener{

    Button btnApplyFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_cake_filter);

        ActionBar ab= getActionBar();
        ab.setNavigationMode(ab.NAVIGATION_MODE_TABS);

        ab.setDisplayShowHomeEnabled(false);
        ab.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.filter_actionbar_layout, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        // mTitleTextView.setText("Filters");

        ImageView imageButton = (ImageView) mCustomView
                .findViewById(R.id.ivClose);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ab.setCustomView(mCustomView);
        ab.setDisplayShowCustomEnabled(true);

        ActionBar.Tab tab_one=ab.newTab();
        ActionBar.Tab tab_two=ab.newTab();
        ActionBar.Tab tab_three=ab.newTab();
        ActionBar.Tab tab_four=ab.newTab();

        FirstFragment firstFragment = new FirstFragment();
        tab_one.setText("")
                .setIcon(R.drawable.filter_flavour)
                .setContentDescription("Flavour Filter")
                .setTabListener(new MyTabListener<FirstFragment>(
                        firstFragment));
        ab.addTab(tab_one);

        SecondFragment secondFragment = new SecondFragment();
        tab_two.setText("")
                .setIcon(R.drawable.filter_cost)
                .setContentDescription("Price Filter")
                .setTabListener( new MyTabListener<FirstFragment>(
                        secondFragment));
        ab.addTab(tab_two);

        ThirdFragment thirdFragment = new ThirdFragment();
        tab_three.setText("")
                .setIcon(R.drawable.filter_weight)
                .setContentDescription("Weight Filter")
                .setTabListener(new MyTabListener<FirstFragment>(
                        thirdFragment));
        ab.addTab(tab_three);

        //Fourth fragment to display fourth tab, -Vrushali on 09/10/15
        FourthFragment fourthFragment = new FourthFragment();
        tab_four.setText("")
                .setIcon(R.drawable.filter_more)
                .setContentDescription("More Filter")
                .setTabListener(new MyTabListener<FirstFragment>(
                        fourthFragment));
        ab.addTab(tab_four);


        if (savedInstanceState != null) {
           // Log.i(TAG, "setting selected tab from saved bundle");
//            get the saved selected tab's index and set that tab as selected
            ab.setSelectedNavigationItem(savedInstanceState.getInt("tabIndex", 0));
        }
            btnApplyFilter=(Button)findViewById(R.id.btnApplyFilter);
            btnApplyFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(SelectCakeFilter.this, SelectCake.class);
                    intent1.putExtra("coming_from","Coming from filter");
                    startActivity(intent1);
                    finish();
                }
            });
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

}
