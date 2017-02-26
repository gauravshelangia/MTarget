package com.example.gaurav.mtarget;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Pair;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by gaurav on 23/2/17.
 */

public class RequestServer {
    private String ip;
    private String address;
    private String output=null;
    HttpURLConnection urlConnection;


    public RequestServer(){
        ip = "10.100.109.196";
    }



    //TODO complete this function
    public boolean addrssidata(int graphnode, ArrayList<Integer> rssi){
        address = "http://"+ip+"/MTarget_Server/add_rssi_data.php";
        try {
            new Sendrssidata(graphnode,rssi).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonObject = new JSONObject(output);
            Boolean result = Boolean.parseBoolean(jsonObject.getString("result"));
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    //
    private class Sendrssidata extends AsyncTask<Void,Void,Void> {
        int graphnode;
        ArrayList<Integer> rssivec;
        Sendrssidata (Integer graphnode, ArrayList<Integer> rssivec){
            this.graphnode = graphnode;
            this.rssivec = rssivec;
        }
        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }
    }

    // Set the query format
    private String getQuery(ArrayList<Pair<String, String>> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        int size  = params.size();
        for (int i=0;i<size;i++) {
            if (i!=0)
                result.append("&");

            result.append(URLEncoder.encode(params.get(i).first, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(params.get(i).second, "UTF-8"));
        }

        return result.toString();
    }



}
