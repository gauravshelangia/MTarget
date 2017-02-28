package com.example.gaurav.mtarget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.gaurav.mtarget.MainActivity.ip;

/**
 * Created by gaurav on 28/2/17.
 */

public class GetandSend extends AsyncTask {
    private WifiManager wifimanager;

    public GetandSend(WifiManager wifimanager){
        this.wifimanager = wifimanager;
    }

    class WifiReciever extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Starting scan");
            ArrayList<Listitem> wlist = new ArrayList<Listitem>();
            wlist.clear();
            List<ScanResult> wifiList;
            wifiList = wifimanager.getScanResults();
            for (ScanResult result : wifiList) {
                Listitem LI = new Listitem();
                LI.ssid = result.SSID;
                LI.bssid = result.BSSID;
                LI.strength = result.level;
                LI.freq = result.frequency;
                wlist.add(LI);
                System.out.println("SSID :  " + LI.ssid + "\t BSSID : " + LI.bssid + "Freq " + LI.freq);
            }

            //Toast.makeText(getApplication(), "DONE SCANNING", Toast.LENGTH_LONG).show();
            System.out.println("Done scanning and size of wlist is "+wlist.size());
            Pair<ArrayList<Integer>,ArrayList<Integer>> rssiv;
            Starttakingreading str = new Starttakingreading();
            rssiv = str.getrssivec(wlist);
            //sendtoserverdata(data);
            String addr = "http://"+ip+"/MTarget_Server/add_rssi_data.php";
            StringBuilder result = new StringBuilder();
            HttpURLConnection connection = null;

            ArrayList<Integer> rssi2ghz;
            ArrayList<Integer> rssi5ghz;


            rssi2ghz = rssiv.first;
            rssi5ghz = rssiv.second;
            JSONObject data_to_send = new JSONObject();
            JSONObject rssi = new JSONObject();
            try {
                data_to_send.put("graphnode",1);
                JSONArray jsonrssi2ghz = new JSONArray();
                for(int i=0;i<rssi2ghz.size();i++){
                    jsonrssi2ghz.put(rssi2ghz.get(i));
                }

                JSONArray jsonrssi5ghz = new JSONArray();
                for(int i=0;i<rssi5ghz.size();i++){
                    jsonrssi5ghz.put(rssi5ghz.get(i));
                }

                rssi.put("rssi2ghz",jsonrssi2ghz);
                rssi.put("rssi5ghz",jsonrssi5ghz);

                data_to_send.put("rssi",rssi);

            } catch (JSONException e) {
                e.printStackTrace();
            }



            try {
                URL url = new URL(addr);
                connection = (HttpURLConnection) url.openConnection();
                //System.out.println("Response code : " + String.valueOf(connection.getResponseCode()));
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                OutputStream os = connection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                //String sendquery = getQuery(data_to_send);
                System.out.println(data_to_send);
                bw.write("data="+data_to_send);
                bw.flush();
                bw.close();

                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line=null;

                while ((line = reader.readLine()) != null) {
                    System.out.println("recieved contennt is : " + line);
                    result.append(line);
                    System.out.println("recieved contennt is : " + line);
                }
                reader.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Error in connection -- openconnection");
                e.printStackTrace();
            }finally {
                //connection.disconnect();
            }

        }
    }

    @Override
    protected Object doInBackground(Object[] params) {

        wifimanager.startScan();
        return null;
    }
}
