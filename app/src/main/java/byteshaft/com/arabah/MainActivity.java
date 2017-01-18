package byteshaft.com.arabah;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import byteshaft.com.arabah.accounts.LoginActivity;
import byteshaft.com.arabah.accounts.RegisterActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button foodTruckOwneButton;
    private Button userButton;

    private TextView logintextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
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
                startActivity(new Intent(getApplicationContext(), TruckFinderActivity.class));
                break;
            case R.id.login_textview:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
        }

    }
}
