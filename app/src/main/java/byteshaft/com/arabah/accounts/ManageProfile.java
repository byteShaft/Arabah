package byteshaft.com.arabah.accounts;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.requests.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import byteshaft.com.arabah.R;
import byteshaft.com.arabah.utils.AppGlobals;
import byteshaft.com.arabah.utils.WebServiceHelpers;

/**
 * Created by husnain on 1/17/17.
 */

public class ManageProfile extends AppCompatActivity implements View.OnClickListener,
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener{

    private TextView logOutTextView;
    private Button saveButton;

    private EditText mDescription;
    private String mDescriptionString;
    private HttpRequest request;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_manage_profile);
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
        logOutTextView = (TextView) findViewById(R.id.logout);
        saveButton = (Button) findViewById(R.id.save_button);
        mDescription = (EditText) findViewById(R.id.description_edit_text);
        logOutTextView.setOnClickListener(this);
        saveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Confirmation");
                alertDialogBuilder.setMessage("Do you really want to logout?").setCancelable(false).setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences sharedpreferences = AppGlobals.getPreferenceManager();
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.clear();
                                editor.commit();
                                AppGlobals.logout = true;
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();
                            }
                        });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            case R.id.save_button:
                mDescriptionString = mDescription.getText().toString();
                System.out.println(mDescriptionString);
                updateUserDescriptions(mDescriptionString);
                mDescription.getText().clear();
                break;
        }

    }

    private void updateUserDescriptions(String description) {
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("PUT", "http://46.101.27.152/api/me");
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send(getUpdateUserData(description));
        WebServiceHelpers.showProgressDialog(ManageProfile.this, "Update Descriptions");
    }


    private String getUpdateUserData(String description) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("description", description);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

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
                switch (request.getStatus()) {
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(this, "Update Failed!", "please check your internet connection");
                        break;
                    case HttpURLConnection.HTTP_OK:
                        System.out.println(request.getResponseText() + "working ");
                        AppGlobals.alertDialog(this, "Update!", "Descriptions Updated Successfully");
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
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
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_ID, userId);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_Description, description);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
