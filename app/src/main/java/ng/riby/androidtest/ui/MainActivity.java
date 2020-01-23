package ng.riby.androidtest.ui;

import android.app.Dialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import ng.riby.androidtest.R;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(isGoogleMapsUpToDate()){
            init();
        }

    }

    private void init() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        finish();
    }


    public boolean isGoogleMapsUpToDate(){
        Log.d(TAG, "isGoogleMapsUpToDate: " +
                "checking if your Google maps version is up to date \n ...............................");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if(available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isGoogleMapsUpToDate: Your version is up to date");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isGoogleMapsUpToDate: Error found! but it can be resolved....");
            Dialog dialog = GoogleApiAvailability
                    .getInstance()
                    .getErrorDialog(this, available, ERROR_DIALOG_REQUEST);

            dialog.show();
        }else{
            Toast.makeText(this, "You can't make Map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


}
