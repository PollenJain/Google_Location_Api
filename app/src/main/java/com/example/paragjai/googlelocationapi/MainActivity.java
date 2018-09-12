package com.example.paragjai.googlelocationapi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    TextView latitude;
    TextView longitude;
    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    GoogleApiClient.Builder builder;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    private Double myLatitude;
    private Double myLongitude;
    private int counter = 0;
    private static final int MY_PERMISSION_REQUEST_LOCATION_PERMISSION = 1;
    private Location myLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d("MainActivity", "onCreate called");
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
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("MainActivity", "onConnected called : calling requestForPermission explicitly");
        requestForPermission();
    }

    private void requestForPermission() {

        /*ActivityCompat.checkSelfPermission is called to check if you have a permission*/
        /*If you have the permission, the method returns PackageManager.PERMISSION_GRANTED,
        if the app does not have the permission, the method returns PackageManager.PERMISSION_DENIED
         */
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
        /* This condition is  true means the app doesn't have the permission.
           There could be 2 reasons for that :
           1. Either the Runtime Permissions dialog box hasn't appeared even once till now.
           2. Or the Runtime Permissions dialog box did appear but the user denied the Permissions the last time you opened the app.
         */
            if(Build.VERSION.SDK_INT < 23)
            {
                Log.d("requestLocationUpdates", "permissions not granted yet");
                Log.d("permission not granted", "reason why onLocationChange is not getting called");
                Log.d("permission not granted", "go to settings and allow Location permission for this particular app");//    ActivityCompat#requestPermissions
                Toast.makeText(this, "Enable location settings for this app on your device", Toast.LENGTH_LONG).show();
            }
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            else
                {
                /* Code for providing runtime permission starts here */
                Log.d("MainActivity", "requestLocationUpdates: Asking for runtime permissions");
                final String perm[] = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                /*since your app doesnot have the permission you prompt the user to provide the permission
                by calling requestPermissions
                 */

                ActivityCompat.requestPermissions(this, perm, MY_PERMISSION_REQUEST_LOCATION_PERMISSION);
            }

        }
        else
        {
            //In case permission is granted this time, onRequestPermissionsResult gets called and 1st case in switch becomes true.
            //In case permission was granted the last time, else part gets called.
            Log.d("MainActivity", "requestForPermission : Permission already granted");
            myLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if(myLastLocation!=null)
            {
                myLatitude = myLastLocation.getLatitude();
                myLongitude = myLastLocation.getLongitude();
                latitude.setText("Latitude (requestForPermission) : " + String.valueOf(myLatitude));
                longitude.setText("Longitude (requestForPermission) :" + String.valueOf(myLongitude));
                Toast.makeText(this, "lat: " + myLatitude + ",long: " + myLongitude, Toast.LENGTH_SHORT).show();
            }
        }
    }
        //LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (LocationListener) this);


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
        counter = counter + 1;
        Log.d("MainActivity", "onLocationChanged called for " + Integer.toString(counter) + " time.");
        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();

        latitude.setText("Latitude is : " + String.valueOf(myLatitude));
        longitude.setText("Longitude is : " + String.valueOf(myLongitude));
        Toast.makeText(this, "lat: " + myLatitude + ",long: " + myLongitude, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d("MainActivity", "onStart called");
        googleApiClient.connect(); /*onConnected gets called when the client actually gets connected*/
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d("MainActivity","onResume");
        if(googleApiClient.isConnected())
        {
            Log.d("MainActivity", "yes connected. now calling requestForPermission explicitly from onResume");

        }
        else
        {
            Log.d("MainActivity", "Google client not yet connected");
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

    /*Callback for the result from requesting permissions. This method is invoked for every call on requestPermissions(android.app.Activity, String[], int).*/
    @Override
    public void onRequestPermissionsResult (int requestCode,
                                     String[] permissions,
                                     int[] grantResults)
    {
        Log.d("MainActivity", "onRequestPermissionResult called");
        switch(requestCode)
        {
            case MY_PERMISSION_REQUEST_LOCATION_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // Permission Granted
                    Log.d("MainActivity", "Permissions Granted :)");
                    myLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    if(myLastLocation!=null)
                    {
                        myLatitude = myLastLocation.getLatitude();
                        myLongitude = myLastLocation.getLongitude();

                        latitude.setText("Latitude (onRequestPermissionResult) : " + String.valueOf(myLatitude));
                        longitude.setText("Longitude (onRequestPermissionResult) :" + String.valueOf(myLongitude));
                        Toast.makeText(this, "lat: " + myLatitude + ",long: " + myLongitude, Toast.LENGTH_SHORT).show();
                    }

                }
                else if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                {

                    boolean permissionNotGranted = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
                    if (permissionNotGranted)
                    {
                        //Permission Denied but Never Ask Again was unchecked.
                        // Permission Denied
                        Toast.makeText(this, "Permissions Denied without Never Ask Again(onRequestPermissionResult)", Toast.LENGTH_LONG).show();
                        showMessageOKCancel("You need to allow access to Location",
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        Log.d("MainActivity", "ok clicked on dialog box");
                                    }
                                });
                    }
                    else
                    {
                        //Permission Denied and Never Ask Again checked.
                        Toast.makeText(this, "Permissions Denied with Never Ask Again(onRequestPermissionResult)", Toast.LENGTH_LONG).show();
                        Toast.makeText(this,"The app may not work as expected", Toast.LENGTH_LONG).show();
                    }

                }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /* NOT USING THIS METHOD FOR NOW */
    public void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener){
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage(message)
                    .setPositiveButton("OK", okListener)
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        }

}
