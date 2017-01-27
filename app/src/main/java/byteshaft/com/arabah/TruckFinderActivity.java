package byteshaft.com.arabah;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.requests.HttpRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import byteshaft.com.arabah.utils.AppGlobals;
import byteshaft.com.arabah.utils.WebServiceHelpers;


public class TruckFinderActivity extends FragmentActivity implements OnMapReadyCallback,
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener, View.OnClickListener {

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private LatLng latLng;
    private Marker currLocationMarker;
    private HttpRequest request;
    private TextView reFreshTextView;
    private int zoomCounter = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_truck_finder);
        reFreshTextView = (TextView) findViewById(R.id.refresh);
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        reFreshTextView.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        getFoodTrucks();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            if (location != null) {
                Log.i("TAG", "location " + location.getLatitude());
                if (zoomCounter < 1) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))
                            .zoom(6).build();
                    mMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                }
                zoomCounter++;
            }

        }
    };

    private void getFoodTrucks() {
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("GET", String.format("%sactive-trucks", AppGlobals.BASE_URL));
        request.send();
        WebServiceHelpers.showProgressDialog(TruckFinderActivity.this, "Fetching FoodTrucks");
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (request.getStatus()) {
            case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                AppGlobals.alertDialog(this, "Update Failed!", "please check your internet connection");
                break;
            case HttpURLConnection.HTTP_OK:
                mMap.clear();
                WebServiceHelpers.dismissProgressDialog();
                try {
                    JSONArray jsonArray = new JSONArray(request.getResponseText());
                    Log.i("TAG", "work " + jsonArray);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            MarkerOptions markerOptions = new MarkerOptions();
                            String[] latLng = jsonObject.getString("location").split(",");
                            markerOptions.position(new LatLng(Double.parseDouble(latLng[0]),
                                    Double.parseDouble(latLng[1])));
                            markerOptions.title(jsonObject.getString("truck_name"));
                            markerOptions.snippet(jsonObject.getString("mobile_number"));
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.truck_marker));
                            mMap.addMarker(markerOptions);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

        }

    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.refresh:
                getFoodTrucks();
                break;
        }

    }
}
