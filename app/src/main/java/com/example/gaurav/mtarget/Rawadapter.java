package com.example.gaurav.mtarget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by gaurav on 22/1/17.
 */

public class Rawadapter extends ArrayAdapter {
    ArrayList<Listitem> data = null;
    Context context;

    public  Rawadapter(Context context, int resource, ArrayList<Listitem> data ){
        super(context, resource, data);
        this.context = context;
        this.data = data;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = convertView;
        if (vi==null){
            vi = inflater.inflate(R.layout.row,null);
            TextView viewssid = (TextView) vi.findViewById(R.id.ssid);
            viewssid.setText("SSID : " + data.get(position).ssid);

            TextView viewbssid = (TextView) vi.findViewById(R.id.bssid);
            viewbssid.setText("BSSID : " + data.get(position).bssid);

            TextView viewfreq = (TextView) vi.findViewById(R.id.freq);
            viewfreq.setText("Frequency : " + data.get(position).freq);

            TextView viewstrength = (TextView) vi.findViewById(R.id.strength);
            viewstrength.setText("Signal Strength : " + data.get(position).strength);
        }

        return vi;
    }
}
