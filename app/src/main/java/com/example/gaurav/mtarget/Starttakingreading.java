package com.example.gaurav.mtarget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

import static com.example.gaurav.mtarget.MainActivity.ip;

public class Starttakingreading extends AppCompatActivity {

    private WifiManager wifimanager;
    int no_of_reading_collected = 0,maxreading;
    Timer timer;
    ArrayList<String> wifibssid = new ArrayList<>(Arrays.asList(
            "2c:c5:d3:23:c2:3c","58:b6:33:26:53:28","58:b6:33:26:53:2c","2c:c5:d3:23:c2:38","58:b6:33:26:51:48","58:b6:33:26:56:cc"
            ,"24:c9:a1:49:a7:28","24:c9:a1:47:29:58","24:c9:a1:47:2f:38","24:c9:a1:49:9a:c8","6c:aa:b3:48:ad:b8","24:c9:a1:49:a1:58",
            "24:c9:a1:49:9f:08",
            "2c:c5:d3:63:c2:3c","24:c9:a1:09:a7:28","24:c9:a1:07:29:58","24:c9:a1:09:a1:58","24:c9:a1:09:9a:c8","24:c9:a1:07:2f:38"
            ,"58:b6:33:66:53:2c","58:b6:33:66:53:28","6c:aa:b3:08:ad:b8","2c:c5:d3:63:c2:38","58:b6:33:66:51:48","24:c9:a1:09:9f:08",
            "58:b6:33:66:56:cc"
    ));
    EditText editText;
    public WifiReciever receiverWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starttakingreading);

        // register wifi reciever
        wifimanager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifimanager.isWifiEnabled() == false) {
            // If wifi disabled then enable it
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled",
                    Toast.LENGTH_LONG).show();
            wifimanager.setWifiEnabled(true);
        }

        /* TODO write code here if the new access point added in the area --updatae wifibssid list */
        editText = (EditText) findViewById(R.id.no_reading);
        receiverWifi = new WifiReciever();
        registerReceiver(receiverWifi,new IntentFilter(wifimanager.SCAN_RESULTS_AVAILABLE_ACTION));

    }

    // press button
    public void start_coll_reading(View view) {
        String readings = editText.getText().toString();
        if (readings != null) {
            maxreading = Integer.parseInt(readings);
        }

        timer = new Timer();

       // for(no_of_reading_collected=0;no_of_reading_collected< maxreading;no_of_reading_collected++) {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Count is s s  a d " + no_of_reading_collected);
                    registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                    GetandSend gs = new GetandSend(wifimanager);
                    gs.execute();
                    no_of_reading_collected++;
                }
            },0,5000);
            //no_of_reading_collected++;
       // }
    }

    // cancel collecting reading
    public void stop_coll_reading(View v){
        timer.cancel();
        maxreading=0;
        no_of_reading_collected=0;
        Log.d("Stop collectiing ", "lund");

    }

    public void sendtoserverdata( Pair<ArrayList<Integer> ,ArrayList<Integer> > rssiv){

        String addr = "http://"+ip+"/MTarget_Server/add_rssi_data.php";
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

        RequestServer rs = new RequestServer();
        RequestServer.Sendrssidatatoserver sendrssi = new RequestServer.Sendrssidatatoserver(addr,data_to_send.toString());
        sendrssi.execute();

    }

    // convert wlist into pair of rssi2ghz and rssi5ghz
    public Pair<ArrayList<Integer>,ArrayList<Integer>> getrssivec(ArrayList<Listitem> wlist){

        Pair<ArrayList<Integer> ,ArrayList<Integer> > rssivec ;
        int no_wifi = wifibssid.size();
        ArrayList<Integer> rssi2ghz = new ArrayList<>();
        ArrayList<Integer> rssi5ghz = new ArrayList<>();

        for (int i=0;i<no_wifi;i++){
            rssi2ghz.add(0);
            rssi5ghz.add(0);
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

    class WifiReciever extends BroadcastReceiver {
        public ArrayList<Listitem> wlist = new ArrayList<Listitem>();
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Starting scan");
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
            unregisterReceiver(receiverWifi);
            //Toast.makeText(getApplication(), "DONE SCANNING", Toast.LENGTH_LONG).show();
            System.out.println("Done scanning and size of wlist is "+wlist.size());
            Pair<ArrayList<Integer>,ArrayList<Integer>> data;
            data = getrssivec(wlist);
            sendtoserverdata(data);


        }

    }



    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }


}
