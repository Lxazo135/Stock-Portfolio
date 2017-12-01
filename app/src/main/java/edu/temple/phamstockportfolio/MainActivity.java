package edu.temple.phamstockportfolio;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements PortfolioFragment.StockSelectable {

    public FragmentManager fm;
    public PortfolioFragment nav;
    public Fragment details;
    public EditText editText;
    public Button button;
    String symbolFromIntent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        nav = new PortfolioFragment();
        fm = getFragmentManager();
        fm.beginTransaction().
                add(R.id.viewFrame,nav).
                commit();

    }

    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        System.out.println("INTENT: " + intent.toString());
        if(intent.hasExtra("symbol")){
            symbolFromIntent = intent.getStringExtra("symbol");
            System.out.println("SYMBOLZZZZ: " + symbolFromIntent);
            nav.addSymbol(symbolFromIntent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    public void stockSelected(String symbol) {
        details = StockDetailsFragment.newInstance(symbol);

        fm.beginTransaction().
                replace(R.id.viewFrame,details).
                addToBackStack(null).
                commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.action_addSymbol:
                Intent searchIntent = new Intent(MainActivity.this, SymbolSearch.class);
                MainActivity.this.startActivity(searchIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
