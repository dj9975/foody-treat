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


public class FourthFragment extends Fragment implements CompletionListener {
    private  View view;
    int selectedPosition = -1;

    private ListView listOccassion,listCakeType;
    private ProgressDialog progress;
    ArrayList<String> OccassionNameArrayList = new ArrayList<String>();
    ArrayList<String> OccassionIdList = new ArrayList<String>();
    ArrayList<String> CakeTypeNameArrayList = new ArrayList<String>();
    ArrayList<String> CakeTypeIdList = new ArrayList<String>();
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fourth, container, false);//fragment_three layout is the layout for third tab in filter

        listOccassion=(ListView)view.findViewById(R.id.listOccassion);
        listCakeType=(ListView)view.findViewById(R.id.listCakeType);
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Loading");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
        progress.setCanceledOnTouchOutside(false);
        getCake_occassion_filter();
        getCake_type_filter();
        return view;
    }

    private void getCake_occassion_filter() {
        NetworkTask networkTaskAllRestaurants = new NetworkTask(this, getActivity(), false);
        List<NameValuePair> params = getRequestParams();
        networkTaskAllRestaurants.execute(params, Post_URL.URL_GET_CAKES_FILTER_OCCASSION, 1);
    }

    private void getCake_type_filter() {
        NetworkTask networkTaskAllRestaurants = new NetworkTask(this, getActivity(), false);
        List<NameValuePair> params = getRequestParams();
        networkTaskAllRestaurants.execute(params, Post_URL.URL_GET_CAKES_FILTER_CAKETYPE, 2);
    }

    private List<NameValuePair> getRequestParams() {
        // TODO Enter Comments Here ...
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        //  params.add(new BasicNameValuePair("flavour_name", String.valueOf(Util.FlavourFilter)));

        return params;
    }


    private void handleResponseGetOccassionFilter(JSONObject serverResponse) {

        ArrayList<String> OccassionNameArrayList = new ArrayList<String>();
        ArrayList<String> OccassionIdList = new ArrayList<String>();


        try {
            int  success = serverResponse.getInt("Success");

            if (success == 1) {
                JSONArray jsonArray = serverResponse.getJSONArray("occasions");

                for (int i = 0; i < jsonArray.length(); i++) {
                    OccassionNameArrayList.add(jsonArray.getJSONObject(i).getString("occasion_name"));
                    OccassionIdList.add(jsonArray.getJSONObject(i).getString("occasion_id"));
                }

                selectedPosition=-1;
                OccassionListAdapter myCakeTypeFilterAdapter = new OccassionListAdapter(getActivity(), OccassionNameArrayList,
                        OccassionIdList,"occassion");
                listOccassion.setAdapter(myCakeTypeFilterAdapter);

            }
            else {

                Toast.makeText(getActivity(), serverResponse.getString(Responce.TAG_MESSAGE), Toast.LENGTH_LONG).show();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void handleResponseGetCakeTypeFilter(JSONObject serverResponse) {

        ArrayList<String> CakeTypeNameArrayList = new ArrayList<String>();
        ArrayList<String> CakeTypeIdList = new ArrayList<String>();


        try {
            int  success = serverResponse.getInt("Success");

            if (success == 1) {
                JSONArray jsonArray = serverResponse.getJSONArray("cake_types");

                for (int i = 0; i < jsonArray.length(); i++) {
                    CakeTypeNameArrayList.add(jsonArray.getJSONObject(i).getString("cake_type"));
                    CakeTypeIdList.add(jsonArray.getJSONObject(i).getString("cake_type_id"));
                }
                progress.dismiss();
               selectedPosition=-1;
                OccassionListAdapter myCakeTypeFilterAdapter = new OccassionListAdapter(getActivity(),CakeTypeNameArrayList,
                        CakeTypeIdList,"cake type" );
                listCakeType.setAdapter(myCakeTypeFilterAdapter);

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
                handleResponseGetOccassionFilter(serverResponse);
                break;

            case 2:
                handleResponseGetCakeTypeFilter(serverResponse);
                break;

            default:
        }
    }

    //adapter for the listview
    public class OccassionListAdapter extends BaseAdapter {
        ArrayList<String> NameArrayList;
        ArrayList<String> IdList ;
        String adapterOf;
        Context context;
        LayoutInflater inflater;

        public OccassionListAdapter(Context context, ArrayList<String> OccassionNameArrayList,
                                    ArrayList<String> OccassionIdList,String adpaterOf) {
            this.context = context;

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (OccassionNameArrayList != null) {
                this.NameArrayList = OccassionNameArrayList;
                this.IdList = OccassionIdList;
                this.adapterOf=adpaterOf;
            }

        }

        @Override
        public int getCount() {
            return NameArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return NameArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            //frag one layout is used here
            View OccassionRow = inflater.inflate(R.layout.flavour_filter_list_item, null);

            final RadioButton rbSelectOccassionFilter = (RadioButton) OccassionRow.findViewById(R.id.rbSelectFlavourFilter);

            rbSelectOccassionFilter.setText(NameArrayList.get(position));

            rbSelectOccassionFilter.setChecked(position == selectedPosition);
            rbSelectOccassionFilter.setTag(position);
            //row onClickListener
            rbSelectOccassionFilter.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    selectedPosition = (Integer) v.getTag();
                    notifyDataSetChanged();
                    if (!rbSelectOccassionFilter.isChecked()) {
                        rbSelectOccassionFilter.setChecked(false);
                        if (adapterOf.equals("occassion")) {
                            Util.OccassionFilter = ""; //to clear string i.e. to disselect radiobutton ,13/10/2015

                        } else {
                            Util.CakeTypeFilter="";
                        }
                    } else {

                        rbSelectOccassionFilter.setChecked(true);
                        if (adapterOf.equals("occassion")) {
                            Util.OccassionFilter = "";
                            Util.OccassionFilter = IdList.get(position);//save id of occasssion not name
                        } else {
                            Util.CakeTypeFilter = "";
                            Util.CakeTypeFilter = IdList.get(position);//save cake type id not cake type
                        }

                    }
                }
            });

            return OccassionRow;
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}