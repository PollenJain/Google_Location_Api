package com.example.paragjai.googlelocationapi;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    TextView latitude;
    TextView longitude;
    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    GoogleApiClient.Builder builder;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    private Double myLatitude;
    private Double myLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitude = (TextView) findViewById(R.id.tvLatitude);
        longitude = (TextView) findViewById(R.id.tvLongitude);

        builder = new GoogleApiClient.Builder(this);
        builder = builder.addApi(LocationServices.API);
        builder = builder.addConnectionCallbacks(this);
        builder = builder.addOnConnectionFailedListener(this);

        googleApiClient = builder.build();

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(60 * 1000);
        locationRequest.setFastestInterval(15* 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("onConnected called", "calling requestLocationUpdates");
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Log.d("requestLocationUpdates", "permissions not granted yet");
            Log.d("permission not granted", "reason why onLocationChange is not getting called");
            Log.d("permission not granted", "go to settings and allow Location permission for this particular app");
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (LocationListener) this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d("onConnectionFailed", "cause : " + connectionResult);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();

        latitude.setText("Latitude is : " + String.valueOf(myLatitude));
        longitude.setText("Longitude is : " + String.valueOf(myLongitude));
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d("MainActivity", "onStart called");
        googleApiClient.connect();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d("MainActivity","onResume");
        if(googleApiClient.isConnected())
        {
            Log.d("yes connected", "now calling requestLocationUpdates from onResume");
            requestLocationUpdates();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (LocationListener) this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        googleApiClient.disconnect();
    }

}
