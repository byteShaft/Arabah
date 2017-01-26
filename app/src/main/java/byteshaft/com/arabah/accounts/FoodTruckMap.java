package byteshaft.com.arabah.accounts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import byteshaft.com.arabah.R;
import byteshaft.com.arabah.utils.AppGlobals;
import byteshaft.com.arabah.utils.WebServiceHelpers;

/**
 * Created by husnain on 1/21/17.
 */

public class FoodTruckMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng currentLatLngAuto;
    boolean cameraAnimatedToCurrentLocation;
    private MarkerOptions currLocationMarker;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_food_truck_map);
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        WebServiceHelpers.showProgressDialog(this, "Acquiring current location");
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);


    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            WebServiceHelpers.dismissProgressDialog();
            currentLatLngAuto = new LatLng(location.getLatitude(), location.getLongitude());
            if (!cameraAnimatedToCurrentLocation) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLngAuto, 10.0f));
                currLocationMarker = new MarkerOptions().position(currentLatLngAuto).title(
                        AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_FOOD_TRUCK_NAME))
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.truck_marker));
                mMap.addMarker(currLocationMarker).showInfoWindow();
                cameraAnimatedToCurrentLocation = true;
            } else {
                currLocationMarker.position(currentLatLngAuto);
            }
        }
    };
}
