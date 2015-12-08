package xpertrixitsolution.com.foodytreat.Filter;
/**
 * @author Vrushali Matale, created on 09/10/2015
 *
 */

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

import xpertrixitsolution.com.foodytreat.R;
import xpertrixitsolution.com.foodytreat.SelectCake;
import xpertrixitsolution.com.foodytreat.Util;


public class SecondFragment extends Fragment  implements View.OnClickListener {
    private  View view;

    RadioGroup rgSelectPriceFilter;
    private RadioButton rbCost1, rbCost2, rbCost3, rbCost4;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_two, container, false);//fragment_three layout is the layout for third tab in filter


        rgSelectPriceFilter=(RadioGroup)view.findViewById(R.id.rgSelectPriceFilter);
        rgSelectPriceFilter.clearCheck();
        rbCost1=(RadioButton)view.findViewById(R.id.rbCost1);
        rbCost2=(RadioButton)view.findViewById(R.id.rbCost2);
        rbCost3=(RadioButton)view.findViewById(R.id.rbCost3);
        rbCost4=(RadioButton)view.findViewById(R.id.rbCost4);

        rbCost1.setOnClickListener(this);
        rbCost2.setOnClickListener(this);
        rbCost3.setOnClickListener(this);
        rbCost4.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rbCost1:
                Util.CostFilterLowerLimit = 0;
                Util.CostFilterUpperLimit = 500;
                break;

            case R.id.rbCost2:
                Util.CostFilterLowerLimit = 500;
                Util.CostFilterUpperLimit = 1000;
                break;

            case R.id.rbCost3:
                Util.CostFilterLowerLimit = 1000;
                Util.CostFilterUpperLimit = 1500;
                break;

            case R.id.rbCost4:
                Util.CostFilterLowerLimit = 1500;
                Util.CostFilterUpperLimit = 50000;
                break;

        }
    }


    }

