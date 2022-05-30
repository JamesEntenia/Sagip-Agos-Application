 package com.example.weareperpared;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.OnLocationCameraTransitionListener;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class Map_Activity extends AppCompatActivity implements TaskFragment.TaskCallbacks, OnMapReadyCallback, PermissionsListener, PopupMenu.OnMenuItemClickListener {


    //REMOVE AREA


    ValueEventListener onGoingRescuer,fmdLisnr,assignReq,myrescuerLsnr,needrescueLsnr,latLsnr,residentmyrescuerLsnr,assigedtoLsnr;
    ChildEventListener newChildLsnr;


//LocationManager
    private long DEFAULT_MAX_WAIT_TIME = 1000L;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationComponent locationComponent;
    private Style MapStyle;
    private Location currentLocation, locationOfAssignedResident_forRescuer;
    private NavigationMapRoute navigationMapRoute;
    private static final String TAG = "Map_Activity";
    private String nameOfAssignedResident_forRescuer;
    private boolean rescuerIsAvailable = true;

    private ConstraintLayout identification,address_container,contact_container;
    private Button acceptBtn,declineBtn,yesBtn,noBtn,yes_cancleBtn,no_cancleBtn,done_yesBtn,done_noBtn,menu,exitBtn;
    private ImageView connectionStatus,addressMarker,user_icon,contact_icon,location_icon;
    private ImageView ShowMyLocation, DoneBtn,navigationBtn,sosBtn;
    private TextView  LoadingTV, OpenMobileData,serverRequestMsg,levelTv,onTheWay,searchingGPS;
    private TextView UI_NameTV, UI_AddressTv, UI_ContactTv, UI_myRescuerTv, UI_myRescuer,distanceTv,durationTv,speedTv,UIContact;
    private TextView profile_name,profile_contact,profile_location,profile_lbl;
    private Dialog serverReqDialog,sosDialogBox,cancleSosDialogBox,doneBtnDialogBox,profileDialogBox;

    private String resident_str = "Resident";
    private String rescuer_str = "Rescuer";
    private String assignedTo_str = "assignedTo";
    private String admin_str = "Admin";
    private String userType_str = "userType";
    private String name_str = "name";
    private String no_rescuer_assigned = "No rescuer assigned";
    private String warning_str = "Warning";

    private boolean isEndNotified;
    private ProgressBar progressBar;
    private OfflineManager offlineManager;
    private LocationManager manager;

    //Choose Map style
    String styleMode = "mapbox://styles/jempot23/cktv4tsp61xi117s8p3nta9qx";
    //String styleMode = "mapbox://styles/jempot23/cl3qxyr8v000e16pfg634p6o2";
    boolean showComponentLocationClicked = false, hide = false;
    boolean GPSisOn;
    boolean OnStart = true,sosReqOngoing = false;
    boolean locationIsAccurate = false,iHaveRescuer = false;
    boolean sendingSOSThroughSMS = false;
    int gpsAccuracy = 25;

    // JSON encoding/decoding
    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";
    public static final String RegionName = "Pulong Gubat";

    double LatNorth = 14.768225296252517, LatSouth = 14.771814117461439, LngWest = 120.89473438372976, LngEast = 120.89468058115267;

    //Screen Rotation
    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private Fragment mTaskFragment;


    //-----SYMBOL LAYER-----------------------------------------------------------------------------
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final String RESIDENT_ICON_ID = "RESIDENT_ICON_ID", RESCUER_ICON_ID = "RESCUER_ICON_ID", WARNING_ICON_ID = "WARNING_ICON_ID", ADMIN_ICON_ID = "ADMIN_ICON_ID";
    private static final String RESIDENT_SOURCE_ID = "RESIDENT_SOURCE_ID", RESCUER_SOURCE_ID = "RESCUER_SOURCE_ID", WARNING_SOURCE_ID = "WARNING_SOURCE_ID", ADMIN_SOURCE_ID = "ADMIN_SOURCE_ID";
    private static final String RESIDENT_LAYER_ID = "RESIDENT_LAYER_ID", RESCUER_LAYER_ID = "RESCUER_LAYER_ID", WARNING_LAYER_ID = "WARNING_LAYER_ID", ADMIN_LAYER_ID = "ADMIN_LAYER_ID";


    //SYMBOL/MARKER
    List<Feature> residentFeatureList = new ArrayList<>();
    SymbolLayer residentSymbolLayer;
    GeoJsonSource residentMapSource;
    //SYMBOL/MARKER
    List<Feature> adminFeatureList = new ArrayList<>();
    SymbolLayer adminSymbolLayer;
    GeoJsonSource adminMapSource;
    //SYMBOL/MARKER
    List<Feature> rescuerFeatureList = new ArrayList<>();
    SymbolLayer rescuerSymbolLayer;
    GeoJsonSource rescuerMapSource;
    //SYMBOL/MARKER
    List<Feature> warningFeatureList = new ArrayList<>();
    SymbolLayer warningSymbolLayer;
    GeoJsonSource warningMapSource;

    Object[] residentToBeRescue = new Object[6],rescuerForThisUser = new Object[6];

    int needRescueCounter = 0, rescuerCounter = 0, adminCounter = 0;
//--------------------------------------------------------------------------------------------------


    //FIREBASE
    FirebaseDatabase firebaseDatabase;
    DatabaseReference barangayRef,notificationRef,fmdRef;
    String Username, ThisUserType;


    String selectedUserType = "", selectedName = "";
    boolean assigning = false, activityStop = false;
    double residentLat, residentLng, rescuerLat, rescuerLng;


    private LocationChangeListeningActivityLocationCallback callback = new LocationChangeListeningActivityLocationCallback(this);


    //____________________________________MAIN ACTIVITY_____________________________________________
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Token
        Mapbox.getInstance(this, "pk.eyJ1IjoiamVtcG90MjMiLCJhIjoiY2ttZGdwYnZ5MGZ2NzJxcDJ3ZmduNnU0NSJ9.8HGhz2OF_EDHffxoJQWgsw");


        setContentView(R.layout.activity_map);
        getSupportActionBar().hide();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            GPSisOn = true;

        firebaseDatabase = FirebaseDatabase.getInstance();
        barangayRef = firebaseDatabase.getReference("Barangay");
        notificationRef = firebaseDatabase.getReference("Notification");
        fmdRef = firebaseDatabase.getReference("FloodMonitoringDevice");
        Username = getIntent().getStringExtra(name_str);
        ThisUserType = getIntent().getStringExtra(userType_str);

        //ShowMyLocation.setText(residentExtras);

        ShowMyLocation = findViewById(R.id.ShowMyLocation);
        //LatLng = findViewById(R.id.LatLng);
        LoadingTV = findViewById(R.id.LoadingTV);
        OpenMobileData = findViewById(R.id.OpenMobileData);
        UI_NameTV = findViewById(R.id.UI_NameTv);
        UI_AddressTv = findViewById(R.id.address_tv);
        UI_ContactTv = findViewById(R.id.UI_ContactTv);
        UIContact = findViewById(R.id.UI_contact);
        UI_myRescuerTv = findViewById(R.id.UI_myRescuerTv);
        connectionStatus = findViewById(R.id.mapConnection);
        distanceTv = findViewById(R.id.distanceTv);
        durationTv = findViewById(R.id.durationTv);
        speedTv = findViewById(R.id.speedTv);
        addressMarker = findViewById(R.id.address_marker);
        searchingGPS = findViewById(R.id.searchingGPS);
        UI_myRescuer = findViewById(R.id.UI_myRescuer);
        DoneBtn = findViewById(R.id.DoneBtn);
        navigationBtn = findViewById(R.id.navigationBtn);
        menu = findViewById(R.id.menubar);
        levelTv= findViewById(R.id.levelTv);
        sosBtn = findViewById(R.id.sosBtn);
        onTheWay = findViewById(R.id.onTheWay);
        identification = findViewById(R.id.Identification);
        address_container = findViewById(R.id.address_container);
        contact_container = findViewById(R.id.contact);





        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        currentLocation = new Location(String.valueOf(new LatLng(0, 0)));
        locationOfAssignedResident_forRescuer = new Location(String.valueOf(new LatLng(0, 0)));



//blur();
        handlingOrientation();

        showMyLocation();
        doneButton();
        sos_btn();
        navigation_btn();
        setMenu();
        setFMD();


        //hideUserIdentification();

        checkIfFirebaseIsConnected();
        newChild();
        setProfile();
        notification();

        if(ThisUserType.equals(rescuer_str)){
            barangayRef.child(Username).child("needRescue").setValue("yes");
            serverRequestDialogBox();
            assignRequest();
            setdoneBtnDialogBox();
            ongoingRescue();
            menu.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(243,172,91)));
            speedTv.setTextColor(Color.parseColor(("#f3ac5b")));
            UI_ContactTv.setTextColor(Color.parseColor(("#f3ac5b")));
            levelTv.setTextColor(Color.parseColor(("#f3ac5b")));
            UIContact.setBackgroundResource(R.drawable.contact_icon_rescuer);//Flood
            onTheWay.setBackgroundResource(R.drawable.edittext_background_rescuer);


        }else if(ThisUserType.equals(resident_str)){


            forResident();
            setsosDialogBox();
            setcancleSosDialogBox();
        }

        //checkMsgPermission();
    }
    //______________________________________________________________________________________________

    public void checkMsgPermission(){
        if (ContextCompat.checkSelfPermission( Map_Activity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
            checkSOS();
        }else{
            ActivityCompat.requestPermissions( Map_Activity.this
                    ,new String[]{Manifest.permission.SEND_SMS},100);
        }
    }

    public void checkSOS(){

        if(sendingSOSThroughSMS){
            if(locationIsAccurate){
                sendMsg("09760793371","sos<>"+currentLocation.getLongitude()+"<>"+currentLocation.getLatitude());
            }else{//location waiting

            }
        }
    }
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    public void sendMsg(String phoneNo,String msg){

        SmsManager smsManager = SmsManager.getDefault();

        smsManager.sendTextMessage(phoneNo,null,msg,null,null);
    }


    public void notification(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("water level notification","water level notification",NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager  = getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }

        //NOTIFICATION: LEVEL LISTENER
        notificationRef.child("level").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot notif_Lvl_snapshot) {
                String notifLvl = notif_Lvl_snapshot.getValue(String.class);

        // READ USER READNOTIF ONCE
        barangayRef.child(Username).child("readNotif").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot readNotif_snapshot) {
                String readNotif = readNotif_snapshot.getValue(String.class);

                //IF NOTIFICATION IS NOT EQUAL TO WHAT USER'S READ THEN SHOW NOTIFICATION
                if(!notifLvl.equals(readNotif)){

                    barangayRef.child(Username+"/readNotif").setValue(notifLvl);

                    // READ NOTIFICATION ONCE
                    notificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot notification_snapshot) {
                            String time = notification_snapshot.child("time").getValue(String.class);
                            Log.i("notif",time);
                            String message = notification_snapshot.child("mes").getValue(String.class),title = "Sagip Agos ("+time+")";
/*
                            if(notifLvl.equals("Normal")){
                                title = notifLvl+" level (Green): "+time;
                            }else if(notifLvl.equals("Alert")){
                                title = notifLvl+" level (Blue): "+time;
                            }else if(notifLvl.equals("Warning")){
                                title = notifLvl+" level (Yellow): "+time;
                            }else if(notifLvl.equals("Critical")){
                                title = notifLvl+" level (Pink): "+time;
                            }else if(notifLvl.equals("Danger")){
                                title = notifLvl+" level (Red): "+time;
                            }*/

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(Map_Activity.this,"water level notification")
                                    .setSmallIcon(R.drawable.app_logo)
                                    .setContentTitle(title)
                                    .setContentText(message)
                                    .setAutoCancel(true);
                            //Vibration
                            builder.setVibrate(new long[] { 1000,1000,1000,1000});

                            //Ton
                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            builder.setSound(alarmSound);
                            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(Map_Activity.this);
                            managerCompat.notify(1,builder.build());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

      /*  barangayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot user : snapshot.getChildren()){
                   barangayRef.child(user.getKey()).child("readNotif").setValue("Normal");
                    Log.i("names",user.getKey());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    public void setProfile(){

        barangayRef.child(Username).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String contact = snapshot.child("cellphoneNum").getValue(String.class);
                String address = snapshot.child("address").getValue(String.class);

                setProfileDialogbox(name,address,contact);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

public void setCurrentLocation(DataSnapshot snapshot){
        snapshot.child("currentLocation").getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot locationSnapshot) {
                String currentLocation = locationSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
}



    //FOR RESCUER ONGOING RESCUE OPERATION
    public void ongoingRescue(){
        onGoingRescuer = barangayRef.child(Username).child("assignedTo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String assignedTo = snapshot.getValue(String.class);
                //Toast.makeText(Map_Activity.this, assignedTo, Toast.LENGTH_SHORT).show();
                if (!assignedTo.equals("Not assigned yet")) {
                    onTheWay.setText("  Ongoing rescue operation  ");
                    onTheWay.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //SET UP FLOOD MONITORING DEVICE LISTENER
    public void setFMD(){

        fmdLisnr = fmdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String level = snapshot.child("level").getValue(String.class);
                    String location = snapshot.child("location").getValue(String.class);
                    Double lat = Double.parseDouble(snapshot.child("lat").getValue(String.class));
                    Double lng = Double.parseDouble(snapshot.child("lng").getValue(String.class));

                    try {
                        //SYMBOL COORDINATES SAMPLE
                        warningFeatureList.set(0, Feature.fromGeometry(
                                Point.fromLngLat(lng, lat)));
                        warningFeatureList.get(0).addStringProperty(name_str, "Flood Warning");
                        warningFeatureList.get(0).addStringProperty("level", level);
                        warningFeatureList.get(0).addStringProperty("location", location);
                        warningFeatureList.get(0).addNumberProperty("Lat", lat);
                        warningFeatureList.get(0).addNumberProperty("Lng", lng);
                        if(level.equals("Normal")){
                            levelTv.setTextColor(Color.parseColor(("#00d71d")));
                        }else if(level.equals("Alert")){
                            levelTv.setTextColor(Color.parseColor(("#5681f7")));
                        }
                        else if(level.equals("Warning")){
                            levelTv.setTextColor(Color.parseColor(("#f3ac5b")));
                        }
                        else if(level.equals("Critical")){
                            levelTv.setTextColor(Color.parseColor(("#ff00ee")));
                        }
                        else if(level.equals("Danger")){
                            levelTv.setTextColor(Color.parseColor(("#ff4949")));
                        }
                        levelTv.setText(level);
                        warningSymbol();
                    } catch (Exception e) {

                        warningFeatureList.add(Feature.fromGeometry(
                                Point.fromLngLat(lng, lat)));
                        warningFeatureList.get(0).addStringProperty(name_str, "Flood Warning");
                        warningFeatureList.get(0).addStringProperty("level", level);
                        warningFeatureList.get(0).addStringProperty("location", location);
                        warningFeatureList.get(0).addNumberProperty("Lat", lat);
                        warningFeatureList.get(0).addNumberProperty("Lng", lng);
                        if(level.equals("Normal")){
                            levelTv.setTextColor(Color.parseColor(("#00d71d")));
                        }else if(level.equals("Alert")){
                            levelTv.setTextColor(Color.parseColor(("#5681f7")));
                        }
                        else if(level.equals("Warning")){
                            levelTv.setTextColor(Color.parseColor(("#f3ac5b")));
                        }
                        else if(level.equals("Critical")){
                            levelTv.setTextColor(Color.parseColor(("#ff00ee")));
                        }
                        else if(level.equals("Danger")){
                            levelTv.setTextColor(Color.parseColor(("#ff4949")));
                        }
                        levelTv.setText(level);
                        warningSymbol();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }





    /*
    public void newUserHandler{
    }
     */


    public void forResident() {

        if (ThisUserType.equals(resident_str)) {


            //sosBtn.setVisibility(View.VISIBLE);

            barangayRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

/*
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            if (dataSnapshot.child(userType_str).getValue(String.class).equals(rescuer_str)) {
                                read_RescueMe(dataSnapshot.getRef().getKey());
                            }

                        }

 */
                        myrescuerLsnr = snapshot.child(Username).child("myRescuer").getRef().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot myRescuerSnapshot) {

                                //Toast.makeText(Map_Activity.this, myRescuerSnapshot.getValue(String.class), Toast.LENGTH_LONG).show();
                                //If this user has myRescuer remove sos Button


                                //delete account pag may error

                                    if (!myRescuerSnapshot.getValue(String.class).equals("No rescuer assigned")) {
                                        iHaveRescuer = true;
                                        sosBtn.setVisibility(View.GONE);
                                        onTheWay.setVisibility(View.VISIBLE);
                                        //Toast.makeText(Map_Activity.this,m.getValue(String.class),Toast.LENGTH_SHORT).show();
                                        setRescuerInfoForThisUser(myRescuerSnapshot.getValue(String.class));

                                    } else {
                                        iHaveRescuer = false;
                                        sosBtn.setVisibility(View.VISIBLE);
                                        onTheWay.setVisibility(View.GONE);

                                        //myRescuerSnapshot.getRef().getParent().child("needRescue").setValue("no");

                                        if (snapshot.child(Username).child("needRescue").getValue(String.class).equals("yes")) {
                                            sosReqOngoing = true;
                                            sosBtn.setBackgroundResource(R.drawable.canclebtn);
                                        } else {
                                            sosReqOngoing = false;
                                            sosBtn.setBackgroundResource(R.drawable.sosbtn);
                                        }


                                    }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    public void setRescuerInfoForThisUser(String name){

        barangayRef.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rescuerForThisUser[0] = name;
                rescuerForThisUser[1] = snapshot.child("lat").getValue(Double.class);
                rescuerForThisUser[2] = snapshot.child("lng").getValue(Double.class);
                rescuerForThisUser[3] = snapshot.child("address").getValue(String.class);
                rescuerForThisUser[4] = snapshot.child("cellphoneNum").getValue(String.class);
                rescuerForThisUser[5] = snapshot.child("assignedTo").getValue(String.class);

                //Toast.makeText(Map_Activity.this,rescuerForThisUser[0]+"_\n"+rescuerForThisUser[1]+"_\n"+rescuerForThisUser[2]+"_\n"+rescuerForThisUser[5]+"_\n",Toast.LENGTH_SHORT).show();

                if(locationIsAccurate) {
                    Point destination = Point.fromLngLat((Double)rescuerForThisUser[2], (Double)rescuerForThisUser[1]);
                    Point origin = Point.fromLngLat(currentLocation.getLongitude(), currentLocation.getLatitude());
                    getRoute(origin, destination);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    /*
    public void hideUserIdentification() {
        if (ThisUserType.equals(rescuer_str) || ThisUserType.equals(resident_str)) {
            identification.setVisibility(View.GONE);
        }
    }*/



    //___________________________________FIREBASE DATA READING______________________________________

    public void newChild(){
        newChildLsnr = barangayRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {



                if (ThisUserType.equals(rescuer_str)) {
                    read_RescueMe(snapshot);


                    if(snapshot.child("userType").getValue(String.class).equals(rescuer_str))
                        setCurrentLocation(snapshot);


                } else if (ThisUserType.equals(resident_str)) {

                    if(snapshot.child("userType").getValue(String.class).equals(rescuer_str)){
                        read_RescueMe(snapshot);
                    }

                }


            }@Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}@Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}@Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}@Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    //-----Update Listener for all user location----------------------------------------------------
    public void read_RescueMe(DataSnapshot dataSnapshot) {

                        //READ ALL(NEED RESCUE) OF USER AND ADD EVERY CHANGE LISTENER
                        needrescueLsnr = dataSnapshot.child("needRescue").getRef().addValueEventListener(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot needRescuesnapshot) {

                                 //IF NEED RESCUE OF THE USE IS EQUAL TO YES THEN THIS PLAY ITS LOCATION FEATURE
                                if (needRescuesnapshot.getValue(String.class).equals("yes")) {


                                    if (!needRescuesnapshot.getRef().getParent().getKey().equals(Username)) {


                                        //Add to the users that need to rescue
                                        addNewUser(needRescuesnapshot.getRef().getParent().getKey());


                                        //SET CHANGE LISTENER FOR ALL USER THAT NEED RESCUE
                                        latLsnr = needRescuesnapshot.getRef().getParent().child("lat").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {


                                                getUserTypeAndIndex(needRescuesnapshot.getRef().getParent().getKey());

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    } else if (ThisUserType.equals(rescuer_str)) {
                                        assignedToListener(needRescuesnapshot.getRef().getParent().getKey());
                                    }


                                } else {//IF THE NEEDRESCUE CHANGE TO NO THEN REMOVE THE USER

                                    featureListScanner(needRescuesnapshot.getRef().getParent().getKey(), "remove");

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



    }


    //SET UP DATA OF NEW USER TO ADD
    public void addNewUser(String name) {

        barangayRef.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                try {
                    String userType = snapshot.child(userType_str).getValue(String.class);
                    String cellphoneNum = snapshot.child("cellphoneNum").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    Double Lat = snapshot.child("lat").getValue(Double.class);
                    Double Lng = snapshot.child("lng").getValue(Double.class);

                    addGeoJson(Lng, Lat, name, userType, address, cellphoneNum);

                } catch (Exception e) {
                    if(Username.equals(snapshot.child("myRescuer").getValue(String.class))){

                        residentToBeRescue[0] = name;
                        residentToBeRescue[1] = 0.0;
                        residentToBeRescue[2] = 0.0;
                        residentToBeRescue[3] = snapshot.child("address").getValue(String.class);
                        residentToBeRescue[4] = snapshot.child("cellphoneNum").getValue(String.class);
                        residentToBeRescue[5] = snapshot.child("myRescuer").getValue(String.class);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //THIS WILL ADD RESIDENT TO GEOJSON
    public void addGeoJson(Double Lng, Double Lat, String name, String userType, String address, String cellphoneNum) {

        try {


            if (userType.equals(resident_str)) {
                residentFeatureList.add(Feature.fromGeometry(Point.fromLngLat(Lng, Lat)));
                residentFeatureList.get(needRescueCounter).addStringProperty(name_str, name);
                residentFeatureList.get(needRescueCounter).addStringProperty("cellphoneNumber", cellphoneNum);
                residentFeatureList.get(needRescueCounter).addStringProperty("address", address);
                checkCurrentLocation(name);
                residentFeatureList.get(needRescueCounter).addStringProperty(userType_str, userType);
                residentFeatureList.get(needRescueCounter).addNumberProperty("Lng", Lng);
                residentFeatureList.get(needRescueCounter).addNumberProperty("Lat", Lat);
                myRescuerListener(name);
                //onlineListener(name);
                residentSymbol();
                needRescueCounter++;

                if(name.equals(residentToBeRescue[0].toString())){
                    residentToBeRescue[1] = Lat;
                    residentToBeRescue[2] = Lng;
                    if(ThisUserType.equals(rescuer_str))
                    {
                        addressMarker.setBackgroundResource(R.drawable.small_marker_icon_rescuer);
                    }else{
                        addressMarker.setBackgroundResource(R.drawable.small_marker_icon);
                    }
                }
            } else if (userType.equals(rescuer_str)) {


                rescuerFeatureList.add(Feature.fromGeometry(Point.fromLngLat(Lng, Lat)));

                rescuerFeatureList.get(rescuerCounter).addStringProperty(name_str, name);
                rescuerFeatureList.get(rescuerCounter).addStringProperty("cellphoneNumber", cellphoneNum);
                rescuerFeatureList.get(rescuerCounter).addStringProperty("address", address);
                rescuerFeatureList.get(rescuerCounter).addStringProperty(userType_str, userType);
                rescuerFeatureList.get(rescuerCounter).addNumberProperty("Lng", Lng);
                rescuerFeatureList.get(rescuerCounter).addNumberProperty("Lat", Lat);
                assignedToListener(name);
                myRescuerListener(name);
                //onlineListener(name);
                rescuerSymbol();
                rescuerCounter++;
            } else if (userType.equals(admin_str)) {

                adminFeatureList.add(Feature.fromGeometry(Point.fromLngLat(Lng, Lat)));

                adminFeatureList.get(adminCounter).addStringProperty(name_str, name);
                adminFeatureList.get(adminCounter).addStringProperty("cellphoneNumber", cellphoneNum);
                adminFeatureList.get(adminCounter).addStringProperty("address", address);
                adminFeatureList.get(adminCounter).addStringProperty(userType_str, userType);
                adminFeatureList.get(adminCounter).addNumberProperty("Lng", Lng);
                adminFeatureList.get(adminCounter).addNumberProperty("Lat", Lat);
                myRescuerListener(name);
                //onlineListener(name);
                adminSymbol();
                adminCounter++;

            }

        } catch (Exception e) {

        }

    }



    //GET THE USER TYPE AND INDEX OF THE USER FEATURELIST
    public void getUserTypeAndIndex(String ParentName) {


        barangayRef.child(ParentName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("needRescue").getValue(String.class).equals("yes")) {
                    //Toast.makeText(Map_Activity.this,"test",Toast.LENGTH_LONG).show();
                    String userType = snapshot.child(userType_str).getValue(String.class);

                    List<Feature> userFeatureList = new ArrayList<>();

                    if (userType.equals(resident_str)) {
                        userFeatureList = residentFeatureList;

                    } else if (userType.equals(rescuer_str)) {
                        userFeatureList = rescuerFeatureList;


                    } else if (userType.equals(admin_str)) {

                        userFeatureList = adminFeatureList;
                    }


                    boolean emptyLatLng = true;

                    //CHECK ALL NAMES IN PROPERTIES TO GET WHO OWN THE LOCATION CHANGE
                    for (int index = 0; index < userFeatureList.size(); index++) {

                        String listName = userFeatureList.get(index).getStringProperty(name_str);

                        //IF FEATURE NAME IS EQUALS TO PARENT NAME OF THE CHANGED LOCATION


                        if (ParentName.equals(listName)) {
                            CheckAgainIfNeedRescue(ParentName, index);
                            emptyLatLng = false;
                            break;
                        }
                    }


                    //if latlng of the user is empty. if latlng id still empty try catch of addnewuser will handle it
                    if (emptyLatLng) {
                        addNewUser(ParentName);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    // THIS WILL CHECK IF THE RESIDENT NEEDS A RESCUE AGAIN IF NOT THEN REMOVE ON FEATURELIST
    public void CheckAgainIfNeedRescue(String name, int index) {

        barangayRef.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //THIS WILL CHECK IF NEED RESCUE CHANGE TO YES
                if (snapshot.child("needRescue").getValue(String.class).equals("yes")) {

                    UpdateSymbolLocation(name, index);

                } else {// IF CHANGE TO NO THEN REMOVE TO GEOJSON

                    featureListScanner(name, "remove");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //UPDATE THE LOCATION OF THE USER
    public void UpdateSymbolLocation(String name, int index) {

        // GET BARANGAY REFERENCE THEN NAME AND THEN GET ITS lATLNG
        barangayRef.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Double Lng = snapshot.child("lng").getValue(Double.class), Lat = snapshot.child("lat").getValue(Double.class);
                String cellphoneNumber = snapshot.child("cellphoneNum").getValue(String.class),
                        address = snapshot.child("address").getValue(String.class),
                        userType = snapshot.child(userType_str).getValue(String.class);


                try {
                    if (userType.equals(resident_str)) {

                        residentFeatureList.set(index, Feature.fromGeometry(Point.fromLngLat(Lng, Lat)));
                        residentFeatureList.get(index).addStringProperty(name_str, name);
                        residentFeatureList.get(index).addStringProperty("cellphoneNumber", cellphoneNumber);
                        residentFeatureList.get(index).addStringProperty("address", address);
                        checkCurrentLocation(name);
                        residentFeatureList.get(index).addStringProperty(userType_str, userType);
                        residentFeatureList.get(index).addNumberProperty("Lng", Lng);
                        residentFeatureList.get(index).addNumberProperty("Lat", Lat);
                        myRescuerListener(name);
                        //onlineListener(name);
                        residentSymbol();

                        if (name.equals(residentToBeRescue[0].toString())) {
                            //locationOfAssignedResident_forRescuer.setLatitude(Lat);
                            //locationOfAssignedResident_forRescuer.setLongitude(Lng)

                            residentToBeRescue[1] = Lat;
                            residentToBeRescue[2] = Lng;


                            if (!rescuerIsAvailable) {
                                Point destination = Point.fromLngLat((Double) residentToBeRescue[2],
                                        (Double) residentToBeRescue[1]);
                                Point origin = Point.fromLngLat(currentLocation.getLongitude(),
                                        currentLocation.getLatitude());

                                getRoute(origin, destination);
                                //setUpDoneBtnRescuer();
                            }

                        }

                    } else if (userType.equals(rescuer_str)) {

                        rescuerFeatureList.set(index, Feature.fromGeometry(Point.fromLngLat(Lng, Lat)));
                        rescuerFeatureList.get(index).addStringProperty(name_str, name);
                        rescuerFeatureList.get(index).addStringProperty("cellphoneNumber", cellphoneNumber);
                        rescuerFeatureList.get(index).addStringProperty("address", address);
                        rescuerFeatureList.get(index).addStringProperty(userType_str, userType);
                        rescuerFeatureList.get(index).addNumberProperty("Lng", Lng);
                        rescuerFeatureList.get(index).addNumberProperty("Lat", Lat);


                        assignedToListener(name);
                        myRescuerListener(name);
                        //onlineListener(name);
                        rescuerSymbol();

                    } else if (userType.equals(admin_str)) {

                        adminFeatureList.set(index, Feature.fromGeometry(Point.fromLngLat(Lng, Lat)));
                        adminFeatureList.get(index).addStringProperty(name_str, name);
                        adminFeatureList.get(index).addStringProperty("cellphoneNumber", cellphoneNumber);
                        adminFeatureList.get(index).addStringProperty("address", address);
                        adminFeatureList.get(index).addStringProperty(userType_str, userType);
                        adminFeatureList.get(index).addNumberProperty("Lng", Lng);
                        adminFeatureList.get(index).addNumberProperty("Lat", Lat);
                        myRescuerListener(name);
                        //onlineListener(name);
                        adminSymbol();

                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void checkCurrentLocation(String name){
        barangayRef.child(name).child("currentLocation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentLocation = snapshot.getValue(String.class);
                if(!currentLocation.equals("No current location")){

                    for (int index = 0; index<residentFeatureList.size();index++) {

                        String residentName = snapshot.getRef().getParent().getKey();
                        if(residentFeatureList.get(index).getStringProperty(name_str).equals(residentName)){
                            residentFeatureList.get(index).addStringProperty("address",currentLocation);
                            if(UI_NameTV.getText().toString().equals(residentName)){
                                UI_AddressTv.setText(currentLocation);
                            }
                            if(name.equals(residentToBeRescue[0])){
                                residentToBeRescue[3] = currentLocation;
                            }
                            break;
                        }
                    }

                }else{
                   //if current location change to no current location while listening
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //-----THIS WILL SCAN ALL VALUE OF ADMIN,RESCUER AND RESIDENT FEATURE LIST----------------------
    public void featureListScanner(String name, String purpose) {

        barangayRef.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                String userType = snapshot.child(userType_str).getValue(String.class);
                String myRescuer = snapshot.child("myRescuer").getValue(String.class);
                String assignedTo = "Not assigned yet";
                String assignedNextTo = "";

                try {
                    assignedTo = snapshot.child(assignedTo_str).getValue(String.class);
                    assignedNextTo = snapshot.child(assignedTo_str).getValue(String.class);
                } catch (Exception e) {
                }


                List<Feature> userFeatureList = new ArrayList<>();

                if (userType.equals(resident_str)) {
                    userFeatureList = residentFeatureList;
                } else if (userType.equals(rescuer_str)) {
                    userFeatureList = rescuerFeatureList;
                } else if (userType.equals(admin_str)) {
                    userFeatureList = adminFeatureList;
                }

                for (int index = 0; index < userFeatureList.size(); index++) {

                    String listName = userFeatureList.get(index).getStringProperty(name_str);

                    //IF FEATURE NAME IS EQUALS TO PARENT NAME OF THE CHANGED LOCATION AND DON'T NEED RESCUE THEN REMOVE LOCATION AND RESET LAYER
                    if (name.equals(listName)) {

                        if (purpose.equals("remove")) {

                            if (userType.equals(resident_str)) {
                                //Toast.makeText(Map_Activity.this,residentFeatureList.get(index).getStringProperty("name")+"1",Toast.LENGTH_SHORT).show();
                                residentFeatureList.remove(index);
                                //Toast.makeText(Map_Activity.this,residentFeatureList.get(index).getStringProperty("name")+"1",Toast.LENGTH_SHORT).show();
                                residentSymbol();
                                needRescueCounter--;
                            } else if (userType.equals(rescuer_str)) {
                                rescuerFeatureList.remove(index);
                                rescuerSymbol();
                                rescuerCounter--;
                                break;
                            } else if (userType.equals(admin_str)) {


                                adminFeatureList.remove(index);
                                adminSymbol();
                                adminCounter--;
                                break;

                            }
                            if (name.equals(UI_NameTV.getText().toString())) {
                                identification.setVisibility(View.GONE);
                            }

                            //THIS IS FOR SETTING VALUE OF MY RESCUER INTO FEATURE LIST
                        } else if (purpose.equals("set_myRescuer")) {

                            if (userType.equals(resident_str)) {



                                if(Username.equals(myRescuer)) {
                                    //add
                                    Double Lat = (Double) residentFeatureList.get(index).getNumberProperty("Lat");
                                    Double Lng = (Double) residentFeatureList.get(index).getNumberProperty("Lng");


                                    //get
                                    String cellphoneNumber = residentFeatureList.get(index).getStringProperty("cellphoneNumber");
                                    String address = residentFeatureList.get(index).getStringProperty("address");
                                    String residentName = residentFeatureList.get(index).getStringProperty(name_str);
                                    String thisMyRescuer = residentFeatureList.get(index).getStringProperty("myRescuer");

                                    //Toast.makeText(Map_Activity.this,residentName+"-"+myRescuer,Toast.LENGTH_SHORT).show();
                                    //resident to be rescue only
                                    residentToBeRescue[0] = residentName;
                                    residentToBeRescue[1] = Lat;
                                    residentToBeRescue[2] = Lng;
                                    residentToBeRescue[3] = address;
                                    residentToBeRescue[4] = cellphoneNumber;
                                    residentToBeRescue[5] = myRescuer;

                                    if(locationIsAccurate) {
                                        Point destination = Point.fromLngLat(Lng, Lat);
                                        Point origin = Point.fromLngLat(currentLocation.getLongitude(), currentLocation.getLatitude());
                                        getRoute(origin, destination);
                                    }
                                }


                                /*
                                //resident to be rescue only
                                residentToBeRescue[0] = residentFeatureList.get(index).getStringProperty("name");
                                residentToBeRescue[1] = residentFeatureList.get(index).getStringProperty("Lat");
                                residentToBeRescue[2] = residentFeatureList.get(index).getStringProperty("Lng");
                                residentToBeRescue[3] = residentFeatureList.get(index).getStringProperty("address");
                                residentToBeRescue[4] = residentFeatureList.get(index).getStringProperty("cellphoneNumber");
                                residentToBeRescue[5] = residentFeatureList.get(index).getStringProperty("myRescuer");

                                 */

                                residentFeatureList.get(index).addStringProperty("myRescuer", myRescuer);
                                residentSymbol();
                                if (name.equals(UI_NameTV.getText().toString())) {
                                    UI_myRescuerTv.setText(myRescuer);
                                    if (ThisUserType.equals(admin_str)) {
                                        //setUpDoneBtnAdmin(residentColor);
                                    }
                                }
                                break;

                            /*} else if (userType.equals(rescuer_str)) {

                                rescuerFeatureList.get(index).addStringProperty("myRescuer",myRescuer);
                                rescuerSymbol();
                                if(name.equals(UI_NameTV.getText().toString()))
                                    UI_myRescuerTv.setText(myRescuer);
                                    setUpDoneBtn(Color.parseColor("#FF9500"));

                                break;*/

                            } else if (userType.equals(admin_str)) {

                                adminFeatureList.get(index).addStringProperty("myRescuer", myRescuer);
                                adminSymbol();
                                if (name.equals(UI_NameTV.getText().toString()))
                                    UI_myRescuerTv.setText(myRescuer);


                                break;

                            }

                        } else if (purpose.equals("set_assignedTo")) {

                            try {
                                rescuerFeatureList.get(index).addStringProperty(assignedTo_str, assignedTo);
                                rescuerSymbol();
                            } catch (Exception e) {
                            }

                            if (name.equals(UI_NameTV.getText().toString())) {
                                UI_myRescuerTv.setText(assignedTo);
                                if (ThisUserType.equals(admin_str)) {
                                    //setUpDoneBtnAdmin(rescuerColor);
                                }
                            }

                            break;

                        } else if (purpose.equals("set_online")) {

                            String online = snapshot.child("online").getValue(String.class);

                            if (userType.equals(resident_str)) {
                                residentFeatureList.get(index).addStringProperty("online", online);
                                residentSymbol();
                                if (name.equals(UI_NameTV.getText().toString()))

                                    break;
                            } else if (userType.equals(rescuer_str)) {
                                rescuerFeatureList.get(index).addStringProperty("online", online);
                                rescuerSymbol();
                                if (name.equals(UI_NameTV.getText().toString()))


                                    break;
                            } else if (userType.equals(admin_str)) {
                                adminFeatureList.get(index).addStringProperty("online", online);
                                adminSymbol();
                                if (name.equals(UI_NameTV.getText().toString()))


                                    break;
                            }

                        } else if (purpose.equals("set_assignedNextTo")) {

                            rescuerFeatureList.get(index).addStringProperty("assignedNextTo", assignedNextTo);
                            rescuerSymbol();
                            if (name.equals(UI_NameTV.getText().toString()))
                                UI_myRescuerTv.setText(assignedNextTo);

                            break;

                        }


                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //-----THIS WILL SET CHANGE LISTENER FOR MY RESCUER---------------------------------------------
    public void myRescuerListener(String name) {
        residentmyrescuerLsnr = barangayRef.child(name).child("myRescuer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                featureListScanner(name, "set_myRescuer");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

/*
    public void onlineListener(String name) {
        barangayRef.child(name).child("online").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                featureListScanner(name, "set_online");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

 */

    public void assignedToListener(String name) {
        assigedtoLsnr = barangayRef.child(name).child(assignedTo_str).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                featureListScanner(name, "set_assignedTo");

                if (!snapshot.getValue(String.class).equals("Not assigned yet")) {
                    getResidentLocationForRescuer(name);

                    set_resident_to_be_rescue(snapshot.getValue(String.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void set_resident_to_be_rescue(String name){

        barangayRef.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {
                    residentToBeRescue[0] = name;
                    residentToBeRescue[1] = snapshot.child("lat").getValue(Double.class);
                    residentToBeRescue[2] = snapshot.child("lng").getValue(Double.class);
                    residentToBeRescue[3] = snapshot.child("address").getValue(String.class);
                    residentToBeRescue[4] = snapshot.child("cellphoneNum").getValue(String.class);
                    residentToBeRescue[5] = snapshot.child("myRescuer").getValue(String.class);
                }catch (Exception e){
                    residentToBeRescue[0] = name;
                    residentToBeRescue[1] = 0.0;
                    residentToBeRescue[2] = 0.0;
                    residentToBeRescue[3] = snapshot.child("address").getValue(String.class);
                    residentToBeRescue[4] = snapshot.child("cellphoneNum").getValue(String.class);
                    residentToBeRescue[5] = snapshot.child("myRescuer").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    //----------------------------------------------------------------------------------------------

    public void getResidentLocationForRescuer(String name) {


            if(ThisUserType.equals(rescuer_str)) {
                try {
                    if ((double) residentToBeRescue[1] == 0.0) {
                        rescuerIsAvailable = false;
                    }
                } catch (Exception e) {

                    barangayRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String assignedTo = snapshot.child(Username + "/assignedTo").getValue(String.class);
                            //Toast.makeText(Map_Activity.this,residentToBeRescue[1]+"",Toast.LENGTH_LONG).show();
                            if (assignedTo.equals("Not assigned yet")) {
                                //Toast.makeText(Map_Activity.this,"You are not assigned yet.",Toast.LENGTH_LONG).show();
                            } else {
                                rescuerIsAvailable = false;//Toast.makeText(Map_Activity.this,"Please wait until all data has been loaded.",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }


            for (int index = 0; index < residentFeatureList.size(); index++) {

                try {
                    //if resident to be rescue latittude == 0(location N/A)
                    if ((residentFeatureList.get(index).getStringProperty("myRescuer").equals(name) && rescuerIsAvailable)) {

                        rescuerIsAvailable = false;
/*
                        //add
                        Double Lat = (Double) residentFeatureList.get(index).getNumberProperty("Lat");
                        Double Lng = (Double) residentFeatureList.get(index).getNumberProperty("Lng");


                        //get
                        String cellphoneNumber = residentFeatureList.get(index).getStringProperty("cellphoneNumber");
                        String address = residentFeatureList.get(index).getStringProperty("address");
                        String residentName = residentFeatureList.get(index).getStringProperty(name_str);
                        String myRescuer = residentFeatureList.get(index).getStringProperty("myRescuer");

                        //resident to be rescue only
                        residentToBeRescue[0] = residentName;
                        residentToBeRescue[1] = Lat;
                        residentToBeRescue[2] = Lng;
                        residentToBeRescue[3] = address;
                        residentToBeRescue[4] = cellphoneNumber;
                        residentToBeRescue[5] = myRescuer;


 */
                        //locationOfAssignedResident_forRescuer.setLongitude(Lng);
                        //locationOfAssignedResident_forRescuer.setLatitude(Lat);
                        break;
                    }
                } catch (Exception e) {
                }
            }
    }
//__________________________________________________________________________________________________


    //______________________________________MAP ACTIONS_____________________________________________


    //-----MAP MARKERS------------------------------------------------------------------------------
    public void mapClickListener() {

        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public boolean onMapClick(@NonNull com.mapbox.mapboxsdk.geometry.LatLng point) {


                try {
                    //this will identify the clicked symbol
                    PointF screenPoint = mapboxMap.getProjection().toScreenLocation(point);

                    List<Feature> rescuerFeatures = mapboxMap.queryRenderedFeatures(screenPoint, RESCUER_LAYER_ID);
                    List<Feature> warningFeatures = mapboxMap.queryRenderedFeatures(screenPoint, WARNING_LAYER_ID);
                    List<Feature> residentFeatures = mapboxMap.queryRenderedFeatures(screenPoint, RESIDENT_LAYER_ID);
                    List<Feature> adminFeatures = mapboxMap.queryRenderedFeatures(screenPoint, ADMIN_LAYER_ID);

                    navigationBtn.setBackgroundResource(R.drawable.get_my_direction);
                    contact_container.setVisibility(View.VISIBLE);
                    levelTv.setVisibility(View.GONE);

                    //RESCUER
                    if (!rescuerFeatures.isEmpty()) {
                        Feature selectedFeature = rescuerFeatures.get(0);
                        String name = selectedFeature.getStringProperty(name_str);
                        String cellphoneNumber = selectedFeature.getStringProperty("cellphoneNumber");
                        String address = selectedFeature.getStringProperty("address");
                        String myRescuer = selectedFeature.getStringProperty("myRescuer");
                        String userType = selectedFeature.getStringProperty(userType_str);
                        String assignedTo = selectedFeature.getStringProperty(assignedTo_str);
                        String online = selectedFeature.getStringProperty("online");

                        ArrayList<String> contents = new ArrayList<>();
                        contents.add(name);
                        contents.add(address);
                        contents.add(cellphoneNumber);


                        double destinationLat = (double) selectedFeature.getNumberProperty("Lat");
                        double destinationLng = (double) selectedFeature.getNumberProperty("Lng");

                        Point origin = Point.fromLngLat(currentLocation.getLongitude(), currentLocation.getLatitude());
                        Point destination = Point.fromLngLat(destinationLng, destinationLat);
                        setIdentification(contents,origin,destination);

                        navigationBtn.setBackgroundResource(R.drawable.get_my_direction);
/*
                        UI_myRescuerTv.setText(assignedTo);
                        UI_NameTV.setText(name);
                        UI_AddressTv.setText(address);
                        UI_ContactTv.setText(cellphoneNumber);
                        identification.setVisibility(View.VISIBLE);
                        UI_myRescuer.setText("Assigned to rescue:");

                        UI_Name.setTextColor(rescuerColor);
                        UI_Address.setTextColor(rescuerColor);
                        UI_Contact.setTextColor(rescuerColor);
                        UI_myRescuer.setTextColor(rescuerColor);


                        //DoneBtn.setBackground(DoneBtn.getContext().getResources().getDrawable(R.color.rescuerColor));
                        DoneBtn.setBackgroundColor(rescuerColor);
                        TextBtn.setBackgroundColor(rescuerColor);
                        CallBtn.setBackgroundColor(rescuerColor);

 */
                        if (!assigning) {
                            selectedName = name;
                            selectedUserType = userType;
                        }

                        /*
                        //IF RESCUER IS ASSIGNED FIND A ROUTE TO ITS RESIDENT
                        if (!assignedTo.equals("Not assigned yet")) {
                            Point origin = Point.fromLngLat(rescuerLng, rescuerLat);
                            findRouteFrom(origin, assignedTo, resident_str);
                        }

                         */


                        if (ThisUserType.equals(admin_str)) {

                            DoneBtn.setVisibility(View.VISIBLE);
                           // setUpDoneBtnAdmin(rescuerColor);

                        } else {
                            DoneBtn.setVisibility(View.GONE);
                        }

                    }
                    //RESIDENT
                    else if (!residentFeatures.isEmpty()) {


                        Feature selectedFeature = residentFeatures.get(0);


                        String name = selectedFeature.getStringProperty(name_str);
                        String cellphoneNumber = selectedFeature.getStringProperty("cellphoneNumber");
                        String address = selectedFeature.getStringProperty("address");
                        String myRescuer = selectedFeature.getStringProperty("myRescuer");
                        String userType = selectedFeature.getStringProperty(userType_str);
                        String online = selectedFeature.getStringProperty("online");


                        ArrayList<String> contents = new ArrayList<>();
                        contents.add(name);
                        contents.add(address);
                        contents.add(cellphoneNumber);

                        double destinationLat = (double) selectedFeature.getNumberProperty("Lat");
                        double destinationLng = (double) selectedFeature.getNumberProperty("Lng");

                        Point origin = Point.fromLngLat(currentLocation.getLongitude(), currentLocation.getLatitude());
                        Point destination = Point.fromLngLat(destinationLng, destinationLat);

                        setIdentification(contents,origin,destination);
/*
                        UI_myRescuerTv.setText(myRescuer);
                        UI_NameTV.setText(name);
                        UI_AddressTv.setText(address);
                        UI_ContactTv.setText(cellphoneNumber);
                        UI_myRescuer.setText("Being rescue by:");

                        identification.setVisibility(View.VISIBLE);


                    UI_Name.setTextColor(residentColor);
                    UI_Address.setTextColor(residentColor);
                    UI_Contact.setTextColor(residentColor);
                    UI_myRescuer.setTextColor(residentColor);
                    DoneBtn.setBackgroundColor(residentColor);
                    CallBtn.setBackgroundColor(residentColor);
                    TextBtn.setBackgroundColor(residentColor);

                     */

                        residentLat = (double) selectedFeature.getNumberProperty("Lat");
                        residentLng = (double) selectedFeature.getNumberProperty("Lng");

                        if (!assigning) {
                            selectedName = name;
                            selectedUserType = userType;
                        }

                        /*
                        // IF RESIDENT IS CLICKED AND HAS A RESCUER FIND ROUTE TO ITS RESCUER
                        if (!myRescuer.equals(no_rescuer_assigned)) {
                            Point origin = Point.fromLngLat(residentLng, residentLat);
                            findRouteFrom(origin, myRescuer, rescuer_str);
                        }

                         */


                        if (ThisUserType.equals(rescuer_str) && Username.equals(myRescuer)) {
                            DoneBtn.setVisibility(View.VISIBLE);

                            if (locationIsAccurate) {
                                //DoneBtn.setEnabled(true);
                               // DoneBtn.setBackgroundResource(R.drawable.done);
                                if(ThisUserType.equals(rescuer_str))
                                {
                                    navigationBtn.setBackgroundResource(R.drawable.direction_onrescuer);
                                }else{
                                    navigationBtn.setBackgroundResource(R.drawable.direction_on);
                                }
                                //ShowMyLocation.setBackgroundResource(R.drawable.gps_activated);
                            } else {
                                //DoneBtn.setEnabled(false);
                                //DoneBtn.setBackgroundResource(R.drawable.disable_done);
                                navigationBtn.setBackgroundResource(R.drawable.get_my_direction);
                                //ShowMyLocation.setBackgroundResource(R.drawable.gps_activated);
                            }

                        } else if (ThisUserType.equals(admin_str)) {
                            DoneBtn.setVisibility(View.VISIBLE);
                            //setUpDoneBtnAdmin(residentColor);

                        } else {
                            DoneBtn.setVisibility(View.GONE);
                        }


                    }
                    //ADMIN
                    else if (!adminFeatures.isEmpty()) {
                        Feature selectedFeature = adminFeatures.get(0);

                        String name = selectedFeature.getStringProperty(name_str);
                        String cellphoneNumber = selectedFeature.getStringProperty("cellphoneNumber");
                        String address = selectedFeature.getStringProperty("address");
                        String myRescuer = selectedFeature.getStringProperty("myRescuer");
                        String userType = selectedFeature.getStringProperty(userType_str);
                        String online = selectedFeature.getStringProperty("online");

                        UI_myRescuerTv.setText(myRescuer);
                        UI_NameTV.setText(name);
                        UI_AddressTv.setText(address);
                        UI_ContactTv.setText(cellphoneNumber);
                        DoneBtn.setVisibility(View.GONE);

                    /*
                    CallBtn.setBackgroundColor(adminColor);
                    TextBtn.setBackgroundColor(adminColor);
                    UI_Name.setTextColor(adminColor);
                    UI_Address.setTextColor(adminColor);
                    UI_Contact.setTextColor(adminColor);
                    UI_myRescuer.setTextColor(adminColor);

                     */


                        if (!assigning) {
                            selectedName = name;
                            selectedUserType = userType;
                        }

                        //navigationMapRoute.updateRouteVisibilityTo(false);
                    }
                    //WARNING
                    else if (!warningFeatures.isEmpty()) {
                        Feature selectedFeature = warningFeatures.get(0);
                        String name = selectedFeature.getStringProperty(name_str);
                        String level = selectedFeature.getStringProperty("level");
                        String location = selectedFeature.getStringProperty("location");


                        ArrayList<String> contents = new ArrayList<>();
                        contents.add(name);
                        contents.add(location);
                        contents.add("N/A");

                        double destinationLat = (double) selectedFeature.getNumberProperty("Lat");
                        double destinationLng = (double) selectedFeature.getNumberProperty("Lng");

                        Point origin = Point.fromLngLat(currentLocation.getLongitude(), currentLocation.getLatitude());
                        Point destination = Point.fromLngLat(destinationLng, destinationLat);

                        setIdentification(contents,origin,destination);

                        navigationBtn.setBackgroundResource(R.drawable.get_my_direction);
/*
                    CallBtn.setBackgroundColor(warningColor);
                    TextBtn.setBackgroundColor(warningColor);
                    UI_Name.setTextColor(warningColor);
                    UI_Address.setTextColor(warningColor);
                    UI_Contact.setTextColor(warningColor);
                    UI_myRescuer.setTextColor(warningColor);

 */
                        levelTv.setText(level);
                        levelTv.setVisibility(View.VISIBLE);
                        UI_NameTV.setText(name);
                        //UI_AddressTv.setText("Unkown");
                        //UI_ContactTv.setText("N/A");
                        DoneBtn.setVisibility(View.GONE);
                        identification.setVisibility(View.VISIBLE);
                        contact_container.setVisibility(View.INVISIBLE);

                        if (!assigning) {
                            selectedName = warning_str;
                            selectedUserType = warning_str;
                        }

                        //navigationMapRoute.updateRouteVisibilityTo(false);
                    } else {
                        //navigationMapRoute.updateRouteVisibilityTo(false);
                        identification.setVisibility(View.GONE);
                        address_container.setVisibility(View.GONE);
                        DoneBtn.setVisibility(View.GONE);

                        navigationBtn.setBackgroundResource(R.drawable.get_my_direction);
                    }

                } catch (Exception e) {
                    //LatLng.setVisibility(View.VISIBLE);
                    //LatLng.setText(e.toString());
                    //Toast.makeText(Map_Activity.this,e.toString(),Toast.LENGTH_LONG).show();
                }
                ;
                return true;
            }
        });
    }
    //----------------------------------------------------------------------------------------------



    public void setIdentification(ArrayList<String> contents, Point origin, Point destination){



        UI_NameTV.setText(contents.get(0));
        UI_AddressTv.setText(contents.get(1));
        UI_ContactTv.setText(contents.get(2));
        distanceTv.setText("-- --");
        durationTv.setText("-- --");

        if(destination.latitude() != 0) {



            if(ThisUserType.equals(rescuer_str))
            {
                addressMarker.setBackgroundResource(R.drawable.small_marker_icon_rescuer);
            }else{
                addressMarker.setBackgroundResource(R.drawable.small_marker_icon);
            }

            NavigationRoute.builder(this)
                    .accessToken(Mapbox.getAccessToken())
                    .origin(origin)
                    .destination(destination)
                    .enableRefresh(true)
                    .build()
                    .getRoute(new Callback<DirectionsResponse>() {
                        @SuppressLint({"LogNotTimber", "SetTextI18n"})
                        @Override
                        public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {


                            if (response.body() == null) {
                                Log.e(TAG, "No routes found,check rigth user and access token");
                                return;
                            } else if (response.body().routes().size() == 0) {
                                Log.e(TAG, "No routes found");
                                return;
                            }

                            DirectionsRoute currentRoute = response.body().routes().get(0);


                            //----------------DISTANCE AND DURATION--------------------------------------------
                            Double distance = currentRoute.distance();
                            Double duration = currentRoute.duration();
                            if (distance > 1000) {
                                distanceTv.setText(String.format("%.2f", currentRoute.distance() / 1000) + " km");
                            } else {
                                int min = (int) (distance / 1);
                                distanceTv.setText(min + " m");
                            }

                            if (duration > 3600) {
                                int hour = (int) (duration / 3600);
                                int min = (int) ((duration % 3600) / 60);
                                durationTv.setText(hour + " hr " + min + " min");
                            } else {
                                int min = (int) (duration / 60);
                                durationTv.setText(min + " min");
                            }
                            //----------------------------------------------------------------------------------

                        }

                        @Override
                        public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                            Log.e(TAG, "error: " + t.getMessage());
                        }
                    });



        }else{//if resident to be rescue has no location
            distanceTv.setText("N/A");
            durationTv.setText("N/A");
            addressMarker.setBackgroundResource(R.drawable.no_location_icon);
        }

        address_container.setVisibility(View.VISIBLE);
        identification.setVisibility(View.VISIBLE);
    }



    public void setUpDoneBtnAdmin(int color) {

        String myRescuer = UI_myRescuerTv.getText().toString();
        if (myRescuer.equals(no_rescuer_assigned) || myRescuer.equals("Not assigned yet")) {
            DoneBtn.setEnabled(true);
            //DoneBtn.setBackgroundResource(R.drawable.done);

            navigationMapRoute.updateRouteVisibilityTo(false);
        } else {
            DoneBtn.setEnabled(false);
            //DoneBtn.setBackgroundResource(R.drawable.disable_done);
        }


    }

    //-----ROUTE------------------------------------------------------------------------------------
    public void findRouteFrom(Point origin, String name, String userType) {

        if (!assigning) {

            if (userType.equals(resident_str)) {
                for (int counter = 0; counter < residentFeatureList.size(); counter++) {
                    if (residentFeatureList.get(counter).getStringProperty(name_str).equals(name)) {
                        Point destination = Point.fromLngLat(
                                (double) residentFeatureList.get(counter).getNumberProperty("Lng"),
                                (double) residentFeatureList.get(counter).getNumberProperty("Lat"));

                        getRoute(origin, destination);
                    }
                }


            } else if (userType.equals(rescuer_str)) {

                for (int counter = 0; counter < rescuerFeatureList.size(); counter++) {
                    if (rescuerFeatureList.get(counter).getStringProperty(name_str).equals(name)) {
                        Point destination = Point.fromLngLat(
                                (double) rescuerFeatureList.get(counter).getNumberProperty("Lng"),
                                (double) rescuerFeatureList.get(counter).getNumberProperty("Lat"));
                        getRoute(destination, origin);
                    }
                }


            }
        }
    }



    private void getRoute(Point origin, Point destination) {

        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .enableRefresh(true)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @SuppressLint({"LogNotTimber", "SetTextI18n"})
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {


                        if (response.body() == null) {
                            Log.e(TAG, "No routes found,check rigth user and access token");
                            return;
                        } else if (response.body().routes().size() == 0) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        DirectionsRoute currentRoute = response.body().routes().get(0);


                        if (navigationMapRoute != null) {

                            navigationMapRoute.updateRouteVisibilityTo(false);
                        } else {

                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap);
                        }
                        //navigationMapRoute.showAlternativeRoutes(true);
                        navigationMapRoute.addRoute(currentRoute);


                 //----------------DISTANCE AND DURATION--------------------------------------------
                        if (ThisUserType.equals(rescuer_str)) {

                            if(residentToBeRescue[0].toString().equals(UI_NameTV.getText().toString())) {
                                Double distance = currentRoute.distance();
                                Double duration = currentRoute.duration();

                                if (distance > 1000) {
                                    distanceTv.setText(String.format("%.2f", currentRoute.distance() / 1000) + " km");
                                } else {
                                    int min = (int) (distance / 1);
                                    distanceTv.setText(min + " m");
                                }

                                if (duration > 3600) {
                                    int hour = (int) (duration / 3600);
                                    int min = (int) ((duration % 3600) / 60);
                                    durationTv.setText(hour + " hr " + min + " min");
                                } else {
                                    int min = (int) (duration / 60);
                                    durationTv.setText(min + " min");
                                }
                            }
                        }else if(ThisUserType.equals(resident_str)){
                            if(rescuerForThisUser[0].toString().equals(UI_NameTV.getText().toString())) {
                                Double distance = currentRoute.distance();
                                Double duration = currentRoute.duration();

                                if (distance > 1000) {
                                    distanceTv.setText(String.format("%.2f", currentRoute.distance() / 1000) + " km");
                                } else {
                                    int min = (int) (distance / 1);
                                    distanceTv.setText(min + " m");
                                }

                                if (duration > 3600) {
                                    int hour = (int) (duration / 3600);
                                    int min = (int) ((duration % 3600) / 60);
                                    durationTv.setText(hour + " hr " + min + " min");
                                } else {
                                    int min = (int) (duration / 60);
                                    durationTv.setText(min + " min");
                                }
                            }
                        }
                //----------------------------------------------------------------------------------





                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e(TAG, "error: " + t.getMessage());
                    }
                });

    }
    //----------------------------------------------------------------------------------------------


    //-----SYMBOLS----------------------------------------------------------------------------------
    public void warningSymbol() {

        try {
            //reset geojson MapStyle source
            warningMapSource.setGeoJson(FeatureCollection.fromFeatures(warningFeatureList));
            //reset map layer
            MapStyle.removeLayer(warningSymbolLayer);
            MapStyle.addLayer(warningSymbolLayer);
        }catch (Exception e){}


    }

    public void rescuerSymbol() {

        //reset geojson MapStyle source
        rescuerMapSource.setGeoJson(FeatureCollection.fromFeatures(rescuerFeatureList));

        //reset map layer
        try {
            if (!assigning || selectedUserType.equals(resident_str)) {
                MapStyle.removeLayer(rescuerSymbolLayer);
                MapStyle.addLayer(rescuerSymbolLayer);
            }
        } catch (Exception e) {
        }

    }

    public void residentSymbol() {

        //reset geojson MapStyle source
        residentMapSource.setGeoJson(FeatureCollection.fromFeatures(residentFeatureList));

        //reset map layer
        try {
            if (!assigning || selectedUserType.equals(rescuer_str)) {
                MapStyle.removeLayer(residentSymbolLayer);
                MapStyle.addLayer(residentSymbolLayer);
            }
        } catch (Exception e) {
        }

    }

    public void adminSymbol() {

        //reset geojson MapStyle source
        adminMapSource.setGeoJson(FeatureCollection.fromFeatures(adminFeatureList));

        //reset map layer
        try {
            if (!assigning) {
                MapStyle.removeLayer(adminSymbolLayer);
                MapStyle.addLayer(adminSymbolLayer);
            }
        } catch (Exception e) {
        }

    }
    //----------------------------------------------------------------------------------------------



    //______________________________________________________________________________________________


    //_________________________________MAPBOX DEFAULT SETUP_________________________________________

    //-----ON MAP READY-----------------------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        //This will Disable mapbox logo and compass
        mapboxMap.getUiSettings().setAttributionEnabled(false);
        mapboxMap.getUiSettings().setCompassEnabled(false);
        mapboxMap.getUiSettings().setLogoEnabled(false);

        //DECLARE MAPSOURCE
        residentMapSource = new GeoJsonSource(RESIDENT_SOURCE_ID, FeatureCollection.fromFeatures(residentFeatureList));
        //DECLARE MAPLAYER
        residentSymbolLayer = new SymbolLayer(RESIDENT_LAYER_ID, RESIDENT_SOURCE_ID);
        residentSymbolLayer.withProperties(iconImage(RESIDENT_ICON_ID), iconAllowOverlap(true), iconIgnorePlacement(true));

        //DECLARE MAPSOURCE
        adminMapSource = new GeoJsonSource(ADMIN_SOURCE_ID, FeatureCollection.fromFeatures(adminFeatureList));
        //DECLARE MAPLAYER
        adminSymbolLayer = new SymbolLayer(ADMIN_LAYER_ID, ADMIN_SOURCE_ID);
        adminSymbolLayer.withProperties(iconImage(ADMIN_ICON_ID), iconAllowOverlap(true), iconIgnorePlacement(true));

        //DECLARE MAPSOURCE
        rescuerMapSource = new GeoJsonSource(RESCUER_SOURCE_ID, FeatureCollection.fromFeatures(rescuerFeatureList));
        //DECLARE MAPLAYER
        rescuerSymbolLayer = new SymbolLayer(RESCUER_LAYER_ID, RESCUER_SOURCE_ID);
        rescuerSymbolLayer.withProperties(iconImage(RESCUER_ICON_ID), iconAllowOverlap(true), iconIgnorePlacement(true));

        //DECLARE MAPSOURCE
        warningMapSource = new GeoJsonSource(WARNING_SOURCE_ID, FeatureCollection.fromFeatures(warningFeatureList));
        //DECLARE MAPLAYER
        warningSymbolLayer = new SymbolLayer(WARNING_LAYER_ID, WARNING_SOURCE_ID);
        warningSymbolLayer.withProperties(iconImage(WARNING_ICON_ID), iconAllowOverlap(true), iconIgnorePlacement(true));


        this.mapboxMap = mapboxMap;
        onMapDrag();

        mapboxMap.setStyle(//styleMode
                new Style.Builder().fromUri(styleMode).withImage(RESIDENT_ICON_ID, BitmapFactory.decodeResource(Map_Activity.this.getResources(),
                        R.drawable.resident_marker)).withSource(residentMapSource).withLayer(residentSymbolLayer), new Style.OnStyleLoaded() {

                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        OpenMobileData.setVisibility(View.GONE);

                        MapStyle = style;


                        MapStyle.addImage(RESCUER_ICON_ID, BitmapFactory.decodeResource(Map_Activity.this.getResources(), R.drawable.rescuer_marker));
                        MapStyle.addSource(rescuerMapSource);
                        MapStyle.addLayer(rescuerSymbolLayer);
                        MapStyle.addImage(WARNING_ICON_ID, BitmapFactory.decodeResource(Map_Activity.this.getResources(), R.drawable.flood_monitoring_system));
                        MapStyle.addSource(warningMapSource);
                        MapStyle.addLayer(warningSymbolLayer);
                        MapStyle.addImage(ADMIN_ICON_ID, BitmapFactory.decodeResource(Map_Activity.this.getResources(), R.drawable.admin));
                        MapStyle.addSource(adminMapSource);
                        MapStyle.addLayer(adminSymbolLayer);


                        enableLocationComponent(style);
                        mapClickListener();
                        residentSymbol();
                        rescuerSymbol();
                        adminSymbol();
                        warningSymbol();

// Set up the OfflineManager
                        offlineManager = OfflineManager.getInstance(Map_Activity.this);

// Create a bounding box for the offline region
                        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                .include(new LatLng(LatNorth, LngEast)) // Northeast
                                .include(new LatLng(LatSouth, LngWest)) // Southwest
                                .build();

// Define the offline region
                        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                                style.getUri(),
                                latLngBounds,
                                12,
                                15,
                                Map_Activity.this.getResources().getDisplayMetrics().density);

// Set the metadata
                        byte[] metadata;
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put(JSON_FIELD_REGION_NAME, RegionName);
                            String json = jsonObject.toString();
                            metadata = json.getBytes(JSON_CHARSET);
                        } catch (Exception exception) {
                            Timber.e("Failed to encode metadata: %s", exception.getMessage());
                            metadata = null;
                        }

// Create the region asynchronously
                        if (metadata != null) {
                            offlineManager.createOfflineRegion(
                                    definition,
                                    metadata,
                                    new OfflineManager.CreateOfflineRegionCallback() {
                                        @Override
                                        public void onCreate(OfflineRegion offlineRegion) {
                                            offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);


                                            if (OnStart) {
                                                OnStart = false;
// Display the download progress bar
                                                LoadingTV = findViewById(R.id.LoadingTV);
                                                progressBar = findViewById(R.id.progress_bar);
                                                startProgress();


// Monitor the download progress using setObserver
                                                offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                                                    @Override
                                                    public void onStatusChanged(OfflineRegionStatus status) {

                                                        // Calculate the download percentage and update the progress bar
                                                        double percentage = status.getRequiredResourceCount() >= 0
                                                                ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                                                                0.0;

                                                        if (status.isComplete()) {
                                                            // Loading complete
                                                            ///LocationCompass();
                                                            endProgress("Loading complete");



                                                            //read_RescueMe("");delete

                                                        } else if (status.isRequiredResourceCountPrecise()) {
                                                            // Switch to determinate state
                                                            setPercentage((int) Math.round(percentage));
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(OfflineRegionError error) {
                                                        // If an error occurs, print to logcat
                                                        Timber.e("onError reason: %s", error.getReason());
                                                        Timber.e("onError message: %s", error.getMessage());
                                                    }

                                                    @Override
                                                    public void mapboxTileCountLimitExceeded(long limit) {
                                                        // Notify if offline region exceeds maximum tile count
                                                        limit = 4000;
                                                        Timber.e("Mapbox tile count limit exceeded: %s", limit);
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onError(String error) {
                                            Timber.e("Error: %s", error);
                                        }
                                    });
                        }
                    }
                });


    }
    //---a-------------------------------------------------------------------------------------------



    @SuppressWarnings({"MissingPermission"})
    public void displayMyLocation() {
        try {
            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(showComponentLocationClicked);
        } catch (Exception e) {
        }
    }
    //----------------------------------------------------------------------------------------------


    //-----REQUESTING TO TURN ON GPS----------------------------------------------------------------
    public void RequestToEnableGPS() {

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
                    GPSisOn = true;
                    //ShowMyLocation.setBackgroundResource(R.drawable.gps_tracked_new_ui);

                } catch (ApiException exception) {      //This will request to enable gps if gps is off

                    GPSisOn = false;
                    searchingGPS.setVisibility(View.GONE);
                    locationIsAccurate = false;

                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        Map_Activity.this,
                                        LocationRequest.PRIORITY_HIGH_ACCURACY);

                                displayMyLocation();


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
                        GPSisOn = true;
                        if (showComponentLocationClicked) {

                            barangayRef.child(Username).child("needRescue").setValue("yes");
                            searchingGPS.setVisibility(View.VISIBLE);

                            if (ThisUserType.equals(rescuer_str)) {
                                getResidentLocationForRescuer(Username);
                            }
                            //LatLng.setText("Please do not leave the app until we find your location.");
                            //LatLng.setVisibility(View.VISIBLE);
                            zoomWhileTracking();
                            displayMyLocation();
                        }

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


    //-----ENABLE LOCATION--------------------------------------------------------------------------
    @SuppressLint("SetTextI18n")
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            locationComponent = mapboxMap.getLocationComponent();

            // Set the LocationComponent activation options
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .build();


            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            locationComponent.setLocationComponentEnabled(false);

            locationComponent.setRenderMode(RenderMode.COMPASS);

            initLocationEngine();

        } else {//If the permission is not granted yet

            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);

        }
    }

    //----------------------------------------------------------------------------------------------

    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);

    }
    //----------------------------------------------------------------------------------------------


    //-----PERMISSION MANAGER-----------------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(Map_Activity.this, requestCode+"<>", Toast.LENGTH_SHORT).show();
      //SMS
        switch (requestCode) {
            case 100:{//if permission is granted

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSOS();
                } else {
                    return;
                }
            }

        }


    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {


        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    MapStyle = style;
                    enableLocationComponent(style);
                    RequestToEnableGPS();
                }
            });
        }

    }
    //----------------------------------------------------------------------------------------------


    int gps_counter = 0;




    //_________________________________LOCATION CALLBACK CLASS______________________________________
    private class LocationChangeListeningActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<Map_Activity> activityWeakReference;

        LocationChangeListeningActivityLocationCallback(Map_Activity activity) {
            this.activityWeakReference = new WeakReference<>(activity);

        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.ongoing rescue
         */
        @RequiresApi(api = Build.VERSION_CODES.O)
        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        @Override
        public void onSuccess(LocationEngineResult result) {



            if (showComponentLocationClicked)
                gps_counter++;


            Map_Activity activity = activityWeakReference.get();


            if (activity != null) {
                Location location = result.getLastLocation();
                if (location == null) {
                    return;
                }

                /*
                if(activityStop){
                    testcount++;
                    if(testcount ==20){
                        barangayRef.child("Resident1").child("needRescue").setValue("no");
                    }
                }*/


                double result_lat = result.getLastLocation().getLatitude();
                double result_lng = result.getLastLocation().getLongitude();

                // Pass the new location to the Maps SDK's LocationComponent
                if (activity.mapboxMap != null && result.getLastLocation()!=null) {

                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                    try {
                        int speed = (int) (result.getLastLocation().getSpeed()*5);
                        speedTv.setText(speed+"\nkm/h");
                    }catch (Exception e){

                    }


                    if (showComponentLocationClicked&&result.getLocations().size()!=0) {

                        //if location is now accurate send the lat and lng to database
                        if (result.getLocations().get(0).getAccuracy() < gpsAccuracy) {
                            gpsAccuracy = 10;
                            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                                GPSisOn = true;
                            else
                                GPSisOn = false;

                            //  WHEN USER MOVE 20 METERS THE LOCATION WILL BE UPDATE IN FIREBASE-----------------
                            if ((currentLocation.getLongitude() - 0.00006 >= result_lng) || (currentLocation.getLongitude() + 0.00006 <= result_lng) ||
                                    (currentLocation.getLatitude() - 0.00006 >= result_lat) || (currentLocation.getLatitude() + 0.00006 <= result_lat)) {


                                //ShowMyLocation.setText(residentExtras);
                                //setUpDoneBtnRescuer();
                                barangayRef.child(Username).child("lat").setValue(result_lat);
                                barangayRef.child(Username).child("lng").setValue(result_lng);
                                //LatLng.setText("Your current location has been sent. please keep your gps on");

                                //for rescuer
                                if(ThisUserType.equals(rescuer_str)) {

                                    if (!rescuerIsAvailable) {

                                        try {
                                            Point destination = Point.fromLngLat((Double) residentToBeRescue[2],
                                                    (Double) residentToBeRescue[1]);
                                            Point origin = Point.fromLngLat(result_lng,
                                                    result_lat);
                                            getRoute(origin, destination);
                                        }catch(Exception e){}

                                    } else {
                                        getResidentLocationForRescuer(Username);
                                    }

                                //for resident
                                }else if(ThisUserType.equals(resident_str)&&iHaveRescuer){
                                    try{
                                        Point destination = Point.fromLngLat((Double) rescuerForThisUser[2],
                                                (Double) rescuerForThisUser[1]);
                                        Point origin = Point.fromLngLat(result_lng,
                                                result_lat);

                                        getRoute(origin, destination);
                                    }catch(Exception e){}
                                    }

                                //Toast.makeText(Map_Activity.this, "Location has been sent", Toast.LENGTH_LONG).show();
                                if(!locationIsAccurate) {

                                    locationIsAccurate = true;
                                    if(ThisUserType.equals(rescuer_str))
                                    {
                                        ShowMyLocation.setBackgroundResource(R.drawable.gps_activated_rescuer);
                                    }else{
                                        ShowMyLocation.setBackgroundResource(R.drawable.gps_activated);
                                    }
                                    searchingGPS.setVisibility(View.GONE);

                                    if(!rescuerIsAvailable) {
                                        gpsFunctions("navigationButton");
                                        if(ThisUserType.equals(rescuer_str))
                                        {
                                            navigationBtn.setBackgroundResource(R.drawable.direction_onrescuer);
                                        }else{
                                            navigationBtn.setBackgroundResource(R.drawable.direction_on);
                                        }
                                    }


                                }

                                currentLocation = result.getLocations().get(0);

                                //ShowMyLocation.setBackgroundResource(R.drawable.gps_activated);toast
                            }

                        }

                    }else{
                        locationIsAccurate = false;
                    }

                }
            }
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location can't be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            Map_Activity activity = activityWeakReference.get();
            if (activity != null) {

                //Toast.makeText(activity, exception.getLocalizedMessage(),
                 //       Toast.LENGTH_SHORT).show();
            }
        }
    }
    //----------------------------------------------------------------------------------------------



//__________________________________________________________________________________________________

//animation

//__________________________________BUTTON AREA_____________________________________________________


    //-----DONE BUTTON------------------------------------------------------------------------------
    public void doneButton() {
        DoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //second click

                doneBtnDialogBox.show();
                 /*else if (ThisUserType.equals(admin_str)) {
                    if (assigning) {

                        if (selectedUserType.equals(rescuer_str)) {

                            barangayRef.child(UI_NameTV.getText().toString()).child("myRescuer").setValue(selectedName);
                            barangayRef.child(selectedName).child(assignedTo_str).setValue(UI_NameTV.getText().toString());
                            try {
                                MapStyle.addLayer(rescuerSymbolLayer);
                                MapStyle.addLayer(adminSymbolLayer);
                            } catch (Exception e) {
                            }


                        } else if (selectedUserType.equals(resident_str)) {
                            barangayRef.child(UI_NameTV.getText().toString()).child(assignedTo_str).setValue(selectedName);
                            barangayRef.child(selectedName).child("myRescuer").setValue(UI_NameTV.getText().toString());
                            try {
                                MapStyle.addLayer(residentSymbolLayer);
                                MapStyle.addLayer(adminSymbolLayer);
                            } catch (Exception e) {
                            }
                        }


                        assigning = false;

                        identification.setVisibility(View.VISIBLE);
                        ShowMyLocation.setVisibility(View.VISIBLE);
                        DoneBtn.setVisibility(View.VISIBLE);

                        //LatLng.setVisibility(View.INVISIBLE);
                        Point origin = Point.fromLngLat(rescuerLng, rescuerLat);
                        Point destination = Point.fromLngLat(residentLng, residentLat);
                        getRoute(origin, destination);


                    }//first click

                    else {

                        if (selectedUserType.equals(rescuer_str)) {
                            selectedName = UI_NameTV.getText().toString();

                            try {
                                MapStyle.removeLayer(rescuerSymbolLayer);
                                MapStyle.removeLayer(adminSymbolLayer);
                            } catch (Exception e) {
                            }

                            //LatLng.setText("Please select resident to be rescued by " + selectedName);
                            //Toast.makeText(Map_Activity.this,"",Toast.LENGTH_LONG);
                        } else if (selectedUserType.equals(resident_str)) {
                            selectedName = UI_NameTV.getText().toString();

                            try {
                                MapStyle.removeLayer(residentSymbolLayer);
                                MapStyle.removeLayer(adminSymbolLayer);
                            } catch (Exception e) {
                            }

                            //LatLng.setText("Please assign rescuer for " + selectedName);
                        }
                        assigning = true;

                        identification.setVisibility(View.GONE);
                        ShowMyLocation.setVisibility(View.INVISIBLE);
                        DoneBtn.setVisibility(View.GONE);

                        //LatLng.setVisibility(View.VISIBLE);


                    }

                }*/
            }
        });
    }
    //----------------------------------------------------------------------------------------------

    //-----SHOW LOCATION BUTTON---------------------------------------------------------------------

    public void showMyLocation() {
        ShowMyLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            gpsFunctions("showMyLocationButton");

            }
        });

    }

    //-----NAVIGATION BUTTON------------------------------------------------------------------------
    public void navigation_btn(){
        navigationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(Map_Activity.this,residentToBeRescue[0]+"\n"+residentToBeRescue[1]+"\n"+residentToBeRescue[2]+"\n"+residentToBeRescue[3]+"\n"+residentToBeRescue[4]+"\n"+residentToBeRescue[5]+"\n",Toast.LENGTH_LONG).show();
                //navigationBtn.setBackgroundResource(R.drawable.direction_on);
                //address_container.setVisibility(View.VISIBLE);
                //identification.setVisibility(View.VISIBLE);
                gpsFunctions("navigationButton");

            }
        });
    }


    //-----SOS BUTTON-------------------------------------------------------------------------------
    public void sos_btn(){
        sosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sosReqOngoing){
                    cancleSosDialogBox.show();
                }else {
                    sosDialogBox.show();
                }

            }
        });
    }

    public void gpsFunctions(String button){

        //This will check if gps is on, if not request to enable gps
        RequestToEnableGPS();

        if (GPSisOn) {



            if(button.equals("sos"))
                barangayRef.child(Username).child("needRescue").setValue("yes");

            if(locationIsAccurate) {
                try {
                    if(ThisUserType.equals(rescuer_str))
                    {
                        ShowMyLocation.setBackgroundResource(R.drawable.gps_activated_rescuer);
                    }else{
                        ShowMyLocation.setBackgroundResource(R.drawable.gps_activated);
                    }
                    searchingGPS.setVisibility(View.GONE);
                } catch (Exception e) {
                }
            }else{
                searchingGPS.setVisibility(View.VISIBLE);
            }


            if (ThisUserType.equals(rescuer_str)) {

                getResidentLocationForRescuer(Username);//this only work one time
                //NAVIGATION BUTTON ONLY
                if(button.equals("navigationButton")) {

                    //IF USER IS RESCUER AND IS NOT AVAILABLE
                    if (!rescuerIsAvailable) {
                        try {
                            ArrayList<String> contents = new ArrayList<>();
                            contents.add(residentToBeRescue[0].toString());//name
                            contents.add(residentToBeRescue[3].toString());//address
                            contents.add(residentToBeRescue[4].toString());//contact


                            double destinationLat = (double) residentToBeRescue[1];
                            double destinationLng = (double) residentToBeRescue[2];
                            //Toast.makeText(Map_Activity.this, destinationLat+"--"+destinationLng, Toast.LENGTH_SHORT).show();
                            Point origin = Point.fromLngLat(currentLocation.getLongitude(), currentLocation.getLatitude());
                            Point destination = Point.fromLngLat(destinationLng, destinationLat);

                            contact_container.setVisibility(View.VISIBLE);
                            DoneBtn.setVisibility(View.VISIBLE);
                            levelTv.setVisibility(View.GONE);
                            if (locationIsAccurate) {
                                if (ThisUserType.equals(rescuer_str)) {
                                    navigationBtn.setBackgroundResource(R.drawable.direction_onrescuer);
                                    ShowMyLocation.setBackgroundResource(R.drawable.gps_activated_rescuer);
                                } else {
                                    navigationBtn.setBackgroundResource(R.drawable.direction_on);
                                    ShowMyLocation.setBackgroundResource(R.drawable.gps_activated);
                                }

                                //searchingGPS.setVisibility(View.GONE);
                            }


                            setIdentification(contents, origin, destination);
                        }catch(Exception e){
                            Toast.makeText(Map_Activity.this, "Resident has no location", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Map_Activity.this, "Not assigned yet", Toast.LENGTH_SHORT).show();
                    }


                }
            }else if(ThisUserType.equals(resident_str)){

                if(iHaveRescuer&&button.equals("navigationButton")){

                    ArrayList<String> contents = new ArrayList<>();
                    contents.add(rescuerForThisUser[0].toString());//name
                    contents.add(rescuerForThisUser[3].toString());//address
                    contents.add(rescuerForThisUser[4].toString());//contact


                    double destinationLat = (double) rescuerForThisUser[1];
                    double destinationLng = (double) rescuerForThisUser[2];

                    Point origin = Point.fromLngLat(currentLocation.getLongitude(), currentLocation.getLatitude());
                    Point destination = Point.fromLngLat(destinationLng, destinationLat);

                    if(ThisUserType.equals(rescuer_str))
                    {
                        navigationBtn.setBackgroundResource(R.drawable.direction_onrescuer);
                        ShowMyLocation.setBackgroundResource(R.drawable.gps_activated_rescuer);
                    }else{
                        navigationBtn.setBackgroundResource(R.drawable.direction_on);
                        ShowMyLocation.setBackgroundResource(R.drawable.gps_activated);
                    }

                    searchingGPS.setVisibility(View.GONE);

                    //DoneBtn.setVisibility(View.VISIBLE);

                    setIdentification(contents,origin,destination);
                }
            }

        }
        /*else if (GPSisOn) {//if still no location, display this

            barangayRef.child(Username).child("needRescue").setValue("yes");
            //LatLng.setText("Please do not leave the app until we find your location.");
            //LatLng.setVisibility(View.VISIBLE);
            //Toast.makeText(Map_Activity.this,"Do not leave the app until we find your location.",Toast.LENGTH_LONG).show();
            if (ThisUserType.equals(rescuer_str)) {
                getResidentLocationForRescuer(Username);

                if(!rescuerIsAvailable&&button.equals("navigationButton")){
                    ArrayList<String> contents = new ArrayList<>();
                    contents.add(residentToBeRescue.get(0).getStringProperty(name_str));
                    contents.add(residentToBeRescue.get(0).getStringProperty("address"));
                    contents.add(residentToBeRescue.get(0).getStringProperty("cellphoneNumber"));

                    double destinationLat = (double) residentToBeRescue.get(0).getNumberProperty("Lat");
                    double destinationLng = (double) residentToBeRescue.get(0).getNumberProperty("Lng");

                    Point origin = Point.fromLngLat(currentLocation.getLongitude(), currentLocation.getLatitude());
                    Point destination = Point.fromLngLat(destinationLng, destinationLat);

                    navigationBtn.setBackgroundResource(R.drawable.direction_on);
                    ShowMyLocation.setBackgroundResource(R.drawable.gps_activated);
                    DoneBtn.setVisibility(View.VISIBLE);
                    setIdentification(contents,origin,destination);
                }
            }


        }*/

        showComponentLocationClicked = true;
        zoomWhileTracking();
        displayMyLocation();

    }



//MENU BUTTON
    public void setMenu(){
        menu.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(Map_Activity.this,v, Gravity.RIGHT);
                popupMenu.setOnMenuItemClickListener(Map_Activity.this);
                popupMenu.inflate(R.menu.popup_menu);


                popupMenu.show();
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()){
            case R.id.Logout:

                barangayRef.child(Username).child("needRescue").setValue("no");

                //delete to sqlite
                DBHelper db = new DBHelper(this);
                boolean deleted = db.delete(Username);

                if(deleted){
                    /*
                    //Toast.makeText(this,"Logout",Toast.LENGTH_LONG).show();
                    if(ThisUserType.equals(rescuer_str)) {

                        try {barangayRef.removeEventListener(assigedtoLsnr);} catch (Exception e) {}
                        barangayRef.child(Username).child("assignRequest").removeEventListener(assignReq);
                        barangayRef.removeEventListener(onGoingRescuer);
                    }


                    fmdRef.removeEventListener(fmdLisnr);
                    barangayRef.removeEventListener(needrescueLsnr);
                    barangayRef.removeEventListener(newChildLsnr);

                    try {barangayRef.removeEventListener(latLsnr);} catch (Exception e) {}
                    if(ThisUserType.equals(resident_str)) {
                        barangayRef.removeEventListener(residentmyrescuerLsnr);
                        barangayRef.removeEventListener(myrescuerLsnr);
                    }

                     */


                    Intent intent = new Intent(Map_Activity.this,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    System.exit(0);
                }else{
                    //Toast.makeText(this,"failed",Toast.LENGTH_LONG).show();
                }



                return true;
            case R.id.Profile:
                profileDialogBox.show();
                return true;
            default:
                return false;
        }
    }

    //__________________________________________________________________________________________________



    //-----On Map Drag------------------------------------------------------------------------------
    public void onMapDrag() {


        mapboxMap.addOnMoveListener(new MapboxMap.OnMoveListener() {

            @Override
            public void onMoveBegin(@NonNull MoveGestureDetector detector) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onMove(MoveGestureDetector detector) {

                ShowMyLocation.setBackgroundResource(R.drawable.gps_inactive);
                //mapboxMap.easeCamera(CameraUpdateFactory.tiltTo(0),2000);

            }

            @Override
            public void onMoveEnd(MoveGestureDetector detector) {
            }
        });
    }
    //----------------------------------------------------------------------------------------------


    //-----Zoom While Tracking----------------------------------------------------------------------
    public void zoomWhileTracking() {
        try {

            locationComponent.setCameraMode(CameraMode.TRACKING_COMPASS, new OnLocationCameraTransitionListener() {
                @Override
                public void onLocationCameraTransitionFinished(@CameraMode.Mode int cameraMode) {
                    locationComponent.zoomWhileTracking(18, 2500, new MapboxMap.CancelableCallback() {
                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onFinish() {
                            locationComponent.tiltWhileTracking(60);

                        }
                    });
                }

                @Override
                public void onLocationCameraTransitionCanceled(@CameraMode.Mode int cameraMode) {
                }
            });
        } catch (Exception e) {
        }

    }
    //----------------------------------------------------------------------------------------------




    /*
    //-----This will identify if the display location is accurate-----------------------------------
    int finding_location_timer = 0;
    public void findAccurateLocation(int Seconds) {

        findingAccuracy_IsRunning = true;

        new CountDownTimer(Seconds * 1000, 1000) {
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                //ShowMyLocation.setText(counter+" -<  >- " + millisUntilFinished / 1000);
            }

            @SuppressLint("SetTextI18n")
            public void onFinish() {
                if (gps_counter < 2) {
                    gps_counter = 0;
                    //LatLng.setText("We can't find your location, please find a strong signal.");

                    if(finding_location_timer==2) {
                        Toast.makeText(Map_Activity.this, "We can't find your location, please find a strong signal.", Toast.LENGTH_LONG).show();
                    }
                    finding_location_timer++;
                    findAccurateLocation(5);
                } else {
                    //ShowMyLocation.setText(counter+"");
                    Toast.makeText(Map_Activity.this,"Your current location has been sent.",Toast.LENGTH_LONG).show();
                    ShowMyLocation.setBackgroundResource(R.drawable.gps_tracked_new_ui);
                    locationIsAccurate = true;
                    findingAccuracy_IsRunning = false;

                }
            }
        }.start();

    }
    //----------------------------------------------------------------------------------------------

 */


    //-----PROGRESS BAR-----------------------------------------------------------------------------
    private void startProgress() {

        // Start and show the progress bar

        isEndNotified = false;
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        LoadingTV.setVisibility(View.VISIBLE);
    }

    //Show progress by percentage
    @SuppressLint("SetTextI18n")
    private void setPercentage(final int percentage) {
        progressBar.setIndeterminate(false);
        progressBar.setProgress(percentage);
        LoadingTV.setText("Loading (" + percentage + "%)");
        if (percentage == 70) {
            startMapAnimation();
        }
    }

    public void startMapAnimation() {
        // Camera animation after loading map
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(14.848204, 120.904121)) // Sets the new camera position
                .zoom(18) // Sets the zoom
                .bearing(55) // Rotate the camera
                .tilt(60) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder

        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 10000);
    }

    private void endProgress(final String message) {
        // Don't notify more than once
        if (isEndNotified) {
            //LatLng.setVisibility(View.VISIBLE);
            ShowMyLocation.setVisibility(View.VISIBLE);
            return;
        }

        // Stop and hide the progress bar
        isEndNotified = true;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);
        LoadingTV.setVisibility(View.GONE);

    }
    //----------------------------------------------------------------------------------------------





    //------DIALOG AREA-----------------------------------------------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setProfileDialogbox(String name, String address, String contact){
        profileDialogBox = new Dialog(Map_Activity.this);
        profileDialogBox.setContentView(R.layout.profile_dialog_box);
        profileDialogBox.getWindow().setBackgroundDrawable(getDrawable(R.drawable.clear_backgroung));
        profileDialogBox.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        profileDialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        profileDialogBox.setCancelable(false);

        exitBtn = profileDialogBox.findViewById(R.id.exitBtn);
        profile_lbl = profileDialogBox.findViewById(R.id.profile_lbl);
        profile_name = profileDialogBox.findViewById(R.id.profile_name);
        profile_contact = profileDialogBox.findViewById(R.id.profile_contact);
        profile_location = profileDialogBox.findViewById(R.id.profile_location);
        user_icon = profileDialogBox.findViewById(R.id.user_icon);
        contact_icon = profileDialogBox.findViewById(R.id.contact_icon);
        location_icon = profileDialogBox.findViewById(R.id.location_icon);
        profile_name.setText(name);
        profile_contact.setText(contact);
        profile_location.setText(address);


        if(ThisUserType.equals(rescuer_str)){
            exitBtn.setTextColor(Color.parseColor(("#f3ac5b")));
            profile_lbl.setTextColor(Color.parseColor(("#f3ac5b")));
            user_icon.setBackgroundResource(R.drawable.user_rescuer);
            contact_icon.setBackgroundResource(R.drawable.contact_icon_rescuer);
            location_icon.setBackgroundResource(R.drawable.small_marker_icon_rescuer);
        }



        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileDialogBox.dismiss();
            }
        });
    }

    String assignRequestName;
    public void assignRequest(){
        assignReq = barangayRef.child(Username+"/assignRequest").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                assignRequestName = snapshot.getValue(String.class);
                if(!assignRequestName.equals("No request")){

                    barangayRef.child(assignRequestName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String current_location = snapshot.child("currentLocation").getValue(String.class);
                            String address = snapshot.child("address").getValue(String.class);
                            if(!current_location.equals("No current location")){
                                serverRequestMsg.setText("There is a request from server to rescue "+assignRequestName+" on "+current_location+".");
                                serverReqDialog.show();
                            }else{
                                serverRequestMsg.setText("There is a request from server to rescue "+assignRequestName+" on "+address+".");
                                serverReqDialog.show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    //serverRequestDialogBox(assignRequestName);


                }else{
                    serverReqDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setdoneBtnDialogBox(){

        doneBtnDialogBox = new Dialog(Map_Activity.this);
        doneBtnDialogBox.setContentView(R.layout.donebtn_dialog_box);
        doneBtnDialogBox.getWindow().setBackgroundDrawable(getDrawable(R.drawable.clear_backgroung));
        doneBtnDialogBox.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        doneBtnDialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        doneBtnDialogBox.setCancelable(false);

        done_yesBtn= doneBtnDialogBox.findViewById(R.id.done_yesBtn);
        done_noBtn = doneBtnDialogBox.findViewById(R.id.done_noBtn);



        done_yesBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (ThisUserType.equals(rescuer_str)) {
                    addRescueCount();

                }
                doneBtnDialogBox.dismiss();
            }

        });


        done_noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneBtnDialogBox.dismiss();
            }
        });
    }

    public void addRescueCount(){

        barangayRef.child(Username).child("rescueCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int rescueCount = snapshot.getValue(Integer.class);

                barangayRef.child(Username).child("rescueCount").setValue(rescueCount+1);

                //barangayRef.child(Username).child(assignedTo_str).setValue("Not assigned yet");

                try {
                    navigationMapRoute.updateRouteVisibilityTo(false);
                }catch (Exception e){
                    //Toast.makeText(Map_Activity.this,e+"",Toast.LENGTH_LONG).show();
                }

                //DoneBtn.setVisibility(View.GONE);
                barangayRef.child(residentToBeRescue[0].toString()).child("myRescuer").setValue(no_rescuer_assigned);
                barangayRef.child(residentToBeRescue[0].toString()).child("needRescue").setValue("no");
                barangayRef.child(residentToBeRescue[0].toString()).child("lat").setValue("");
                barangayRef.child(residentToBeRescue[0].toString()).child("lng").setValue("");
                barangayRef.child(Username+"/assignedTo").setValue("Not assigned yet");
                rescuerIsAvailable = true;
                identification.setVisibility(View.GONE);
                address_container.setVisibility(View.GONE);
                onTheWay.setVisibility(View.GONE);
                navigationBtn.setBackgroundResource(R.drawable.get_my_direction);

                residentToBeRescue[0] = null;
                residentToBeRescue[1] = null;
                residentToBeRescue[2] = null;
                residentToBeRescue[3] = null;
                residentToBeRescue[4] = null;
                residentToBeRescue[5] = null;

                residentSymbol();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void serverRequestDialogBox(){

        serverReqDialog = new Dialog(Map_Activity.this);
        serverReqDialog.setContentView(R.layout.server_req_dialog_box);
        serverReqDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.clear_backgroung));
        serverReqDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        serverReqDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        serverReqDialog.setCancelable(false);

        serverRequestMsg = serverReqDialog.findViewById(R.id.serverRequestMsg);
        acceptBtn= serverReqDialog.findViewById(R.id.acceptBtn);

        declineBtn = serverReqDialog.findViewById(R.id.exitBtn);




        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barangayRef.child(assignRequestName+"/myRescuer").setValue(Username);
                barangayRef.child(Username+"/assignRequest").setValue("No request");
                barangayRef.child(Username+"/assignedTo").setValue(assignRequestName);
                serverReqDialog.dismiss();

                //Toast.makeText(Map_Activity.this, "Accepted", Toast.LENGTH_SHORT).show();
            }
        });

        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverReqDialog.dismiss();
                barangayRef.child(Username+"/assignRequest").setValue("No request");
                //Toast.makeText(Map_Activity.this, "Declined", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setsosDialogBox(){

        sosDialogBox = new Dialog(Map_Activity.this);
        sosDialogBox.setContentView(R.layout.sos_dialog_box);
        sosDialogBox.getWindow().setBackgroundDrawable(getDrawable(R.drawable.clear_backgroung));
        sosDialogBox.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        sosDialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        sosDialogBox.setCancelable(true);

        yesBtn= sosDialogBox.findViewById(R.id.yesBtn);
        noBtn = sosDialogBox.findViewById(R.id.noBtn);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sosDialogBox.dismiss();
                sosBtn.setBackgroundResource(R.drawable.canclebtn);
                gpsFunctions("sos");
                barangayRef.child(Username).child("needRescue").setValue("yes");
                sosReqOngoing = true;
                //Toast.makeText(Map_Activity.this, "Yes", Toast.LENGTH_SHORT).show();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sosDialogBox.dismiss();
                /*sosBtn.setBackgroundResource(R.drawable.canclebtn);
                sendingSOSThroughSMS = true;
                gpsFunctions("sos");
                sosReqOngoing = true;
                checkMsgPermission();
                */
                //Toast.makeText(Map_Activity.this, "No", Toast.LENGTH_SHORT).show();
            }
        });
        /*
        sosDialogBox = new Dialog(Map_Activity.this);
        sosDialogBox.setContentView(R.layout.sos_dialog_box);
        sosDialogBox.getWindow().setBackgroundDrawable(getDrawable(R.drawable.clear_backgroung));
        sosDialogBox.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        sosDialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        sosDialogBox.setCancelable(false);

        yesBtn= sosDialogBox.findViewById(R.id.yesBtn);
        noBtn = sosDialogBox.findViewById(R.id.noBtn);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sosDialogBox.dismiss();
                sosBtn.setBackgroundResource(R.drawable.canclebtn);
                gpsFunctions("sos");
                barangayRef.child(Username).child("needRescue").setValue("yes");
                sosReqOngoing = true;
                //Toast.makeText(Map_Activity.this, "Yes", Toast.LENGTH_SHORT).show();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sosDialogBox.dismiss();

                //Toast.makeText(Map_Activity.this, "No", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setcancleSosDialogBox(){

        cancleSosDialogBox = new Dialog(Map_Activity.this);
        cancleSosDialogBox.setContentView(R.layout.cancle_sos_dialog_box);
        cancleSosDialogBox.getWindow().setBackgroundDrawable(getDrawable(R.drawable.clear_backgroung));
        cancleSosDialogBox.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        cancleSosDialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        cancleSosDialogBox.setCancelable(false);

        yes_cancleBtn= cancleSosDialogBox.findViewById(R.id.yes_cancleBtn);
        no_cancleBtn = cancleSosDialogBox.findViewById(R.id.no_cancleBtn);

        yes_cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancleSosDialogBox.dismiss();
                //sosBtn.setBackgroundResource(R.drawable.canclebtn);
                sosBtn.setBackgroundResource(R.drawable.sosbtn);

                barangayRef.child(Username).child("needRescue").setValue("no");
                // gpsFunctions("sos");

                sosReqOngoing = false;
                //Toast.makeText(Map_Activity.this, "Yes", Toast.LENGTH_SHORT).show();
            }
        });

        no_cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancleSosDialogBox.dismiss();

                //Toast.makeText(Map_Activity.this, "No", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //----------------------------------------------------------------------------------------------




    // -----Offline Map State-----------------------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        activityStop = true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);

        }
        mapView.onDestroy();

    }
    //----------------------------------------------------------------------------------------------

    public class ExitService extends Service {

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onTaskRemoved(Intent rootIntent) {
            System.out.println("onTaskRemoved called");
            barangayRef.child("Resident1").child("needRescue").setValue("no");
            super.onTaskRemoved(rootIntent);
            //do something you want before app closes.
            //stop service
            this.stopSelf();
        }
    }


    //-----This will avoid restarting activity when orientation change------------------------------
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void handlingOrientation() {

        FragmentManager fm = getFragmentManager();
        mTaskFragment = fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mTaskFragment == null) {
            mTaskFragment = new Fragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }

    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onProgressUpdate(int percent) {
    }

    @Override
    public void onCancelled() {
    }

    @Override
    public void onPostExecute() {
    }
    //----------------------------------------------------------------------------------------------


/*
    List<Symbol> sym = new ArrayList<>();
    List<SymbolOptions> symOp = new ArrayList<>();
    List<SymbolManager>
    public void symbolTest(double lat, double lng){
            // Set up a SymbolManager instance

            MapStyle.addImage(ICON_ID, BitmapFactory.decodeResource(MainActivity.this.getResources(), R.drawable.mapbox_marker_icon_default));

            symbolManager = new SymbolManager(mapView, mapboxMap, MapStyle);
            symbolManager.setIconAllowOverlap(true);
            symbolManager.setTextAllowOverlap(true);
            symOp.add(new SymbolOptions());

// Add symbol at specified lat/lon
        sym.add(symbolManager.create(symOp.get(symbolCount)
                .withLatLng(new LatLng(lat, lng))
                .withIconImage(ICON_ID)
                .withIconSize(1.0f).withDraggable(true)));

        sym.get(symbolCount).setTextJustify("Resident "+(symbolCount+1));
        symbolCount++;

// Add click listener and change the symbol to a cafe icon on click
            symbolManager.addClickListener(new OnSymbolClickListener() {
                @Override
                public boolean onAnnotationClick(Symbol symbol) {
                    return true;
                }
            });

// Add long click listener and change the symbol to an airport icon on long click
            symbolManager.addLongClickListener((new OnSymbolLongClickListener() {
                @Override
                public boolean onAnnotationLongClick(Symbol symbol) {

                    symbol.setLatLng(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));


                    //Toast.makeText(MainActivity.this,"Long Clicked", Toast.LENGTH_SHORT).show();

                    sym.get(0).setGeometry(Point.fromLngLat(currentLocation.getLongitude(),currentLocation.getLatitude()));
                    sym.get(0).setLatLng(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
                    ShowMyLocation.setText(sym.get(0).getLatLng().toString());

                    symbolManager.update(sym);
                    return true;
                }
            }));
    }
    //----------------------------------------------------------------------------------------------
    */


    /*
    public void setUpMarker(){
        symbolLayerIconFeatureList = new ArrayList<>();

        symbolLayerIconFeatureList.add(Feature.fromGeometry(
                Point.fromLngLat(120.905121, 14.848204)));
        symbolLayerIconFeatureList.add(Feature.fromGeometry(
                Point.fromLngLat(120.906121, 14.849204)));


        MapStyle.addImage(ICON_ID, BitmapFactory.decodeResource(MainActivity.this.getResources(), R.drawable.mapbox_marker_icon_default));


        MapStyle.addSource(new GeoJsonSource(SOURCE_ID,
                FeatureCollection.fromFeatures(symbolLayerIconFeatureList)));

        MapStyle.addLayer(new SymbolLayer(LAYER_ID,SOURCE_ID)
                .withProperties(iconImage(ICON_ID), iconAllowOverlap(true), iconIgnorePlacement(true)));

    }*/

    /*
    -----Download Map Without Displaying------------------------------------------------------------

    public void downloadMap(){

                // Define region of map tiles
                String styleUrl = "mapbox://styles/mapbox/streets-v11";
                double minZoom = 12, maxZoom = 15;

                OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                        styleUrl,
                        new LatLngBounds.Builder()
                                .include(new LatLng(LatNorth, LngEast)) // Northeast
                                .include(new LatLng(LatSouth,  LngWest)) // Southwest
                                .build(),
                        minZoom,
                        maxZoom,
                        getResources().getDisplayMetrics().density
                );


                // Customize the download notification's appearance
                NotificationOptions notificationOptions = NotificationOptions.builder(MainActivity.this)
                        .smallIconRes(R.drawable.mapbox_compass_icon)
                        .returnActivity(MainActivity.class.getName())
                        .build();



                // Start downloading the map tiles for offline use
                OfflinePlugin.getInstance(MainActivity.this).startDownload(
                        OfflineDownloadOptions.builder()
                                .definition(definition)
                                .metadata(OfflineUtils.convertRegionName(RegionName))
                                .notificationOptions(notificationOptions)
                                .build()
                );

                OfflineMapDisplay(dark);
         }
         */


    //-----CHECK CONNECTION-------------------------------------------------------------------------
    public void checkIfFirebaseIsConnected(){

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean connected = snapshot.getValue(Boolean.class);

                if (connected) {
                    if(ThisUserType.equals(rescuer_str))
                    {
                        connectionStatus.setBackgroundResource(R.drawable.internet_connected_rescuer);
                    }else{
                        connectionStatus.setBackgroundResource(R.drawable.internet_connected);
                    }
                } else {


                    if(ThisUserType.equals(rescuer_str))
                    {
                        connectionStatus.setBackgroundResource(R.drawable.internet_disconnected__rescuer);
                    }else{
                        connectionStatus.setBackgroundResource(R.drawable.internet_disconnected);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //----------------------------------------------------------------------------------------------

    private long backPressedTime = 0L;
    @Override
    public void onBackPressed() {

        if((backPressedTime+2000) > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }else{
            Toast.makeText(Map_Activity.this,"Press again to exit",Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();

/*
        if((backPressedTime+2000) > System.currentTimeMillis()){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("Exit",true);
            startActivity(intent);

            finish();
            System.exit(0);
            return;
        }else{
            Toast.makeText(Map_Activity.this,"Press again to exit",Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Exit",true);
        startActivity(intent);

        finish();
        System.exit(0);

 */

    }
}