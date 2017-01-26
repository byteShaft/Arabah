package byteshaft.com.arabah;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import byteshaft.com.arabah.accounts.LoginActivity;
import byteshaft.com.arabah.accounts.ManageProfile;
import byteshaft.com.arabah.accounts.RegisterActivity;
import byteshaft.com.arabah.utils.AppGlobals;
import byteshaft.com.arabah.utils.Helpers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button foodTruckOwneButton;
    private Button userButton;

    private TextView logintextView;

    private static MainActivity instance;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 0;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        if (AppGlobals.isUserLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), ManageProfile.class));
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
        foodTruckOwneButton = (Button) findViewById(R.id.owner_button);
        userButton = (Button) findViewById(R.id.user_button);
        logintextView = (TextView) findViewById(R.id.login_textview);
        foodTruckOwneButton.setOnClickListener(this);
        userButton.setOnClickListener(this);
        logintextView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.owner_button:
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                break;
            case R.id.user_button:
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                } else {
                    if (!Helpers.locationEnabled()) {
                        // notify user
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setMessage("Location is not enabled");
                        dialog.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                // TODO Auto-generated method stub
                                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);
                                //get gps
                            }
                        });
                        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                // TODO Auto-generated method stub

                            }
                        });
                        dialog.show();
                    } else {
                        startActivity(new Intent(getApplicationContext(), TruckFinderActivity.class));
                    }
                }
                break;
            case R.id.login_textview:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    System.out.println("True");
                    if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
                        if (!Helpers.locationEnabled()) {
                            // notify user
                            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                            dialog.setMessage("Location is not enabled");
                            dialog.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    // TODO Auto-generated method stub
                                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(myIntent);
                                    //get gps
                                }
                            });
                            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    // TODO Auto-generated method stub

                                }
                            });
                            dialog.show();
                        }
                    } else {
                        if (!Helpers.locationEnabled()) {
                            // notify user
                            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                            dialog.setMessage("Location is not enabled");
                            dialog.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    // TODO Auto-generated method stub
                                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(myIntent);
                                    //get gps
                                }
                            });
                            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                                }
                            });
                            dialog.show();
                        }
                    }


                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();

                }
                return;
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_right_out);
    }
}
