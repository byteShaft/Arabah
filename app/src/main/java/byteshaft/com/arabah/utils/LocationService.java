package byteshaft.com.arabah.utils;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.byteshaft.requests.HttpRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import byteshaft.com.arabah.MainActivity;
import byteshaft.com.arabah.R;

/**
 * Created by husnain on 1/24/17.
 */
public class LocationService extends IntentService implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    LatLng latLngUserUpdatedLocation;
    private HttpRequest request;

    public static int onLocationChangedCounter = 0;

    public GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    boolean isLocationUpdateTaskIsRunning;

    public static LatLng driverCurrentLocation = null;
    private static int recursionCounter = 0;

    public LocationService() {
        super("Location Service");
    }


    public void startLocationServices() {
        connectGoogleApiClient();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onLocationChanged(Location location) {
        onLocationChangedCounter++;
        driverCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if (onLocationChangedCounter > 2 && !isLocationUpdateTaskIsRunning) {
            updateUserLocation(driverCurrentLocation.latitude + "," + driverCurrentLocation.longitude);
            Log.i("LocationServices", "LocationChanged called" + driverCurrentLocation.latitude + ","
                    + driverCurrentLocation.longitude);
        }
        Log.i("LocationServices", "LocationChanged called");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Failed to start Location Service", Toast.LENGTH_LONG).show();
    }

    private void connectGoogleApiClient() {
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    protected void createLocationRequest() {
        long INTERVAL = 0;
        long FASTEST_INTERVAL = 0;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        recursionCounter();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    public void stopLocationService() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
        onLocationChangedCounter = 0;
    }

    void recursionCounter() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (recursionCounter > 160 && mGoogleApiClient.isConnected()) {
                    stopLocationService();
                    Log.i("LocationServices", "Location cannot be acquired at the moment");
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startLocationServices();
                        }
                    }, 2000);
                } else if (mGoogleApiClient.isConnected()) {
                    recursionCounter();
                    recursionCounter++;
                }
            }
        }, 1000);
    }

    private void updateUserLocation(String location) {
        isLocationUpdateTaskIsRunning = true;
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("PUT", "http://46.101.27.152/api/me");
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send(getUpdateUserDataLocation(location));
    }

    private String getUpdateUserDataLocation(String location) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("location", location);
            jsonObject.put("on_service", "true");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        isLocationUpdateTaskIsRunning = false;
        Log.i("onError", "Called");
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        isLocationUpdateTaskIsRunning = false;
        Log.i("onReadyState", "Called");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Notification.Builder builder = new Notification.Builder(AppGlobals.getContext());
        builder.setSmallIcon(R.mipmap.marker_logo);
        builder.setContentTitle("Arabah FoodTruck");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        builder.setContentIntent(pendingIntent);
        startForeground(1, builder.build());
        startLocationServices();
    }
}
