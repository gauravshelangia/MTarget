package com.example.gaurav.mtarget;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Addtiledetail extends AppCompatActivity {
    EditText editText;
    Bundle data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtiledetail);

        // get data from previous activity i.e map acitvity
        Intent intent = getIntent();
        data = intent.getExtras();
    }

    public void addtiledetail(View view){

        editText = (EditText)findViewById(R.id.tiledetail);
        String tiledetail =  editText.getText().toString();
        data.putString("tiledetail",tiledetail);
        Intent intentto = new Intent(getApplicationContext(),Starttakingreading.class);
        intentto.putExtras(data);
        startActivity(intentto);
    }
}
