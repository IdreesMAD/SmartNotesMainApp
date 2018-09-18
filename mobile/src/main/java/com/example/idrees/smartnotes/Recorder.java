package com.example.idrees.smartnotes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Recorder extends AppCompatActivity {
    Button back, save;
    ImageView play, pause, stop;
    Chronometer chronometer;
    private String outputFile;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference reference = db.getReference("Users");
    StorageReference storageRef;
    MediaRecorder recorder;
    boolean recording = false;
    private Handler handler;


    ArrayList<String> users = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        Intent i = getIntent();
        String meetingName = i.getExtras().getString("connect");

        Date date = Calendar.getInstance().getTime();
        String time = date.toString();
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath()+"/recording-"+time+".mp3";

        storageRef = FirebaseStorage.getInstance().getReference();

        back = (Button) findViewById(R.id.back);
        save = (Button) findViewById(R.id.save);
        play = (ImageView) findViewById(R.id.play);
        pause = (ImageView) findViewById(R.id.pause);
        stop = (ImageView) findViewById(R.id.stop);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        
        //Part Of Connecting all the users
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(Recorder.this, "Data Changed.", Toast.LENGTH_LONG).show();
                //users.add(dataSnapshot.getValue(String.class));
                //Toast.makeText(Recorder.this, ""+value, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Failed to read value", String.valueOf(databaseError.toException()));

            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.stop();
                stop();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertView("Unsaved recordings will be deleted. Are you sure?", "back");
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recording == true) {
                    recorder.resume();
                    chronometer.start();
                    handler = new Handler();

                    handler.postDelayed(runnable,10000);

                } else {
                    try {
                        recorder = new MediaRecorder();
                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                        recorder.setOutputFile(outputFile);
                        recorder.prepare();
                        chronometer.stop();
                        play();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pause();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recording == false){
                    chronometer.stop();
                }else {
                    stop();
                }
            }
        });



    }

    public void stop()
    {
        alertView("Do you want to finish meeting? Recordings will be saved automatically.", "Stop");
        handler.removeCallbacks(runnable);
    }

    public void pause() throws Exception {
        if(recording == true) {
            recorder.pause();
            chronometer.stop();
            handler.removeCallbacks(runnable);
        }else {
            //do nothing
        }
    }

    public void play() throws IOException
    {
        recorder.start();
        recording = true;
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();


        handler = new Handler();

        handler.postDelayed(runnable,10000);

        Toast.makeText(Recorder.this, "playing", Toast.LENGTH_LONG).show();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            handler.postDelayed(this,10000);
            recorder.stop();
            try {
                uploadAudio();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    };

    private void alertView(String message, final String s ) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(Recorder.this);
        dialog.setTitle( "Warning!" )
                .setIcon(R.drawable.warning)
                .setMessage(message)
             .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialoginterface, int i) {
                  dialoginterface.cancel();
                  }})
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        if (recording == true)
                        {
                            chronometer.stop();
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            recorder.stop();
                            recording = false;
                            try {
                                uploadAudio();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            recorder.release();
                            recorder = null;
                        }

                        else if (s.equalsIgnoreCase("back")) {
                            if (recording == true)
                            {
                                recorder.stop();
                                recording = false;
                                try {
                                    uploadAudio();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                recorder.release();
                                recorder = null;
                            }else {
                                Recorder.super.finish();
                            }
                        }

                    }
                }).show();
    }

    public void uploadAudio() throws FileNotFoundException
    {

        Uri file = Uri.fromFile(new File(outputFile));
        StorageReference storageReferenceRef = storageRef.child(file.getLastPathSegment());
        UploadTask uploadTask = storageReferenceRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(Recorder.this, "Uploading Failed", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                taskSnapshot.getMetadata();
                Toast.makeText(Recorder.this, "Upload Success", Toast.LENGTH_LONG).show();
            }
        });

        if (recording == true) {
            try {
                Date date = Calendar.getInstance().getTime();
                String time = date.toString();
                outputFile = Environment.getExternalStorageDirectory().getAbsolutePath()+"/recording-"+time+".mp3";
                recorder.release();
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                recorder.setOutputFile(outputFile);
                recorder.prepare();
                recorder.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
