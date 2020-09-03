package com.example.wsr_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "/* Main Activity */";

    // private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_SRORAGE_AND_LOCATION = 1;
    Button btnDisplayPaired;
    ListView ListViewPairedDevices;
    BluetoothAdapter BA;
    BluetoothDevice SelectedPaired=null;
    Set<BluetoothDevice> pairedDevice;
    ArrayList<BluetoothDevice> listDevice = new ArrayList();
    ArrayList list = new ArrayList();
    BluetoothConnectionService mBCS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, " : onCreate() Started");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDisplayPaired = findViewById(R.id.btnDisplayPaired);
        ListViewPairedDevices = findViewById(R.id.ListViewPairedDevices);
        BA = BluetoothAdapter.getDefaultAdapter();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Log.d(TAG, " : builder - onClick() Started");

                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        if (ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED	||
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)

        {

            Log.d(TAG, " : ActivityCompat.checkSelfPermission Started");

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, REQUEST_SRORAGE_AND_LOCATION);
        }

        btnDisplayPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, " :  btnDisplayPaired - onClick() Started");

                list();
            }
        });
        ListViewPairedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedPaired = listDevice.get(position);
                String s = "name " + SelectedPaired.getName() + "  Add " + SelectedPaired.getAddress();

                Log.d(TAG, " :  Creating BluetoothConnectionService - Object ");

                mBCS = new BluetoothConnectionService(MainActivity.this, SelectedPaired);

                Log.d(TAG, " :  Creating - Intent - General_User_Activity ");

                Intent intent = new Intent(getApplicationContext(), General_User_Activity.class);
                intent.putExtra("EXTRA_STRING", SelectedPaired.getName());


                Log.d(TAG, " :  Creating - Intent - General_User_Activity  Be Start");

                startActivity(intent);
            }
        });
    }

    private void list() {

        Log.d(TAG, " :  List() Called ");

        pairedDevice = BA.getBondedDevices();

        for (BluetoothDevice bt : pairedDevice){
            listDevice.add(bt);
            list.add(bt.getName()+"\n"+bt.getAddress());
        }
        Toast.makeText(this,"Showing Devices",Toast.LENGTH_SHORT).show();
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        ListViewPairedDevices.setAdapter(adapter);
    }

}
