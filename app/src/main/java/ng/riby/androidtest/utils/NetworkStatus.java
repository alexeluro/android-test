package ng.riby.androidtest.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


public class NetworkStatus {

    private Context context;
    private boolean isNetworkAvailable;

    public NetworkStatus(Context context){
        this.context = context;
    }

    public boolean checkNetworkStatus(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null){
            isNetworkAvailable = false;
            Toast.makeText(context, "No Internet Conection is Available", Toast.LENGTH_SHORT).show();
        }else{
            isNetworkAvailable = true;
        }
        return isNetworkAvailable;
    }

}
