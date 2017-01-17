package edu.upc.eetac.dsa.pokeetacgo;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;
import edu.upc.eetac.dsa.pokeetacgo.entity.Capturado;
import edu.upc.eetac.dsa.pokeetacgo.entity.LocationMarker;
import edu.upc.eetac.dsa.pokeetacgo.entity.User;
import edu.upc.eetac.dsa.pokeetacgo.entity.serviceLibraryResults.ProfemonLocationResult;
import edu.upc.eetac.dsa.pokeetacgo.entity.serviceLibraryResults.ScannedRouterResult;
import edu.upc.eetac.dsa.pokeetacgo.entity.serviceLibraryResults.UserFloorResult;
import edu.upc.eetac.dsa.pokeetacgo.entity.serviceLibraryResults.UserLevelResult;
import edu.upc.eetac.dsa.pokeetacgo.serviceLibrary.PokEETACRestClient;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<Status> {
    final String TAG = "MAPACT";
    private GoogleMap mMap;
    PokEETACGo pokEETACGo = PokEETACGo.getInstance();
    private GoogleApiClient mGoogleApiClient;
    PokEETACGoBusiness pokEETACGoBusiness = new PokEETACGoBusiness();
    LocationRequest mLocationRequest;
    List<Geofence> mGeofenceList;
    PendingIntent mGeofencePendingIntent;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    TextView floor;
    TextView level;
    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();
    WifiManager wifi;
    List<ScanResult> results;
    List<ScannedRouterResult> wifis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        pokEETACGoBusiness.init(getApplicationContext());
        createLocationRequest();
        mGeofenceList = new ArrayList<>();
        floor = (TextView) findViewById(R.id.floor);
        level = (TextView) findViewById(R.id.level);
        setUsername();
        setUserLevel();

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        setMyLocationEnabled();
        startLocationUpdates();
        if (pokEETACGo.profemonLocationMarkers.size() == 0) {
            setProfemonLocationMarkers();
//            updateFloor();
        }
        //startTimer();
    }

    private void setProfemonLocationMarkers() {
        PokEETACRestClient.get("/profemon/location/all", null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "Error getting random profemon locations!");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.i(TAG, "Success getting random profemon locations: " + responseString);
                Type listType = new TypeToken<ArrayList<ProfemonLocationResult>>() {}.getType();
                List<ProfemonLocationResult> profemons = new Gson().fromJson(responseString, listType);
                for (ProfemonLocationResult profemonLocation : profemons) {
                    Marker markerAdded = addMarkerOnMap(profemonLocation);
                    pokEETACGo.profemonLocationMarkers.put(profemonLocation.locationId, new LocationMarker(profemonLocation, markerAdded));
                }
                updateMarkersAndGeofences(0);
            }
        });
    }

    //private void updateFloor() {
        /*List<ScannedRouterResult> scannedRouters = getScannedRouters();
        PokEETACRestClient.post(this, "/user/location/floor", PokEETACRestClient.getObjectAsStringEntity(scannedRouters), "application/json", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "Error getting the floor location of the user");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.i(TAG, "Success getting the floor location of the user: " + responseString);
                UserFloorResult userFloorResult = new Gson().fromJson(responseString, UserFloorResult.class);
                if (userFloorResult.floor != Integer.parseInt(floor.getText().toString())) {
                    floor.setText(userFloorResult.floor);
                    //setProfemonMarkerIconsOnMap();
                }
            }
        });*/
    //}

    private void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 0, 15000);
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        //updateFloor();
                    }
                });
            }
        };
    }

    public void updateFloor(View view) {
        if (floor.getText().toString().equals("0 floor")) {
            floor.setText("1 floor");
            hideMarkersAndRemoveGeofences();
            updateMarkersAndGeofences(1);
        } else {
            floor.setText("0 floor");
            hideMarkersAndRemoveGeofences();
            updateMarkersAndGeofences(0);
        }
    }

    private void hideMarkersAndRemoveGeofences() {
        List<String> requestIdOfGeofencesToRemove = new ArrayList<>();

        for(int i = 0; i < pokEETACGo.profemonLocationMarkers.size(); i++) {
            pokEETACGo.profemonLocationMarkers.get(pokEETACGo.profemonLocationMarkers.keyAt(i)).getMarker().setVisible(false);
            requestIdOfGeofencesToRemove.add(String.valueOf(pokEETACGo.profemonLocationMarkers.get(pokEETACGo.profemonLocationMarkers.keyAt(i)).getProfemonLocationResult().locationId));
        }

        mGeofenceList.clear();
        try{
            LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, requestIdOfGeofencesToRemove);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void updateMarkersAndGeofences(int floor) {
        List<LocationMarker> locationMarkersOfCurrentFloor = pokEETACGoBusiness.getLocationMarkersOfCurrentFloor(floor, pokEETACGo.profemonLocationMarkers);
        for(LocationMarker locationMarker : locationMarkersOfCurrentFloor) {
            locationMarker.getMarker().setVisible(true);
            addGeofenceToGeofenceList(locationMarker.getProfemonLocationResult());
        }
        if(!mGeofenceList.isEmpty()) {
            addGeofences();
        }
    }

    private List<ScannedRouterResult> getScannedRouters() {
        wifi.startScan();
        results = wifi.getScanResults();
        wifis = new ArrayList<>();
        if (results != null) {
            for (int i = 0; i < results.size(); i++) {
                ScannedRouterResult routerScanned = new ScannedRouterResult();
                if (results.get(i).SSID.equals("eduroam")) {
                    routerScanned.BSSID = results.get(i).BSSID;
                    routerScanned.signalLevel = results.get(i).level;
                }
                wifis.add(routerScanned);
            }
        }
        return wifis;
    }

    protected void startLocationUpdates() {
        if (checkLocationPermission()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19f));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    private void setMyLocationEnabled() {
        if (checkLocationPermission()) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            if (getMyLatLng() != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getMyLatLng(), 19f));
            }
            Log.i(TAG, "My Location enabled");
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);
                        if (getMyLatLng() != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getMyLatLng(), 19f));
                        }
                        Log.i(TAG, "My Location enabled");
                    }
                } else {
                    Log.i(TAG, "" + ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION));
                    Log.i(TAG, "" + PackageManager.PERMISSION_GRANTED);
                    Log.e(TAG, "My Location Errors");
                }
            }
        }
    }

    private LatLng getMyLatLng() {
        if (checkLocationPermission()) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location != null) {
                return new LatLng(location.getLatitude(), location.getLongitude());
            }
        }
        return null;
    }

    private Marker addMarkerOnMap(ProfemonLocationResult profemonLocation) {
        LatLng markerLatLng = new LatLng(profemonLocation.latitude, profemonLocation.longitude);
        return mMap.addMarker(new MarkerOptions()
                .visible(false)
                .position(markerLatLng)
                .title(profemonLocation.name)
                .icon(BitmapDescriptorFactory.fromBitmap(pokEETACGoBusiness.getProfemonIcon(profemonLocation.name))));
    }

    private void addGeofenceToGeofenceList(ProfemonLocationResult profemonLocation) {
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(String.valueOf(profemonLocation.locationId))
                .setCircularRegion(profemonLocation.latitude, profemonLocation.longitude, 25)
                .setExpirationDuration(-1)
                .setLoiteringDelay(500)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL)
                .build());
    }

    private void addGeofences() {
        if (checkLocationPermission()) {
            try {
                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        getGeofencingRequest(),
                        getGeofencePendingIntent()
                );
            } catch (IllegalStateException e) {
                Log.e(TAG, "GoogleApiClient not connected yet");
                mGoogleApiClient.connect();
                addGeofences();
            }
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    private void setUsername() {
        final TextView username = (TextView) findViewById(R.id.username);

        PokEETACRestClient.get("/user/" + pokEETACGo.getCurrentUserId(), null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "Error getting user from API!");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.i(TAG, "Success getting user from API: " + responseString);
                User user = new Gson().fromJson(responseString, User.class);
                username.setText(user.getUsername());
            }
        });
    }

    private void setUserLevel() {
        PokEETACRestClient.get("/user/level/" + pokEETACGo.getCurrentUserId(), null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "Error getting user's level!");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.i(TAG, "Success getting user's level: " + responseString);
                UserLevelResult userLevelResult = new Gson().fromJson(responseString, UserLevelResult.class);
                level.setText(MessageFormat.format("Level {0}", userLevelResult.userLevel));
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle intentData = intent.getExtras();

        boolean capturadoIsSuccessful = intentData.getBoolean("capturadoIsSuccessful");
        int requestIdOfGeofenceTriggered = intentData.getInt("requestIdOfGeofenceTriggered");
        ProfemonLocationResult profemonLocationResultJustCapturado = pokEETACGo.profemonLocationMarkers.get(requestIdOfGeofenceTriggered).getProfemonLocationResult();
        postCapturado(profemonLocationResultJustCapturado, capturadoIsSuccessful);

        if (checkLocationPermission()) {
            pokEETACGo.profemonLocationMarkers.get(requestIdOfGeofenceTriggered).getMarker().remove();
            pokEETACGo.profemonLocationMarkers.delete(requestIdOfGeofenceTriggered);
            Log.i(TAG, "Geofence and Marker of location " + requestIdOfGeofenceTriggered + " deleted");
        }

        if (capturadoIsSuccessful) {
            setUserLevel();
        }
    }

    private void postCapturado(ProfemonLocationResult profemonLocationResult, boolean capturadoIsSuccessful) {
        Capturado capturado = new Capturado();
        capturado.setIdProfemon(profemonLocationResult.profemonId);
        capturado.setIdLocation(profemonLocationResult.locationId);
        capturado.setIdUser(pokEETACGo.getCurrentUserId());
        capturado.setIsSuccessful(capturadoIsSuccessful);

        PokEETACRestClient.post(this, "/capturado", PokEETACRestClient.getObjectAsStringEntity(capturado), "application/json", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "Error posting a new capturado");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.i(TAG, "Capturado added successfully");
            }
        });
    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.i(TAG, "Entered On Result");
    }
}
