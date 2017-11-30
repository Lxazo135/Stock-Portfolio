package edu.temple.phamstockportfolio;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StockDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StockDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;
    View v;
    JSONObject jsonObject;
    String result;
    String HTML = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=";
    String jsonString = "";


    public StockDetailsFragment() {
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
    public static StockDetailsFragment newInstance(String param1) {
        StockDetailsFragment fragment = new StockDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_stock_details, container, false);
        TextView symbol = (TextView) v.findViewById(R.id.symbol);
        symbol.setText(mParam1);
        HTML += mParam1;

        JsonTask jsonTask = new JsonTask();
        jsonTask.execute(HTML);
        try {
            jsonString = jsonTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String strStockName = null;
        try {
            strStockName = jsonObject.getString("Name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView stockName = (TextView) v.findViewById(R.id.stockName);
        stockName.setText("Name: " + strStockName);

        String strStockPrice = null;
        try {
            strStockPrice = jsonObject.getString("LastPrice");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TextView price = (TextView) v.findViewById(R.id.stockPrice);
        price.setText("Price: " + strStockPrice);

        return v;
    }

}

class JsonTask extends AsyncTask<String, String, String>{

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try{
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";
            while((line = reader.readLine())!= null){
                buffer.append(line + "\n");
                Log.d("Response: ", line);
            }

            return buffer.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(connection != null){
                connection.disconnect();
            }
            try{
                if (reader != null){
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}

