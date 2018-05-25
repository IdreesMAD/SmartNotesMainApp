package com.example.idrees.smartnotes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;

public class StartMeeting extends AppCompatActivity {

    Button select;
    ListView listView;
    String[] mobileArray = {"Meeting 1","Meeting 2","Meeting 3","Meeting 4",
            "Meeting 5","Meeting 6","Meeting 7","Meeting 8"};
    ArrayAdapter adapter;
    Button back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_meeting);

        listView = (ListView) findViewById(R.id.listview);
        select = (Button) findViewById(R.id.back);
        back = (Button) findViewById(R.id.back);

        Intent i = getIntent();
        String value = i.getExtras().getString("key");

        adapter = new ArrayAdapter<String>(this,
                R.layout.list, mobileArray);

        listView.setAdapter(adapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartMeeting.super.finish();
            }
        });

        //listView.setOnItemClickListener((AdapterView.OnItemClickListener) this);


    }

    public void OnItemClick(AdapterView<?> l, View v, int position, long id)
    {
        Log.i("ListView", "You clicked Item: " + id + " at position:" + position);
        Intent intent = new Intent();
        intent.setClass(this, Connecting.class);
        intent.putExtra("connect", position);
        startActivity(intent);
    }
}
