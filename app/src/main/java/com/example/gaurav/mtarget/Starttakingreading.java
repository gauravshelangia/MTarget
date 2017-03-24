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

    static int maxreading,tileactual;
    static Timer timer;

    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starttakingreading);

        // get data from previous activity i.e map acitvity
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        tileactual = data.getInt("tileactual");
        addgraphdetail(data);

        // define edittext
        editText = (EditText)findViewById(R.id.no_reading);

    }

    // press button
    public void start_coll_reading(View view) {

        String readings = editText.getText().toString();
        if (readings != null) {
            maxreading = Integer.parseInt(readings);
        }
        int i=0;
       GetandSend gs = new GetandSend(getApplicationContext());
       gs.execute();
/*
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                GetandSend gs = new GetandSend(getApplicationContext());
                gs.execute();
            }
        });
*/

    }

    // cancel collecting reading
    public void stop_coll_reading(View v){
        //timer.cancel();
        maxreading=0;
        //no_of_reading_collected=0;
        Log.d("Stop collectiing ", "lund");

    }

    //add graph detail
    public void addgraphdetail(Bundle data){
        String addr = "http://"+ip+"/MTarget_Server/add_graph_detail.php";
        String detail = data.getString("tiledetail");
        int tile = data.getInt("tileactual");
        ArrayList<Integer> adjacent = new ArrayList<>();
        adjacent.add(data.getInt("tileleft"));
        adjacent.add(data.getInt("tileright"));
        adjacent.add(data.getInt("tileup"));
        adjacent.add(data.getInt("tiledown"));

        JSONObject datatosend = new JSONObject();
        try {
            datatosend.put("graphnode",tile);
            datatosend.put("detail",detail);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray jsonadjacent = new JSONArray();
        int size = adjacent.size();
        for(int i=0; i<size;i++){
            jsonadjacent.put(adjacent.get(i));
        }

        try {
            datatosend.put("adjacentnode",jsonadjacent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestServer rs = new RequestServer();
        RequestServer.Sendrssidatatoserver sendrssi = new RequestServer.Sendrssidatatoserver(addr,datatosend.toString());
        sendrssi.execute();
    }

    protected void onPause() {
        super.onPause();}

    protected void onResume() {
        super.onResume();
    }


}
