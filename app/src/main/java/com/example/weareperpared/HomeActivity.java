package com.example.weareperpared;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.geometry.LatLng;

public class HomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


    }




/*
    public void intentToMap(){
        Intent intent = new Intent(getApplicationContext(),Map_Activity.class);
        startActivity(intent, savedInstanceState);
    }

    //-----LatLng-----------------------------------------------------------------------------------
    @SuppressLint("MissingPermission")
    public void test() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                LatLngTv.setText(latitude+"<>"+longitude);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                LatLngTv.setText("Please turn on your GPS, for us to get realtime your location.");

            }
        };

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10,locationListener);

    }
    //----------------------------------------------------------------------------------------------



    //-----REQUESTING TO TURN ON GPS----------------------------------------------------------------
    public void RequestToEnableGPS(){

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);


        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {   //Continue to map view if gps is already on

                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    test();


                } catch (ApiException exception) {      //This will request to enable gps if gps is off

                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        HomeActivity.this,
                                        LocationRequest.PRIORITY_HIGH_ACCURACY);


                            } catch (IntentSender.SendIntentException e) {
                            } catch (ClassCastException e) {
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }


                }

            }
        });

    }

    //result of enabling GPS dialog request
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LocationRequest.PRIORITY_HIGH_ACCURACY:
                switch (resultCode) {
                    case Activity.RESULT_OK:

                        // If the enabling GPS is accepted
                        test();

                        break;
                    case Activity.RESULT_CANCELED:

                        // If the enabling GPS is rejected

                        break;
                    default:
                        break;
                }
                break;
        }
    }
    //----------------------------------------------------------------------------------------------
*/




}