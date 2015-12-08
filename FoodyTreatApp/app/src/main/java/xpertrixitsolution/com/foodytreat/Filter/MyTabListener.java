package xpertrixitsolution.com.foodytreat.Filter;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;

import xpertrixitsolution.com.foodytreat.R;

public class MyTabListener<T extends Fragment> implements ActionBar.TabListener {

    private Fragment fragment;
    private static final String TAG = "junk";

    public MyTabListener(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Log.i(TAG, "Tab " + tab.getText() + " ReSelected");
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        fragmentTransaction.replace(R.id.container, fragment, null);
        Log.i(TAG, "Tab " + tab.getText() + " selected");

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Log.i(TAG, "Tab " + tab.getText() + " UnSelected");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
    }
}
