package byteshaft.com.arabah.accounts;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.requests.HttpRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import byteshaft.com.arabah.MainActivity;
import byteshaft.com.arabah.R;
import byteshaft.com.arabah.utils.AppGlobals;
import byteshaft.com.arabah.utils.WebServiceHelpers;

/**
 * Created by husnain on 1/16/17.
 */

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, HttpRequest.OnReadyStateChangeListener,
        HttpRequest.OnErrorListener {

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

    private HttpRequest request;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
        mFoodTruckName = (EditText) findViewById(R.id.food_truck_name_edit_text);
        mEmailAddress = (EditText) findViewById(R.id.email_edit_text);
        mMobileNumber = (EditText) findViewById(R.id.mobile_edit_text);
        mDescription = (EditText) findViewById(R.id.description_edit_text);
        mPassword = (EditText) findViewById(R.id.password_edit_text);
        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateEditText()) {
                    buildGoogleApiClient();
                    mGoogleApiClient.connect();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_right_out);
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
        registerUser(mFoodTrcuknameString, mPasswordString,
                mEmailAddressString, mMobileNumberString, mDescriptionString, mLocationString);

    }

    private void registerUser(String truckName, String password, String email, String phoneNumber,
                              String description, String location) {
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%suser/register", AppGlobals.BASE_URL));
        request.send(getRegisterData(truckName, password, email, phoneNumber, description, location));
        WebServiceHelpers.showProgressDialog(RegisterActivity.this, "Registering FoodTruck");
    }


    private String getRegisterData(String truckName, String password, String email, String phoneNumber,
                                   String description, String location) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("truck_name", truckName);
            jsonObject.put("password", password);
            jsonObject.put("email", email);
            jsonObject.put("mobile_number", phoneNumber);
            jsonObject.put("description", description);
            jsonObject.put("location", location);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Log.i("TAG", "Url " + request.getResponseURL());
                WebServiceHelpers.dismissProgressDialog();
                Log.i("TAG", "Response " + request.getResponseText());
                switch (request.getStatus()) {
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(RegisterActivity.this, "Registration Failed!", "please check your internet connection");
                        break;
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        AppGlobals.alertDialog(RegisterActivity.this, "Registration Failed!", "Email already in use");
                        break;
                    case HttpURLConnection.HTTP_CREATED:
                        System.out.println(request.getResponseText() + "working ");
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
                            System.out.println(jsonObject + "working ");
                            String username = jsonObject.getString(AppGlobals.KEY_FOOD_TRUCK_NAME);
                            String userId = jsonObject.getString(AppGlobals.KEY_USER_ID);
                            String email = jsonObject.getString(AppGlobals.KEY_EMAIL);
                            String phoneNumber = jsonObject.getString(AppGlobals.KEY_PHONE_NUMBER);
                            String description = jsonObject.getString(AppGlobals.KEY_Description);
                            //saving values
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_FOOD_TRUCK_NAME, username);
                            Log.i("user name", " " + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_FOOD_TRUCK_NAME));
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_PHONE_NUMBER, phoneNumber);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_Description, description);
                            Log.i("user name", " " + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_PHONE_NUMBER));
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_ID, userId);
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                            alertDialogBuilder.setTitle("Account Successfully Created!");
                            alertDialogBuilder.setMessage("After admins approval you will receive an activation E-Mail").setCancelable(false).setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            finish();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }

    }
}
