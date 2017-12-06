package edu.temple.phamstockportfolio;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements PortfolioFragment.StockSelectable {

    public StockDetailsService server;
    public FragmentManager fm;
    public PortfolioFragment nav;
    public Fragment details;
    public EditText editText;
    public Button button;
    String symbolFromIntent = "";
    final String HTTP = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=";
    final String FILE_NAME = "Stocks.txt";
    String json;
    Handler handler;
    String jsonFromFile;
    Boolean first = true;
    Boolean bounded;

    ServiceConnection serverConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            StockDetailsService.LocalBinder binder = (StockDetailsService.LocalBinder)iBinder;
            server = binder.getServerInstance();
            bounded = true;
            setListView();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onStart(){
        super.onStart();

        Intent serviceIntent = new Intent(this, StockDetailsService.class);
        this.bindService(serviceIntent, serverConnection, Context.BIND_AUTO_CREATE);
    }

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
    protected void onStop(){
        super.onStop();
        if(bounded){
            unbindService(serverConnection);
            bounded = false;
        }
    }

    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        System.out.println("INTENT: " + intent.toString());
        handler = new Handler();
        if(intent.hasExtra("symbol")){
            symbolFromIntent = intent.getStringExtra("symbol");
            System.out.println("SYMBOL FROM INTENT: " + symbolFromIntent);
            nav.addSymbol(symbolFromIntent);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    json = server.getJson(HTTP + symbolFromIntent);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                System.out.println("JSON FROM SERVER: " + json);
                                if(first){
                                    first = false;
                                }
                                server.writeJsonToFile(symbolFromIntent, json, FILE_NAME);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            try {
                                jsonFromFile = server.readFile(FILE_NAME);
                                System.out.println("$$$$$ JSON FROM FILE: " + jsonFromFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            thread.start();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    public void stockSelected(String symbol, int index) {
        String fileJson = null;
        try {
            fileJson = server.readLine(FILE_NAME, (index * 2) + 1);//getting index of actual json in file
        } catch (IOException e) {
            e.printStackTrace();
        }
        details = DetailsFragment.newInstance(symbol, fileJson);

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
            case R.id.action_clearSymbols:
                try {
                    server.clearFile(FILE_NAME);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
                startActivity(getIntent());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setListView(){
        String symbol;
        int i = 0;
        try {
            while(!(symbol = server.readLine(FILE_NAME, i)).equals("null")){
                nav.addSymbol(symbol);
                i += 2;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
