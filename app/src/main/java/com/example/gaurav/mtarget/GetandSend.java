package com.example.gaurav.mtarget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.gaurav.mtarget.MainActivity.ip;

/**
 * Created by gaurav on 28/2/17.
 */

public class GetandSend extends AsyncTask {

   static ArrayList<String> wifibssid = new ArrayList<>(Arrays.asList(
            "2c:c5:d3:23:c2:3c","58:b6:33:26:53:28","58:b6:33:26:53:2c","2c:c5:d3:23:c2:38","58:b6:33:26:51:48","58:b6:33:26:56:cc"
            ,"24:c9:a1:49:a7:28","24:c9:a1:47:29:58","24:c9:a1:47:2f:38","24:c9:a1:49:9a:c8","6c:aa:b3:48:ad:b8","24:c9:a1:49:a1:58",
            "24:c9:a1:49:9f:08",
            "2c:c5:d3:63:c2:3c","24:c9:a1:09:a7:28","24:c9:a1:07:29:58","24:c9:a1:09:a1:58","24:c9:a1:09:9a:c8","24:c9:a1:07:2f:38"
            ,"58:b6:33:66:53:2c","58:b6:33:66:53:28","6c:aa:b3:08:ad:b8","2c:c5:d3:63:c2:38","58:b6:33:66:51:48","24:c9:a1:09:9f:08",
            "58:b6:33:66:56:cc"
    ));

    static public WifiManager wifimanager;
    public WifiReciever receiverWifi;
    Context context;
    public static int no_of_reading_collected;

    public GetandSend(Context context){
        this.context = context;
        // register wifi reciever
        wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(wifimanager == null)
            Log.e("errore n " ,"wifimanager");
        if (wifimanager.isWifiEnabled() == false) {
            // If wifi disabled then enable it
            Toast.makeText(context, "wifi is disabled..making it enabled",
                    Toast.LENGTH_LONG).show();
            wifimanager.setWifiEnabled(true);
        }

        receiverWifi = new GetandSend.WifiReciever();
        context.registerReceiver(receiverWifi,new IntentFilter(wifimanager.SCAN_RESULTS_AVAILABLE_ACTION));

    }

    class WifiReciever extends BroadcastReceiver{
        @Override
        public void onReceive(Context cont, Intent intent) {
            //System.out.println("Starting scan");
            no_of_reading_collected ++;
            ArrayList<Listitem> wlist = new ArrayList<Listitem>();
            wlist.clear();
            List<ScanResult> wifiList = new ArrayList<>()  ;

            if(wifimanager==null){
                Log.e("wifimanager is null","");
            }
            wifiList = wifimanager.getScanResults();

            Toast.makeText(context,"Collected :" + no_of_reading_collected,Toast.LENGTH_SHORT).show();

            if(no_of_reading_collected == Starttakingreading.maxreading){
                context.unregisterReceiver(receiverWifi);
                //System.out.println("value of no_reading is " +no_of_reading_collected);
                Toast.makeText(context,"Done",Toast.LENGTH_LONG).show();
                no_of_reading_collected=0;
            }

            for (ScanResult result : wifiList) {
                Listitem LI = new Listitem();
                LI.ssid = result.SSID;
                LI.bssid = result.BSSID;
                LI.strength = result.level;
                LI.freq = result.frequency;
                wlist.add(LI);
                //System.out.println("SSID :  " + LI.ssid + "\t BSSID : " + LI.bssid + "Freq " + LI.freq);
            }

            //Toast.makeText(getApplication(), "DONE SCANNING", Toast.LENGTH_LONG).show();
            //System.out.println("Done scanning and size of wlist is "+wlist.size());
            Pair<ArrayList<Integer>,ArrayList<Integer>> rssiv;
            rssiv = getrssivec(wlist);
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
                data_to_send.put("graphnode",Starttakingreading.tileactual);
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

            RequestServer rs = new RequestServer();
            RequestServer.Sendrssidatatoserver sendrssi = new RequestServer.Sendrssidatatoserver(addr,data_to_send.toString());
            sendrssi.execute();

        }
    }

    @Override
    protected Object doInBackground(Object[] params) {
        wifimanager.startScan();

        return null;
    }

    // convert wlist into pair of rssi2ghz and rssi5ghz
    static public Pair<ArrayList<Integer>,ArrayList<Integer>> getrssivec(ArrayList<Listitem> wlist){

        Pair<ArrayList<Integer> ,ArrayList<Integer> > rssivec ;
        int no_wifi = wifibssid.size();
        ArrayList<Integer> rssi2ghz = new ArrayList<>();
        ArrayList<Integer> rssi5ghz = new ArrayList<>();

        for (int i=0;i<no_wifi;i++){
            rssi2ghz.add(-100);
            rssi5ghz.add(-100);
        }

        for (Listitem l : wlist) {
            if (wifibssid.contains(l.bssid)){
                int index = wifibssid.indexOf(l.bssid);
                if(l.freq <= 2800 && l.freq >=1800){
                    rssi2ghz.set(index, l.strength);
                }
                if(l.freq <= 5500 && l.freq >=4800){
                    rssi5ghz.set(index,l.strength);
                }
            }else{
                Log.e("NOt our wifi ","");
            }
        }

        rssivec = new Pair<>(rssi2ghz,rssi5ghz);
        return rssivec;
    }
}
