package com.example.weareperpared;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.icu.text.RelativeDateTimeFormatter;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.config.GservicesValue;
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
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.ui.v5.route.OnRouteSelectionChangeListener;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static android.graphics.Color.*;
import static com.example.weareperpared.R.drawable.admin;
import static com.example.weareperpared.R.drawable.rescuer;
import static com.example.weareperpared.R.drawable.resident;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class Map_Activity extends AppCompatActivity implements TaskFragment.TaskCallbacks, OnMapReadyCallback, PermissionsListener {

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

    private ConstraintLayout identification,address_container;
    private ImageView connectionStatus;
    private ImageView ShowMyLocation, DoneBtn,navigationBtn;
    private TextView  LoadingTV, OpenMobileData;
    private TextView UI_NameTV, UI_AddressTv, UI_ContactTv, UI_myRescuerTv, UI_myRescuer,distanceTv,durationTv,speedTv;

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
    boolean showComponentLocationClicked = false, hide = false;
    boolean GPSisOn;
    boolean OnStart = true;
    boolean locationIsAccurate = false;

    // JSON encoding/decoding
    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";
    public static final String RegionName = "Pulong Gubat";

    double LatNorth = 14.868179, LatSouth = 14.822714, LngWest = 120.884195, LngEast = 120.927998;

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

    List<Feature> residentToBeRescue = new ArrayList<>();

    int needRescueCounter = 0, rescuerCounter = 0, adminCounter = 0;
//--------------------------------------------------------------------------------------------------


    //FIREBASE
    FirebaseDatabase firebaseDatabase;
    DatabaseReference barangayRef;
    String Username, ThisUserType;


    String selectedUserType = "", selectedName = "";
    boolean assigning = false, activityStop = false;
    double residentLat, residentLng, rescuerLat, rescuerLng;

    //BLUR
    BlurView blurView;

    //COLORS RESOURCES
    int residentColor, rescuerColor, adminColor, disableColor, warningColor;

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
        UI_myRescuerTv = findViewById(R.id.UI_myRescuerTv);
        connectionStatus = findViewById(R.id.mapConnection);
        distanceTv = findViewById(R.id.distanceTv);
        durationTv = findViewById(R.id.durationTv);
        speedTv = findViewById(R.id.speedTv);

        UI_myRescuer = findViewById(R.id.UI_myRescuer);
        DoneBtn = findViewById(R.id.DoneBtn);
        navigationBtn = findViewById(R.id.navigationBtn);

        identification = findViewById(R.id.Identification);
        address_container = findViewById(R.id.address_container);

        residentColor = ResourcesCompat.getColor(getResources(), R.color.residentColor, null);
        rescuerColor = ResourcesCompat.getColor(getResources(), R.color.rescuerColor, null);
        adminColor = ResourcesCompat.getColor(getResources(), R.color.adminColor, null);
        disableColor = ResourcesCompat.getColor(getResources(), R.color.disableColor, null);
        warningColor = ResourcesCompat.getColor(getResources(), R.color.warningColor, null);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        currentLocation = new Location(String.valueOf(new LatLng(0, 0)));
        locationOfAssignedResident_forRescuer = new Location(String.valueOf(new LatLng(0, 0)));


        //blur();
        handlingOrientation();
        showMyLocation();
        doneButton();
        navigation_btn();
        //hideUserIdentification();
        forResident();
        checkIfFirebaseIsConnected();
    }
    //______________________________________________________________________________________________




    public void forResident() {
        if (ThisUserType.equals(resident_str)) {
            barangayRef.child(Username).child("myRescuer").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String myRescuer = snapshot.getValue(String.class);
                    if (!myRescuer.equals(no_rescuer_assigned)) {
                        read_RescueMe(myRescuer);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    /*
    public void hideUserIdentification() {
        if (ThisUserType.equals(rescuer_str) || ThisUserType.equals(resident_str)) {
            identification.setVisibility(View.GONE);
        }
    }*/



    //___________________________________FIREBASE DATA READING______________________________________


    //-----Update Listener for all user location----------------------------------------------------
    public void read_RescueMe(String myRescuer) {

        //THIS WILL READ ALL NAMES OF USER IN BARANGAY
        barangayRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                //LOOP THAT WILL READ ALL NAMES IN BARANGAY
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {//Users users = dataSnapshot.getValue(Users.class);


                    //ShowMyLocation.setText(dataSnapshot.getRef().getKey());
                    if (ThisUserType.equals(rescuer_str) || ThisUserType.equals(admin_str) || (dataSnapshot.getRef().getKey().equals(myRescuer))) {


                        //READ ALL(NEED RESCUE) OF USER AND ADD EVERY CHANGE LISTENER
                        dataSnapshot.child("needRescue").getRef().addValueEventListener(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot needRescuesnapshot) {


                                //IF NEED RESCUE OF THE USE IS EQUAL TO YES THEN THIS PLAY ITS LOCATION FEATURE
                                if (needRescuesnapshot.getValue(String.class).equals("yes")) {


                                    if (!needRescuesnapshot.getRef().getParent().getKey().equals(Username)) {


                                        //Add to the users that need to rescue
                                        addNewUser(needRescuesnapshot.getRef().getParent().getKey());


                                        //SET CHANGE LISTENER FOR ALL USER THAT NEED RESCUE
                                        needRescuesnapshot.getRef().getParent().child("lat").addValueEventListener(new ValueEventListener() {
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
                residentFeatureList.get(needRescueCounter).addStringProperty(userType_str, userType);
                residentFeatureList.get(needRescueCounter).addNumberProperty("Lng", Lng);
                residentFeatureList.get(needRescueCounter).addNumberProperty("Lat", Lat);
                myRescuerListener(name);
                //onlineListener(name);
                residentSymbol();
                needRescueCounter++;
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
                        residentFeatureList.get(index).addStringProperty(userType_str, userType);
                        residentFeatureList.get(index).addNumberProperty("Lng", Lng);
                        residentFeatureList.get(index).addNumberProperty("Lat", Lat);
                        myRescuerListener(name);
                        //onlineListener(name);
                        residentSymbol();

                        if (name.equals(nameOfAssignedResident_forRescuer)) {
                            locationOfAssignedResident_forRescuer.setLatitude(Lat);
                            locationOfAssignedResident_forRescuer.setLongitude(Lng);

                            if (!rescuerIsAvailable) {
                                Point destination = Point.fromLngLat(locationOfAssignedResident_forRescuer.getLongitude(),
                                        locationOfAssignedResident_forRescuer.getLatitude());
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
                                residentFeatureList.remove(index);
                                residentSymbol();
                                needRescueCounter--;
                                break;
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

                                residentFeatureList.get(index).addStringProperty("myRescuer", myRescuer);
                                residentSymbol();
                                if (name.equals(UI_NameTV.getText().toString())) {
                                    UI_myRescuerTv.setText(myRescuer);
                                    if (ThisUserType.equals(admin_str)) {
                                        setUpDoneBtnAdmin(residentColor);
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
                                    setUpDoneBtnAdmin(rescuerColor);
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
        barangayRef.child(name).child("myRescuer").addValueEventListener(new ValueEventListener() {
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
        barangayRef.child(name).child(assignedTo_str).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                featureListScanner(name, "set_assignedTo");


                if (!snapshot.getValue(String.class).equals("Not assigned yet")) {
                    getResidentLocationForRescuer(name);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

/*
        barangayRef.child(name).child("assignedNextTo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                featureListScanner(name,"set_assignedNextTo");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }
    //----------------------------------------------------------------------------------------------

    public void getResidentLocationForRescuer(String name) {


        if (name.equals(Username)) {
            for (int index = 0; index < residentFeatureList.size(); index++) {

                try {
                    if (residentFeatureList.get(index).getStringProperty("myRescuer").equals(name) && rescuerIsAvailable) {

                        nameOfAssignedResident_forRescuer = residentFeatureList.get(index).getStringProperty(name_str);
                        rescuerIsAvailable = false;
                        //add
                        Double Lat = (Double) residentFeatureList.get(index).getNumberProperty("Lat");
                        Double Lng = (Double) residentFeatureList.get(index).getNumberProperty("Lng");

                        //get
                        String cellphoneNumber = residentFeatureList.get(index).getStringProperty("cellphoneNumber");
                        String address = residentFeatureList.get(index).getStringProperty("address");
                        String residentName = residentFeatureList.get(index).getStringProperty(name_str);

                        //resident to be rescue only
                        residentToBeRescue.add(0, Feature.fromGeometry(Point.fromLngLat(Lng, Lat)));
                        residentToBeRescue.get(0).addStringProperty(name_str, residentName);
                        residentToBeRescue.get(0).addStringProperty("cellphoneNumber", cellphoneNumber);
                        residentToBeRescue.get(0).addStringProperty("address", address);
                        residentToBeRescue.get(0).addNumberProperty("Lng", Lng);
                        residentToBeRescue.get(0).addNumberProperty("Lat", Lat);

                        locationOfAssignedResident_forRescuer.setLongitude(Lng);
                        locationOfAssignedResident_forRescuer.setLatitude(Lat);
                        break;
                    }
                } catch (Exception e) {
                }
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
                            setUpDoneBtnAdmin(rescuerColor);

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
                                DoneBtn.setEnabled(true);
                                DoneBtn.setBackgroundResource(R.drawable.done);
                                navigationBtn.setBackgroundResource(R.drawable.direction_on);
                            } else {
                                DoneBtn.setEnabled(false);
                                DoneBtn.setBackgroundResource(R.drawable.disable_done);
                                navigationBtn.setBackgroundResource(R.drawable.get_my_direction);
                            }

                        } else if (ThisUserType.equals(admin_str)) {
                            DoneBtn.setVisibility(View.VISIBLE);
                            setUpDoneBtnAdmin(residentColor);

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


                        ArrayList<String> contents = new ArrayList<>();
                        contents.add(name);
                        contents.add("Taliptip, Bulakan, Bulacan");
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


                        identification.setVisibility(View.VISIBLE);
                        UI_NameTV.setText(name);
                        UI_AddressTv.setText("Unkown");
                        UI_ContactTv.setText("Unkown");
                        DoneBtn.setVisibility(View.GONE);

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
                    Toast.makeText(Map_Activity.this,e.toString(),Toast.LENGTH_LONG).show();
                }
                ;
                return true;
            }
        });
    }
    //----------------------------------------------------------------------------------------------



    public void setIdentification(ArrayList<String> contents, Point origin, Point destination){

        address_container.setVisibility(View.VISIBLE);

        UI_NameTV.setText(contents.get(0));
        UI_AddressTv.setText(contents.get(1));
        UI_ContactTv.setText(contents.get(2));
        distanceTv.setText("-- --");
        durationTv.setText("-- --");


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
                        if(distance>1000) {
                            distanceTv.setText(String.format("%.2f", currentRoute.distance() / 1000) + " km");
                        }else{
                            int min = (int) (distance/1);
                            distanceTv.setText(min + " m");
                        }

                        if(duration>3600) {
                            int hour = (int) (duration/3600);
                            int min = (int) ((duration%3600)/60);
                            durationTv.setText(hour+" hr "+min+" min");
                        }else{
                            int min = (int) (duration/60);
                            durationTv.setText(min+" min");
                        }
                        //----------------------------------------------------------------------------------

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e(TAG, "error: " + t.getMessage());
                    }
                });


        identification.setVisibility(View.VISIBLE);
    }



    public void setUpDoneBtnAdmin(int color) {

        String myRescuer = UI_myRescuerTv.getText().toString();
        if (myRescuer.equals(no_rescuer_assigned) || myRescuer.equals("Not assigned yet")) {
            DoneBtn.setEnabled(true);
            DoneBtn.setBackgroundResource(R.drawable.done);

            navigationMapRoute.updateRouteVisibilityTo(false);
        } else {
            DoneBtn.setEnabled(false);
            DoneBtn.setBackgroundResource(R.drawable.disable_done);
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
                        navigationMapRoute.showAlternativeRoutes(true);
                        navigationMapRoute.addRoute(currentRoute);

/*
                 //----------------DISTANCE AND DURATION--------------------------------------------
                Double distance = currentRoute.distance();
                Double duration = currentRoute.duration();
                if(distance>1000) {
                    distanceTv.setText(String.format("%.2f", currentRoute.distance() / 1000) + " km");
                }else{
                    int min = (int) (distance/1);
                    distanceTv.setText(min + " m");
                }

                if(duration>3600) {
                    int hour = (int) (duration/3600);
                    int min = (int) ((duration%3600)/60);
                    durationTv.setText(hour+" hr "+min+" min");
                }else{
                    int min = (int) (duration/60);
                    durationTv.setText(min+" min");
                }
                //----------------------------------------------------------------------------------

 */

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

        //SYMBOL COORDINATES SAMPLE
        warningFeatureList.add(Feature.fromGeometry(
                Point.fromLngLat(120.912121, 14.8230204)));
        warningFeatureList.get(0).addStringProperty(name_str, "Flood Warning");
        warningFeatureList.get(0).addNumberProperty("Lat",14.8230204);
        warningFeatureList.get(0).addNumberProperty("Lng",120.912121);
        //reset geojson MapStyle source
        warningMapSource.setGeoJson(FeatureCollection.fromFeatures(warningFeatureList));


        //reset map layer
        MapStyle.removeLayer(warningSymbolLayer);
        MapStyle.addLayer(warningSymbolLayer);


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
                        resident)).withSource(residentMapSource).withLayer(residentSymbolLayer), new Style.OnStyleLoaded() {

                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        OpenMobileData.setVisibility(View.GONE);

                        MapStyle = style;


                        MapStyle.addImage(RESCUER_ICON_ID, BitmapFactory.decodeResource(Map_Activity.this.getResources(), R.drawable.rescuer));
                        MapStyle.addSource(rescuerMapSource);
                        MapStyle.addLayer(rescuerSymbolLayer);
                        MapStyle.addImage(WARNING_ICON_ID, BitmapFactory.decodeResource(Map_Activity.this.getResources(), R.drawable.stop__1_));
                        MapStyle.addSource(warningMapSource);
                        MapStyle.addLayer(warningSymbolLayer);
                        MapStyle.addImage(ADMIN_ICON_ID, BitmapFactory.decodeResource(Map_Activity.this.getResources(), R.drawable.admin));
                        MapStyle.addSource(adminMapSource);
                        MapStyle.addLayer(adminSymbolLayer);

                        enableLocationComponent(style);
                        mapClickListener();
                        residentSymbol();
                        rescuerSymbol();
                        warningSymbol();
                        adminSymbol();


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

        read_RescueMe("");

    }
    //----------------------------------------------------------------------------------------------



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
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);


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
         * @param result the LocationEngineResult object which has the last known location within it.
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
                        if (result.getLocations().get(0).getAccuracy() < 10) {

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

                                if (!rescuerIsAvailable) {

                                    Point destination = Point.fromLngLat(locationOfAssignedResident_forRescuer.getLongitude(),
                                            locationOfAssignedResident_forRescuer.getLatitude());
                                    Point origin = Point.fromLngLat(result_lng,
                                            result_lat);

                                    getRoute(origin, destination);
                                } else {
                                    getResidentLocationForRescuer(Username);
                                }

                                Toast.makeText(Map_Activity.this, "Location has been sent", Toast.LENGTH_LONG).show();
                                currentLocation = result.getLocations().get(0);
                                locationIsAccurate = true;
                                ShowMyLocation.setBackgroundResource(R.drawable.gps_activated);
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

                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
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

                if (ThisUserType.equals(rescuer_str)) {
                    barangayRef.child(Username).child(assignedTo_str).setValue("Not assigned yet");
                    navigationMapRoute.updateRouteVisibilityTo(false);
                    DoneBtn.setVisibility(View.GONE);
                    barangayRef.child(nameOfAssignedResident_forRescuer).child("myRescuer").setValue(no_rescuer_assigned);
                    barangayRef.child(nameOfAssignedResident_forRescuer).child("needRescue").setValue("no");
                    rescuerIsAvailable = true;
                    identification.setVisibility(View.GONE);


                } else if (ThisUserType.equals(admin_str)) {
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

                }
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

                navigationBtn.setBackgroundResource(R.drawable.direction_on);
                address_container.setVisibility(View.VISIBLE);
                identification.setVisibility(View.VISIBLE);
                gpsFunctions("navigationButton");

            }
        });
    }

    public void gpsFunctions(String button){

        //This will check if gps is on, if not request to enable gps
        RequestToEnableGPS();

        if (locationIsAccurate && GPSisOn) {
            barangayRef.child(Username).child("needRescue").setValue("yes");
            ShowMyLocation.setBackgroundResource(R.drawable.gps_activated);

            if (ThisUserType.equals(rescuer_str)) {



                getResidentLocationForRescuer(Username);//this only work one time
                if(!rescuerIsAvailable&&button.equals("navigationButton")){
                    ArrayList<String> contents = new ArrayList<>();
                    contents.add(residentToBeRescue.get(0).getStringProperty(name_str));
                    contents.add(residentToBeRescue.get(0).getStringProperty("address"));
                    contents.add(residentToBeRescue.get(0).getStringProperty("cellphoneNumber"));

                    double destinationLat = (double) residentToBeRescue.get(0).getNumberProperty("Lat");
                    double destinationLng = (double) residentToBeRescue.get(0).getNumberProperty("Lng");

                    Point origin = Point.fromLngLat(currentLocation.getLongitude(), currentLocation.getLatitude());
                    Point destination = Point.fromLngLat(destinationLng, destinationLat);

                    setIdentification(contents,origin,destination);
                }
            }

        } else if (GPSisOn) {//if still no location, display this

            barangayRef.child(Username).child("needRescue").setValue("yes");
            //LatLng.setText("Please do not leave the app until we find your location.");
            //LatLng.setVisibility(View.VISIBLE);
            Toast.makeText(Map_Activity.this,"Do not leave the app until we find your location.",Toast.LENGTH_LONG).show();
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

                    setIdentification(contents,origin,destination);
                }
            }


        }

        showComponentLocationClicked = true;
        zoomWhileTracking();
        displayMyLocation();

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
                .newCameraPosition(position), 15000);
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
                    connectionStatus.setBackgroundResource(R.drawable.internet_connected);
                } else {
                    connectionStatus.setBackgroundResource(R.drawable.internet_disconnected);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //----------------------------------------------------------------------------------------------
}