package com.example.gaurav.mtarget;

import android.os.AsyncTask;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class RequestServer{

   static class Senduseranddevicetoserver extends AsyncTask<Void, Void, String> {

        private ArrayList<Pair<String,String>> data_to_send;
        String addr;
        Senduseranddevicetoserver(String addr, ArrayList<Pair<String,String>> data_to_send){
            this.data_to_send = data_to_send;
            this.addr = addr;
        }
        @Override
        protected String doInBackground(Void... params) {
            //CookieHandler.setDefault( new CookieManager( null, CookiePolicy.ACCEPT_ALL ) );

            StringBuilder result = new StringBuilder();
            HttpURLConnection connection = null;
            try {
                URL url = new URL(addr);
                connection = (HttpURLConnection) url.openConnection();
                //System.out.println("Response code : " + String.valueOf(connection.getResponseCode()));
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                OutputStream os = connection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                String sendquery = getQuery(data_to_send);
                System.out.println(sendquery);
                bw.write(sendquery);
                bw.flush();
                bw.close();

                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line=null;

                while ((line = reader.readLine()) != null) {
                    //System.out.println("recieved contennt is : " + line);
                    result.append(line);
                    //System.out.println("recieved contennt is : " + line);
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

            return result.toString();
        }


   }


    static class Sendrssidatatoserver extends AsyncTask<Void, Void, String> {

        private String data_to_send;
        private  String addr;
        Sendrssidatatoserver(String addr, String data_to_send){
            this.data_to_send = data_to_send;
            this.addr = addr;
        }
        @Override
        protected String doInBackground(Void... params) {
            //CookieHandler.setDefault( new CookieManager( null, CookiePolicy.ACCEPT_ALL ) );

            StringBuilder result = new StringBuilder();
            HttpURLConnection connection = null;
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

            return result.toString();
        }


    }


    // Set the query format
    static String getQuery(ArrayList<Pair<String, String>> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        int size  = params.size();
        for (int i=0;i<size;i++) {
            if (i!=0)
                result.append("&");

            result.append(params.get(i).first);
            result.append("=");
            result.append(params.get(i).second);
        }

        return result.toString();
    }

}
