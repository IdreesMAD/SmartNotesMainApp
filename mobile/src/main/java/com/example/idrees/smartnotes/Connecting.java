package com.example.idrees.smartnotes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Connecting extends AppCompatActivity {

    Button back;
    ImageView img;
    String code;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting);

        Intent i = getIntent();
        final String value = i.getExtras().getString("connectCode");
        //Toast.makeText(Connecting.this, ""+value, Toast.LENGTH_SHORT).show();

        back = (Button) findViewById(R.id.back);
        img = (ImageView) findViewById(R.id.img);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connecting.super.finish();
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Connecting.this, Recorder.class);
                i.putExtra("connect", ""+value);
                Connecting.this.startActivity(i);

                Toast.makeText(Connecting.this, "Members Connected", Toast.LENGTH_SHORT).show();

            }
        });

    }
}
