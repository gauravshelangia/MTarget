package com.example.gaurav.mtarget;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gaurav on 22/1/17.
 */

public class wificonnections extends Activity {


    ListView listView;
    public ArrayList<Listitem> wlist = new ArrayList<>();
    WifiManager wifimanager;
    WifiReciever receiverWifi;
    public List<ScanResult> wifiList;
    Rawadapter myadapter;
    String file;
    ArrayList<String> wifibssid = new ArrayList<>(Arrays.asList(
            "2c:c5:d3:23:c2:3c","58:b6:33:26:53:28","58:b6:33:26:53:2c","2c:c5:d3:23:c2:38","58:b6:33:26:51:48","58:b6:33:26:56:cc"
            ,"24:c9:a1:49:a7:28","24:c9:a1:47:29:58","24:c9:a1:47:2f:38","24:c9:a1:49:9a:c8","6c:aa:b3:48:ad:b8","24:c9:a1:49:a1:58",
            "24:c9:a1:49:9f:08",
            "2c:c5:d3:63:c2:3c","24:c9:a1:09:a7:28","24:c9:a1:07:29:58","24:c9:a1:09:a1:58","24:c9:a1:09:9a:c8","24:c9:a1:07:2f:38"
            ,"58:b6:33:66:53:2c","58:b6:33:66:53:28","6c:aa:b3:08:ad:b8","2c:c5:d3:63:c2:38","58:b6:33:66:51:48","24:c9:a1:09:9f:08",
            "58:b6:33:66:56:cc"
                ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifilist);

        Intent intent = getIntent();
        file = intent.getStringExtra("grid_name");
        System.out.println("Grid name is " + file);

        listView = (ListView) findViewById(R.id.rawdata);
        wifimanager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (wifimanager.isWifiEnabled() == false) {   // If wifi disabled then enable it
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled",
                    Toast.LENGTH_LONG).show();
            wifimanager.setWifiEnabled(true);
        }
        //start scanning
        scanning();
    }


    private void scanning() {
        receiverWifi = new WifiReciever();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifimanager.startScan();
    }

    // start scan by pressing button
    protected void scan(View v) {
        wifimanager.startScan();
        myadapter.notifyDataSetChanged();
    }

    protected void storedetails(View v) {
        // number of wifi routers
        int no_wifi = wifibssid.size();
        ArrayList<Integer> rssi2ghz = new ArrayList<>();
        ArrayList<Integer> rssi5ghz = new ArrayList<>();

        for (int i=0;i<no_wifi;i++){
            rssi2ghz.add(0);
            rssi5ghz.add(0);
        }
        //rssi.clear();
        //System.out.println("Here collecting the result \n");
        for (Listitem l : wlist) {
            if (wifibssid.contains(l.bssid)){
                //System.out.println("inside for loop ");

                int index = wifibssid.indexOf(l.bssid);
                //System.out.println("Index is " + index);
                if(l.freq <= 2800 && l.freq >=1800){
                    rssi2ghz.add(index, l.strength);
                   // System.out.println("Coming inside 2.4GHz");
                }
                if(l.freq <= 5500 && l.freq >=4800){
                    rssi5ghz.add(index,l.strength);
                }
            }else{
                Log.e("NOt our wifi ","");
            }

        }

        //TODO create the separate file for 2.4 band and for 5 GHZ band

        File dir = new File(Environment.getExternalStorageDirectory(), "/MTarget_Training_data");
        Log.e("Dir is ssi : ", dir.toString());
        // if  directory does exist
        File data2ghz = null, data5ghz=null;
        if (dir.exists()) {
            if(new File(dir+file+"_2GHZ.txt").isFile()){
                //do not create file
                Log.d("file exist " , "no need to create in 2ghz");
            }else{
                data2ghz = new File(dir, file+"_2GHZ.txt");
            }
            if(new File(dir+file+"_5GHZ.txt").isFile()){
                //do not create file
            }else{
                data5ghz = new File(dir, file+"_5GHZ.txt");
            }
            try {
                BufferedWriter bw2ghz = new BufferedWriter(new FileWriter(data2ghz,true));
                BufferedWriter bw5ghz = new BufferedWriter(new FileWriter(data5ghz,true));

                //write 2.4GHZ data
                for (Integer i : rssi2ghz) {
                    bw2ghz.write(i + ",");
                }
                bw2ghz.newLine();
                bw2ghz.close();

                // write 5GHZ data
                for (Integer i : rssi5ghz) {
                    bw5ghz.write(i + ",");
                }
                bw5ghz.newLine();
                bw5ghz.close();


            } catch (IOException e) {
                Log.e("File not found", "");
            }

        } else {
            Toast.makeText(this, "Directory doesnot exist ", Toast.LENGTH_SHORT).show();
        }

    }


    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverWifi);
    }

    protected void onResume() {
        super.onResume();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    class WifiReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Starting scan");
            wlist.clear();
            wifiList = wifimanager.getScanResults();
            for (ScanResult result : wifiList) {
                Listitem LI = new Listitem();
                LI.ssid = result.SSID;
                LI.bssid = result.BSSID;
                LI.strength = result.level;
                LI.freq = result.frequency;
                wlist.add(LI);
                System.out.println("SSIS :  " + LI.ssid + "\t BSSID : " + LI.bssid);
            }
            //System.out.println("Size is : "+ wlist.size());
            //Log.e("fgggggggh " ,wlist.toString());
            Toast.makeText(getApplication(), "DONE SCANNING", Toast.LENGTH_LONG).show();
            System.out.println("Done scanning");
            myadapter = new Rawadapter(getApplicationContext(), R.layout.row, wlist);
            listView.setAdapter(myadapter);
            myadapter.notifyDataSetChanged();
            
            
        context.startActivity(getContext());

        }
    }
}
