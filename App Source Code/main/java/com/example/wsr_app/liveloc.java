package com.example.wsr_app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class liveloc extends Thread {
    private static final String TAG = "$$ Liveloc $$ ";
    private static final int REQUEST_LOCATION = 1;
    Context mContext;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    LocationManager locationManager;
    String latitude, longitude,time;
    General_User_Activity g;
    String uploadId = null;
    public liveloc(Context context){
        this.mContext = context;
    }
    @Override
    public void run() {

        while(true){
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Live");

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
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            time = "Time : " + format.format(calendar.getTime());

            if(uploadId == null){
                uploadId = mDatabaseRef.push().getKey();
            }
            Upload upload = new Upload(latitude, longitude,time);
            mDatabaseRef.child(uploadId).setValue(upload);
            try {
                sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        }
    }
}
