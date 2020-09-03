package com.example.wsr_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.IntentCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class General_User_Activity extends AppCompatActivity {
    TextView textView;
    String s;
    Button btnshowupload,btnlogout,btnliveloc;
    Context mContext;

    private static final String TAG = "General_User_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference();
        reference.child("Upload")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        notification();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        Log.d(TAG, " : onCreate() Started");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general__user_);
        textView = findViewById(R.id.textView);
        s = getIntent().getStringExtra("EXTRA_STRING");

        if(s!=null)
            textView.setText("Connected to "+s);

        btnshowupload = findViewById(R.id.btnGetinfo);
        btnlogout = findViewById(R.id.btnLogout);
        btnliveloc = findViewById(R.id.btnliveloc);
        btnshowupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, " : btnshowupload.onClick() Started ");

                startActivity(new Intent(getApplicationContext(), ImagesActivity.class));
            }
        });
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
        btnliveloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchcurrent();
            }
        });




    }
    private void notification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("n", "n", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Intent myIntent = new Intent(this,ImagesActivity.class );
        @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(this,0,myIntent,Intent.FLAG_ACTIVITY_NEW_TASK);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n")
                .setContentTitle("Women safety Ring")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_action_name)
                .setContentText("EMERGENCY!! New Data is uploaded")
                .setContentIntent(pendingIntent);
        builder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999, builder.build());
    }

    private void fetchcurrent() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference();

       reference.child("Live").addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              // Upload upload =dataSnapshot.getValue(Upload.class);
               for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                   Upload upload = postSnapshot.getValue(Upload.class);
                    String lat = upload.getLat();
                    String lon = upload.getLon();

               String link = "https://www.google.com/maps/search/?api=1&query="+lat+","+lon;
               Intent browserIntent=new Intent(Intent.ACTION_VIEW);
               browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               browserIntent.setData(Uri.parse(link));
               startActivity(browserIntent);

           }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });


    }
}
