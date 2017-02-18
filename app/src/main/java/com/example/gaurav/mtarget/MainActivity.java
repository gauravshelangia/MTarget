package com.example.gaurav.mtarget;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class MainActivity extends AppCompatActivity {

    String grid_name;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_detail);

        // Create Directory as MTarget
        File dir = new File(Environment.getExternalStorageDirectory(),"/MTarget_Training_data");
        Log.e("File is ssi : " , dir.toString());
        // if  directory doesnot exist
        if (!dir.exists()){
            boolean success = dir.mkdir();
            //System.out.println( "hello there\ns"+success);
            Log.e("file : ", dir.toString());
            Toast.makeText(this, "Training data directory doesnot exist -- creating the directry", Toast.LENGTH_SHORT).show();
        }

        editText = (EditText) findViewById(R.id.grid_name);

    }



    public void scan(View v){
        Toast.makeText(getApplicationContext(),"Starting scan ", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getApplicationContext(),wificonnections.class);

        grid_name = editText.getText().toString();
        //System.out.println("Grid name : "+grid_name);

        i.putExtra("grid_name",grid_name);
        startActivity(i);
    }

    public void deleteallfile(View v){
        File mtarget = new File(Environment.getExternalStorageDirectory(),"MTarget_Training_data");
        File[] list = mtarget.listFiles();
        for(File l : list ){
            l.delete();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
