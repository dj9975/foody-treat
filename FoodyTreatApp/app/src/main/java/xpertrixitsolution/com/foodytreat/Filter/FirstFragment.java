package xpertrixitsolution.com.foodytreat.Filter;
/**
 * @author Vrushali Matale, created on 09/10/2015
 *
 */
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import xpertrixitsolution.com.foodytreat.R;
import xpertrixitsolution.com.foodytreat.SelectCake;
import xpertrixitsolution.com.foodytreat.Util;
import xpertrixitsolution.com.foodytreat.network.CompletionListener;
import xpertrixitsolution.com.foodytreat.network.NetworkTask;
import xpertrixitsolution.com.foodytreat.network.Post_URL;
import xpertrixitsolution.com.foodytreat.network.Responce;


public class FirstFragment extends Fragment implements CompletionListener {

    int selectedPosition = -1;
    public static ListView listFlavour;
    private NetworkTask networkTask;
    private ProgressDialog progress;

    ArrayList<String> FlavourArrayList = new ArrayList<String>();
    ArrayList<String> FlavourArrayIDList = new ArrayList<String>();
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState){
        final View view = inflater.inflate(R.layout.fragment_one, container, false);//fragment_one layout is the layout for first tab in filter

        listFlavour=(ListView)view.findViewById(R.id.listFlavour);
        listFlavour.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        progress = new ProgressDialog(getActivity());
        progress.setMessage("Loading");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
        progress.setCanceledOnTouchOutside(false);
        get_cake_filter();
        return view;
    }
    private void get_cake_filter() {
        NetworkTask networkTaskAllRestaurants = new NetworkTask(this, getActivity(), false);
        List<NameValuePair> params = getRequestParams();
        networkTaskAllRestaurants.execute(params, Post_URL.URL_GET_CAKES_FILTER_FLAVOUR, 1);
    }

    private List<NameValuePair> getRequestParams() {
        // TODO Enter Comments Here ...
        List<NameValuePair> params = new ArrayList<NameValuePair>();
      //  params.add(new BasicNameValuePair("flavour_name", String.valueOf(Util.FlavourFilter)));
        return params;
    }


    private void handleResponseGetCakeFilter(JSONObject serverResponse) {

         ArrayList<String> FlavourArrayList = new ArrayList<String>();
        ArrayList<String> FlavourArrayIDList = new ArrayList<String>();


        try {
            int  success = serverResponse.getInt("Success");

            if (success == 1) {
                JSONArray jsonArray = serverResponse.getJSONArray("flavours");

                for (int i = 0; i < jsonArray.length(); i++) {
                    FlavourArrayList.add(jsonArray.getJSONObject(i).getString("flavour_name"));
                    FlavourArrayIDList.add(jsonArray.getJSONObject(i).getString("flavour_id"));
                }
                progress.dismiss();
                FlavourListAdapter myCakeFilterAdapter = new FlavourListAdapter(getActivity(), FlavourArrayList,FlavourArrayIDList );
                listFlavour.setAdapter(myCakeFilterAdapter);

            }
            else {
                progress.dismiss();
                Toast.makeText(getActivity(), serverResponse.getString(Responce.TAG_MESSAGE), Toast.LENGTH_LONG).show();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onComplete(JSONObject serverResponse, int RESPONSE_IDENTIFIER_FLAG) throws JSONException {
        switch (RESPONSE_IDENTIFIER_FLAG) {
            case 1:
                handleResponseGetCakeFilter(serverResponse);
                break;

            default:
        }
    }

    //adapter for the listview
    public class FlavourListAdapter extends BaseAdapter {
        ArrayList<String> FlavourNameList;
        ArrayList<String> FlavourIdList;
        Context context;
        LayoutInflater inflater;

        public FlavourListAdapter(Context context, ArrayList<String> FlavourArrayList, ArrayList<String> FlavourArrayIDList) {
            this.context = context;

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (FlavourArrayList != null) {
                this.FlavourNameList = FlavourArrayList;
                this.FlavourIdList = FlavourArrayIDList;
            }

        }

        @Override
        public int getCount() {
            return FlavourNameList.size();
        }

        @Override
        public Object getItem(int position) {
            return FlavourNameList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            int value;

            View flavourRow = inflater.inflate(R.layout.flavour_filter_list_item, null);

            final RadioButton rbSelectFlavourFilter = (RadioButton) flavourRow.findViewById(R.id.rbSelectFlavourFilter);

            rbSelectFlavourFilter.setText(FlavourNameList.get(position));
//
            rbSelectFlavourFilter.setChecked(position == selectedPosition);
            rbSelectFlavourFilter.setTag(position);
            //row onClickListener
            rbSelectFlavourFilter.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    selectedPosition = (Integer) v.getTag();
                    notifyDataSetChanged();
                    if (!rbSelectFlavourFilter.isChecked()) {

                        Util.FlavourFilter=""; //to clear string i.e. to disselect radiobutton ,13/10/2015
                        rbSelectFlavourFilter.setChecked(false);
                    } else {
                        rbSelectFlavourFilter.setChecked(true);
                        Util.FlavourFilter="";
                        Util.FlavourFilter=FlavourIdList.get(position);//store flavour id not name
                        Log.e("flavour filter",Util.FlavourFilter);

                    }
                }
            });

            return flavourRow;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
