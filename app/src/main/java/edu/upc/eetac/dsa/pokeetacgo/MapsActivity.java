package edu.upc.eetac.dsa.pokeetacgo;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import edu.upc.eetac.dsa.pokeetacgo.entity.LocationMarker;
import edu.upc.eetac.dsa.pokeetacgo.entity.serviceLibraryResults.ProfemonLocationResult;
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
        if(pokEETACGo.profemonLocationMarkers.size() == 0) {
            setProfemonMarkerIconsOnMap();
        }
    }

    protected void startLocationUpdates() {
        if (checkLocationPermission()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f));
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
    }

    private void setMyLocationEnabled() {
        if (checkLocationPermission()) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            if (getMyLatLng() != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getMyLatLng(), 17f));
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
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getMyLatLng(), 17f));
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

    private void setProfemonMarkerIconsOnMap() {
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
                    addGeofenceToGeofenceList(profemonLocation);
                    pokEETACGo.profemonLocationMarkers.put(profemonLocation.locationId, new LocationMarker(profemonLocation, markerAdded));
                }
                addGeofences();
            }
        });
    }

    private Marker addMarkerOnMap(ProfemonLocationResult profemonLocation) {
        LatLng markerLatLng = new LatLng(profemonLocation.latitude, profemonLocation.longitude);
        return mMap.addMarker(new MarkerOptions()
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
        if(checkLocationPermission()) {
            try {
                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        getGeofencingRequest(),
                        getGeofencePendingIntent()
                );
            } catch(IllegalStateException e) {
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle intentData = intent.getExtras();
        boolean capturadoIsSuccessful = intentData.getBoolean("capturadoIsSuccessful");
        int requestIdOfGeofenceTriggered = intentData.getInt("requestIdOfGeofenceTriggered");
        if (checkLocationPermission()) {
            pokEETACGo.profemonLocationMarkers.get(requestIdOfGeofenceTriggered).getMarker().remove();
            pokEETACGo.profemonLocationMarkers.delete(requestIdOfGeofenceTriggered);
            Log.i(TAG, "Geofence and Marker of location " + requestIdOfGeofenceTriggered + " deleted");
        }
    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.i(TAG, "Entered On Result");
    }
}
