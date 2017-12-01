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

public class StockDetailsService extends Service {

    IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
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

    public void writeJsonToFile(String json, String fileName) throws IOException {

        File stockFile = new File(this.getFilesDir(), fileName);
        if(!stockFile.exists()){
            stockFile.createNewFile();
        }
        FileWriter fw = new FileWriter(stockFile, true);
        fw.append(json);
        fw.append("\n");
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

    public String readJsonFromFile(String fileName) throws IOException {
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

        return sb.toString();
    }
}
