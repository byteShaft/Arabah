package byteshaft.com.arabah.accounts;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import byteshaft.com.arabah.MainActivity;
import byteshaft.com.arabah.R;

/**
 * Created by husnain on 1/16/17.
 */

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Button registerButton;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    private EditText mFoodTruckName;
    private EditText mEmailAddress;
    private EditText mPassword;
    private EditText mMobileNumber;
    private EditText mDescription;

    private String mFoodTrcuknameString;
    private String mEmailAddressString;
    private String mPasswordString;
    private String mMobileNumberString;
    private String mDescriptionString;
    private String mLocationString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        mFoodTruckName = (EditText) findViewById(R.id.food_truck_name_edit_text);
        mEmailAddress = (EditText) findViewById(R.id.email_edit_text);
        mMobileNumber = (EditText) findViewById(R.id.mobile_edit_text);
        mDescription = (EditText) findViewById(R.id.description_edit_text);
        mPassword = (EditText) findViewById(R.id.password_edit_text);
        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildGoogleApiClient();
                mGoogleApiClient.connect();
                validateEditText();
//                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    private boolean validateEditText() {
        boolean valid = true;
        mFoodTrcuknameString = mFoodTruckName.getText().toString();
        mEmailAddressString = mEmailAddress.getText().toString();
        mPasswordString = mPassword.getText().toString();
        mMobileNumberString = mMobileNumber.getText().toString();
        mDescriptionString = mDescription.getText().toString();

        System.out.println(mPasswordString);
        System.out.println(mFoodTrcuknameString);
        System.out.println(mEmailAddressString);
        System.out.println(mDescriptionString);
        System.out.println(mMobileNumberString);

        if (mFoodTrcuknameString.trim().isEmpty()) {
            mFoodTruckName.setError("required");
        } else {
            mFoodTruckName.setError(null);
        }

        if (mDescriptionString.trim().isEmpty()) {
            mDescription.setError("required");
        } else {
            mDescription.setError(null);
        }

        if (mMobileNumberString.trim().isEmpty()) {
            mMobileNumber.setError("required");
        } else {
            mMobileNumber.setError(null);
        }

        if (mPasswordString.trim().isEmpty() || mPasswordString.length() < 3) {
            mPassword.setError("enter at least 3 characters");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        if (mEmailAddressString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailAddressString).matches()) {
            mEmailAddress.setError("please provide a valid email");
            valid = false;
        } else {
            mEmailAddress.setError(null);
        }
        return valid;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void startLocationUpdates() {
        long INTERVAL = 0;
        long FASTEST_INTERVAL = 0;
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(INTERVAL);
//        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }


    @Override
    public void onLocationChanged(Location location) {
        String latitude = "" + location.getLatitude();
        String longitude = "" + location.getLongitude();
        mLocationString = "" + location.getLatitude() + "," + location.getLongitude();
        System.out.println(latitude + "lat");
        System.out.println(longitude + "long");
        System.out.println(mLocationString + "latlong");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
