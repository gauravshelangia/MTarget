package com.example.gaurav.mtarget;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Floorto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floorto);

    }

    // on press buttoon open iiitv_ground_floor map
    public void togroundfloormap(View view){
        Intent intent = new Intent(getApplicationContext(),IiitvGroundFloor.class);
        startActivity(intent);
    }

    // on press button open iiitv_first_floor map
    public void tofirstfloormap(View view){
        Intent intent = new Intent(getApplicationContext(),IiiitvFirstFloor.class);
        startActivity(intent);
    }
}
