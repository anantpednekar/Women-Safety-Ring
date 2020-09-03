package com.example.wsr_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.ContextCompat.startActivity;

public class UploadThread extends Thread {

    private static final String TAG = "$$ UploadThread $$ ";

    private static final int REQUEST_LOCATION = 1;
    private File Pic_file;
    Context mContext;
    General_User_Activity g;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private ProgressBar mProgressBar;

    LocationManager locationManager;
    String latitude, longitude, time;

    //private static File Pic_file;
    public UploadThread(File Pic_file_incoming, Context mContext) {

        Log.d(TAG, " : UploadThread() Constructor Called with :");

        this.Pic_file = Pic_file_incoming;
        this.mContext = mContext;
        //Pic_file = Pic_file_incoming;
        //this.File Pic_file = File Pic_file;
    }

    @Override
    public void run() {



        Log.d(TAG, " run :  RUN UploadThread ");

        Uri uri = Uri.fromFile(Pic_file);
        mImageUri = Uri.parse(uri.toString());
        mStorageRef = FirebaseStorage.getInstance().getReference("Upload");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Upload");
        //  mProgressBar = g.findViewById(R.id.progressBar);

        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(g, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {

            Location LocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location LocationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (LocationGPS != null) {
                double lat = LocationGPS.getLatitude();
                double longi = LocationGPS.getLongitude();

                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);

                //showLocationtxt.setText("Your Location:" + "\n" + "Latitude:" + latitude + "\n" + "Longitude:" + longitude);
            } else if (LocationNetwork != null) {
                double lat = LocationNetwork.getLatitude();
                double longi = LocationNetwork.getLongitude();

                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);

                // showLocationtxt.setText("Your Location:" + "\n" + "Latitude:" + latitude + "\n" + "Longitude:" + longitude);
            } else if (LocationPassive != null) {
                double lat = LocationPassive.getLatitude();
                double longi = LocationPassive.getLongitude();

                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);

                //showLocationtxt.setText("Your Location:" + "\n" + "Latitude:" + latitude + "\n" + "Longitude:" + longitude);
            } else {
                Toast.makeText(mContext, "Can't Get Your location", Toast.LENGTH_SHORT).show();
            }


            //get time
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            time = "Time : " + format.format(calendar.getTime());


        }

        Log.d(TAG, " run :  Get location ");

        if (mImageUri != null) {
            //  StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + MimeTypeMap.getFileExtensionFromUrl(mImageUri.toString()));
            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(mContext, "Upload Successful", Toast.LENGTH_LONG).show();
                            Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String photoLink = uri.toString();
                                    Upload upload = new Upload(latitude, longitude, time, photoLink);
                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(upload);

                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        } else {

            Log.d(TAG, " run :  No Image URI to upload ");

            Toast.makeText(mContext, "No file selected", Toast.LENGTH_SHORT).show();
        }

        Log.d(TAG, " run :  UploadThread Finished");
    }


}