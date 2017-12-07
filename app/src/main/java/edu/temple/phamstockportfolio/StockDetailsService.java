package edu.temple.phamstockportfolio;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class StockDetailsService extends Service {

    IBinder mBinder = new LocalBinder();
    final private String FILE_NAME = "Stocks.txt";
    final String HTTP = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("SERVICE BOUND");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(30000);
                        ArrayList<String> symbols = getSymbols(FILE_NAME);
                        String json;
                        String symbol;

                        clearFile(FILE_NAME);
                        for (int i = 0; i < symbols.size(); i++) {
                            symbol = symbols.get(i);
                            json = getJson(HTTP + symbol);
                            writeJsonToFile(symbol, json, FILE_NAME);
                        }
                        String file = readFile(FILE_NAME);
                        System.out.print("FILE UPDATE IN SERVICE: " + file);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        System.out.println("SERVICE START");
        return START_NOT_STICKY;
    }

    public class LocalBinder extends Binder {
        public StockDetailsService getServerInstance() {
            return StockDetailsService.this;
        }
    }

    @Override
    public void onCreate(){

    }

    public String getJson(String http){
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();

        try{
            URL url = new URL(http);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));

            String line = "";
            while((line = reader.readLine())!= null){
                buffer.append(line + "\n");
                Log.d("Response: ", line);
            }

            //return buffer.toString();

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
        return buffer.toString();
    }

    public void writeJsonToFile(String symbol, String json, String fileName) throws IOException {

        File stockFile = new File(this.getFilesDir(), fileName);
        if(!stockFile.exists()){
            stockFile.createNewFile();
        }
        FileWriter fw = new FileWriter(stockFile, true);
        fw.append(symbol);
        fw.append("\n");
        fw.append(json);
        fw.flush();
        fw.close();
    }

    public void clearFile(String fileName) throws IOException {
        File stockFile = new File(this.getFilesDir(), fileName);
        if(!stockFile.exists()){
            stockFile.createNewFile();
        }

        FileWriter fw = new FileWriter(stockFile);
        fw.append("");
        fw.flush();
        fw.close();
    }

    public String readFile(String fileName) throws IOException {
        String filePath = this.getFilesDir() + "/" + fileName;
        File file = new File(filePath);

        FileInputStream fis = this.openFileInput(fileName);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }

        bufferedReader.close();
        isr.close();
        fis.close();

        return sb.toString();
    }

    public String readLine(String fileName,int index) throws IOException {
        String filePath = this.getFilesDir() + "/" + fileName;
        File file = new File(filePath);

        FileInputStream fis = this.openFileInput(fileName);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < index; i++){
            bufferedReader.readLine();
        }

        sb.append(bufferedReader.readLine());
        bufferedReader.close();
        isr.close();
        fis.close();
        return sb.toString();
    }
    
    public ArrayList<String> getSymbols(String fileName){
        ArrayList<String> symbols = new ArrayList<String>();
        String symbol;
        int i = 0;
        try {
            while(!(symbol = readLine(fileName, i)).equals("null")){
                symbols.add(symbol);
                i += 2;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return symbols;
    }
}
