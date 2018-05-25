package com.example.idrees.smartnotes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class Connecting extends AppCompatActivity {

    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting);

        back = (Button) findViewById(R.id.back);

        Intent i = getIntent();
        String value = i.getExtras().getString("connect");

    }
}
