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


public class ThirdFragment extends Fragment implements CompletionListener {

    int selectedPosition = -1;
    private ListView listWeight;
    private ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_three, container, false);//fragment_three layout is the layout for three tab in filter


        listWeight=(ListView)view.findViewById(R.id.listWeight);
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Loading");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
        progress.setCanceledOnTouchOutside(false);
        getCake_weight();
        return view;

    }

    private void getCake_weight() {
        NetworkTask networkTaskAllRestaurants = new NetworkTask(this, getActivity(), false);
        List<NameValuePair> params = getRequestParams();
        networkTaskAllRestaurants.execute(params, Post_URL.URL_GET_CAKES_FILTER_WEIGHT, 1);
    }

    private List<NameValuePair> getRequestParams() {
        // TODO Enter Comments Here ...
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        //  params.add(new BasicNameValuePair("flavour_name", String.valueOf(Util.FlavourFilter)));

        return params;
    }


    private void handleResponseGetCakeFilter(JSONObject serverResponse) {

        ArrayList<String> WeightArrayList = new ArrayList<String>();
        ArrayList<String> WeightArrayIdList = new ArrayList<String>();


        try {
            int  success = serverResponse.getInt("Success");

            if (success == 1) {
                JSONArray jsonArray = serverResponse.getJSONArray("weights");

                for (int i = 0; i < jsonArray.length(); i++) {
                    WeightArrayList.add(jsonArray.getJSONObject(i).getString("type"));
                    WeightArrayIdList.add(jsonArray.getJSONObject(i).getString("id"));
                }
                progress.dismiss();
                WeightListAdapter myCakeFilterAdapter = new WeightListAdapter(getActivity(), WeightArrayList,WeightArrayIdList );
                listWeight.setAdapter(myCakeFilterAdapter);

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
    public class WeightListAdapter extends BaseAdapter {
        ArrayList<String> WeightArrayList;
        ArrayList<String> WeightArrayIdList;
        Context context;
        LayoutInflater inflater;

        public WeightListAdapter(Context context, ArrayList<String> WeightArrayList, ArrayList<String> WeightArrayIdList) {
            this.context = context;

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (WeightArrayList != null) {
                this.WeightArrayList = WeightArrayList;
                this.WeightArrayIdList = WeightArrayIdList;
            }

        }

        @Override
        public int getCount() {
            return WeightArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return WeightArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            //frag one layout is used here
            View weightRow = inflater.inflate(R.layout.flavour_filter_list_item, null);

            final RadioButton rbSelectWeightFilter = (RadioButton) weightRow.findViewById(R.id.rbSelectFlavourFilter);

            rbSelectWeightFilter.setText(WeightArrayList.get(position)+" kg");

            rbSelectWeightFilter.setChecked(position == selectedPosition);
            rbSelectWeightFilter.setTag(position);
            //row onClickListener
            rbSelectWeightFilter.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    selectedPosition = (Integer) v.getTag();
                    notifyDataSetChanged();
                    if (!rbSelectWeightFilter.isChecked()) {

                        Util.WeightFilter=""; //to clear string i.e. to disselect radiobutton ,13/10/2015
                        rbSelectWeightFilter.setChecked(false);
                    } else {
                        rbSelectWeightFilter.setChecked(true);
                        Util.WeightFilter="";
                        Util.WeightFilter=WeightArrayIdList.get(position);//store weight id not actual weight
                    }
                }
            });

            return weightRow;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
