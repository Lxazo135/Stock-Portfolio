package edu.temple.phamstockportfolio;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SYMBOL = "param1";
    private static final String JSON = "param2";

    // TODO: Rename and change types of parameters
    private String symbol;
    private String json;
    View v;
    JSONObject jsonObject;
    String result;
    final String chartHTTP = "https://finance.google.com/finance/getchart?p=5d&q=";


    public DetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment StockDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(SYMBOL, param1);
        args.putString(JSON, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            symbol = getArguments().getString(SYMBOL);
            json = getArguments().getString(JSON);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_details, container, false);
        TextView symbol = (TextView) v.findViewById(R.id.symbol);
        TextView name = (TextView) v.findViewById(R.id.stockName);
        TextView price = (TextView) v.findViewById(R.id.stockPrice);
        symbol.setText(this.symbol);

        try {
            jsonObject = new JSONObject(json);
            name.setText("Name: " + jsonObject.getString("Name"));
            price.setText("Price: " + jsonObject.getString("LastPrice"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebView chart = (WebView) v.findViewById(R.id.stockChart);
        String url = chartHTTP + this.symbol;
        chart.loadUrl(url);

        return v;
    }

}

