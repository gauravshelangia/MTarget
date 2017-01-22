package com.example.gaurav.mtarget;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaurav on 22/1/17.
 */

public class wificonnections extends Activity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    ListView listView;
    public ArrayList<Listitem> wlist = new ArrayList<>();
    WifiManager wifimanager;
    WifiReciever receiverWifi;
    public List<ScanResult> wifiList;
    Rawadapter myadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifilist);
        listView = (ListView) findViewById(R.id.rawdata);


        wifimanager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (wifimanager.isWifiEnabled() == false)
        {   // If wifi disabled then enable it
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled",
                    Toast.LENGTH_LONG).show();
            wifimanager.setWifiEnabled(true);
        }

        // wifi scaned value broadcast receiver
        receiverWifi = new WifiReciever();
        registerReceiver(receiverWifi,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        //wlist.clear();
        //wifimanager.startScan();

        wifimanager.startScan();

        System.out.println("Wlist size is : " + wlist.size());
        myadapter =  new Rawadapter(this, R.layout.row, wlist);
        myadapter.notifyDataSetChanged();
        listView.setAdapter(myadapter);

        wlist.clear();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    protected void scan(View v){
        wlist.clear();
        wifimanager.startScan();
        myadapter.notifyDataSetChanged();
    }

    protected void onPause() {
        //unregisterReceiver(receiverWifi);
        super.onPause();
    }

    protected void onResume() {
        //registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        //wlist.clear();
        super.onResume();
    }

    class WifiReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Starting scan");
            wifiList = wifimanager.getScanResults();
            for (ScanResult result : wifiList) {
                Listitem LI = new Listitem();
                LI.ssid = result.SSID;
                LI.bssid = result.BSSID;
                LI.strength = result.level;
                LI.freq = result.frequency;
                wlist.add(LI);
                System.out.println("SSIS :  " +LI.ssid + "\t BSSID : " +LI.bssid);
            }
            System.out.println("Size is : "+ wlist.size());
            Log.e("fgggggggh " ,wlist.toString());
            Toast.makeText(getApplication(),"Helel a",Toast.LENGTH_LONG).show();
            System.out.println("Done scanning");


            myadapter.notifyDataSetChanged();


        }
    }
}
