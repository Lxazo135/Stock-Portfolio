package edu.temple.phamstockportfolio;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PortfolioFragment extends Fragment {
    private View v;
    private ArrayAdapter adapter;
    private ArrayList<String> symbols;
    private ListView listView;
    public PortfolioFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        symbols = new ArrayList<String>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_portfolio, container, false);
        listView = (ListView) v.findViewById(R.id.listView);
        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, symbols);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((MainActivity)getActivity()).stockSelected(symbols.get(i), i);
            }
        });

        if(listView.getCount() == 0){
            TextView emptyText = (TextView) v.findViewById(R.id.empty);
            listView.setEmptyView(emptyText);
        }

        return v;
    }

    public void addSymbol(String symbol){
        symbols.add(symbol);
        adapter.notifyDataSetChanged();
        System.out.println("Array list: " + symbols);
    }

    public interface StockSelectable{
        public void stockSelected(String symbol, int index);
    }

    public void changeColor(String color){
        v.setBackgroundColor(Color.parseColor(color));
    }
}
