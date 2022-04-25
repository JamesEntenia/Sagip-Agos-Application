package com.example.weareperpared;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapbox.mapboxsdk.plugins.offline.model.NotificationOptions;
import com.mapbox.mapboxsdk.plugins.offline.model.OfflineDownloadOptions;
import com.mapbox.mapboxsdk.plugins.offline.offline.OfflinePlugin;
import com.mapbox.mapboxsdk.plugins.offline.utils.OfflineUtils;

public class MainActivity extends AppCompatActivity {

    DBHelper db;
    TextView downloadingMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiamVtcG90MjMiLCJhIjoiY2ttZGdwYnZ5MGZ2NzJxcDJ3ZmduNnU0NSJ9.8HGhz2OF_EDHffxoJQWgsw");
        setContentView(R.layout.activity_main);

            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getSupportActionBar().hide();
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            downloadingMap = findViewById(R.id.downloadingMap);

            checkIfFirebaseIsConnected();
            intentTimer(20);



        //deleteData();
        //insertData();
        //updateData();
        //insertData();
        //viewData();
    }

    boolean intented = false;

    public void checkIfFirebaseIsConnected(){

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean connected = snapshot.getValue(Boolean.class);


                if (connected) {

                    if(!intented) {
                        //Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                        loginUser();
                    }
                    //connectionStatus.setBackgroundResource(R.drawable.internet_connected);
                } else {
                    //connectionStatus.setBackgroundResource(R.drawable.internet_disconnected);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void intentTimer(int Seconds) {


        new CountDownTimer(Seconds * 1000, 1000) {
            public void onTick(long millisUntilFinished) {



            }

            public void onFinish() {

                if(!intented) {
                    loginUser();
                }

            }
        }.start();

    }








    public void loginUser(){
        db = new DBHelper(this);

        boolean updated = db.update("","","","");

        if(updated){
            //Toast.makeText(this,"updated",Toast.LENGTH_LONG).show();
            viewData();
        }else{
            insertData();
            //Toast.makeText(this,"failed",Toast.LENGTH_LONG).show();
        }
    }

    public void viewData(){
        boolean loggedIn = false;
        try {

            Cursor res = db.getdata();

            if (res.getCount() == 0) {
                return;
            }

            //StringBuffer buffer = new StringBuffer();

            while (res.moveToNext()) {

                if(!res.getString(0).equals("")) {

                    //deleteData(res.getString(0));
                    downloadMap();
                    Intent intent = new Intent(MainActivity.this, Map_Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("name", res.getString(0));
                    intent.putExtra("userType", res.getString(2));
                    startActivity(intent);

                    loggedIn = true;
                    intented = true;

                    break;

                    //buffer.append("Name: " + res.getString(0) + "\n");
                    //buffer.append("Password: " + res.getString(1) + "\n");
                    //buffer.append("Usertype: " + res.getString(2) + "\n");
                    //buffer.append("Contact: " + res.getString(3) + "\n\n");
                }
            }
            if(!loggedIn){
                downloadMap();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                intented = true;
            }

        }catch (Exception e){}


    }





    public void deleteData(String name){
        db = new DBHelper(this);

        boolean deleted = db.delete(name);

        if(deleted){
            //Toast.makeText(this,"deleted",Toast.LENGTH_LONG).show();
        }else{
            //Toast.makeText(this,"failed",Toast.LENGTH_LONG).show();
        }
    }/*
 */
    public void insertData(){
        db = new DBHelper(this);

        boolean inserted = db.insert("","","","");
        if(inserted){
            loginUser();
            //Toast.makeText(this,"inserted",Toast.LENGTH_LONG).show();
        }else{
            //Toast.makeText(this,"failed",Toast.LENGTH_LONG).show();
        }


    }


    //-----Download Map Without Displaying------------------------------------------------------------
    double LatNorth = 14.868179, LatSouth = 14.822714, LngWest = 120.884195, LngEast = 120.927998;
    public void downloadMap(){

        // Define region of map tiles
        String styleUrl = "mapbox://styles/jempot23/cktv4tsp61xi117s8p3nta9qx";
        double minZoom = 12, maxZoom = 15;

        downloadingMap.setText("Downloading Map...");

        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                styleUrl,
                new LatLngBounds.Builder()
                        .include(new LatLng(14.768225296252517, 120.89468058115267)) // Northeast
                        .include(new LatLng(14.771814117461439,  120.89473438372976)) // Southwest
                        .build(),
                minZoom,
                maxZoom,
                getResources().getDisplayMetrics().density
        );



        // Customize the download notification's appearance
        NotificationOptions notificationOptions = NotificationOptions.builder(MainActivity.this)
                .smallIconRes(R.drawable.app_logo)
                .returnActivity(MainActivity.class.getName())
                .build();



        // Start downloading the map tiles for offline use
        OfflinePlugin.getInstance(MainActivity.this).startDownload(
                OfflineDownloadOptions.builder()
                        .definition(definition)
                        .metadata(OfflineUtils.convertRegionName("Taliptip"))
                        .notificationOptions(notificationOptions)
                        .build()
        );

    }


}