package ng.riby.androidtest.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

public class LocationUtil {
    private Context context;
    private boolean isLocationAvailable;

    public LocationUtil(Context context){
        this.context = context;
    }

    public boolean checkLocationAvailability(){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            isLocationAvailable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception e){
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        return isLocationAvailable;
    }


}
