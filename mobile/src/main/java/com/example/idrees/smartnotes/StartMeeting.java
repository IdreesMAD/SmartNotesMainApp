package com.example.idrees.smartnotes;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class StartMeeting extends AppCompatActivity {

    Button select, back;
    ListView listView;
    ArrayAdapter adapter;

    String code;
    String meetingName;
    ArrayList<String> mobileArray = new ArrayList<String>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_meeting);

        listView = (ListView) findViewById(R.id.listview);
        select = (Button) findViewById(R.id.back);
        back = (Button) findViewById(R.id.back);

        Intent i = getIntent();
        String value = i.getExtras().getString("key");
        firebaseData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                String splitting = adapter.getItem(position).toString();

                String[] splitted = splitting.split(",");
                for(int i = 0; i <= 3; i++)
                {
                    if(i == 3)
                    {
                        code = splitted[i].replaceAll("[]]","");
                        break;
                    }
                    else if (i == 0){
                        meetingName = splitted[i].replaceAll("[/\\[]","");
                    }
                }
                //Toast.makeText(StartMeeting.this, ""+meetingName, Toast.LENGTH_SHORT).show();
                //Toast.makeText(StartMeeting.this, "Connecting to the users of Meeting "+(position+1), Toast.LENGTH_SHORT).show();
                //Toast.makeText(StartMeeting.this, ""+code, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(StartMeeting.this, Connecting.class);
                i.putExtra("connectCode", ""+meetingName);
                StartMeeting.this.startActivity(i);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartMeeting.super.finish();
            }
        });
    }

    public void firebaseData()
    {
        db.collection("Meetings")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                //Log.d("Result",documentSnapshot.getId()+"->"+documentSnapshot.getData().get("Meeting_Name"));
                                mobileArray.add(documentSnapshot.getData().get("Meeting_Name").toString());
                                adapter = new ArrayAdapter<String>(StartMeeting.this, R.layout.list, mobileArray);
                                listView.setAdapter(adapter);
                            }
                        }else{
                            Toast.makeText(StartMeeting.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
