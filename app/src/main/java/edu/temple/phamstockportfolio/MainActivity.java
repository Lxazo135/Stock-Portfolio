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

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements PortfolioFragment.StockSelectable {

    public StockDetailsService server;
    public FragmentManager fm;
    public PortfolioFragment nav;
    public Fragment details;
    String symbolFromIntent = "";
    final String HTTP = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=";
    final String FILE_NAME = "Stocks.txt";
    String json;
    Handler handler;
    String jsonFromFile;
    Boolean bounded;
    Boolean twoPane = false;
    private static final String NAV_FRAG_TAG = "NavFrag";

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        fm = getFragmentManager();
        nav = (PortfolioFragment) fm.findFragmentByTag(NAV_FRAG_TAG);
        if(nav == null){
            nav = new PortfolioFragment();
            fm.beginTransaction().
                    add(R.id.viewFrame,nav,NAV_FRAG_TAG).
                    commit();
        }
        else{
            fm.beginTransaction().
                    replace(R.id.viewFrame,nav,NAV_FRAG_TAG).
                    commit();
        }

        if(findViewById(R.id.detailsFrame) != null){
            twoPane = true;
        }

        Intent serviceIntent = new Intent(this, StockDetailsService.class);
        this.bindService(serviceIntent, serverConnection, Context.BIND_AUTO_CREATE);
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

        if(twoPane){
            if(fm.findFragmentById(R.id.detailsFrame) == null) {
                fm.beginTransaction().
                        add(R.id.detailsFrame, details).
                        commit();
            }
            else{
                fm.beginTransaction().
                        replace(R.id.detailsFrame, details).
                        commit();
            }
        }
        else{
            if(fm.findFragmentById(R.id.topDetailsFrame) == null){
                fm.beginTransaction().
                        add(R.id.viewFrame,details).
                        show(details).
                        hide(nav).
                        commit();
            }
            else{
                fm.beginTransaction().
                        replace(R.id.viewFrame,details).
                        hide(nav).
                        show(details).
                        commit();
            }
        }

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
            case R.id.action_back:
                if(!twoPane && !fm.findFragmentByTag(NAV_FRAG_TAG).isVisible()) {
                    fm.beginTransaction().
                        setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).
                        hide(details).
                        show(nav).
                        commit();
                }
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
