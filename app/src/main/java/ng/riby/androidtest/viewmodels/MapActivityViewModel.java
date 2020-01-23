package ng.riby.androidtest.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.content.Context;
import android.location.Location;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.Routing;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import ng.riby.androidtest.database.LocationEntity;
import ng.riby.androidtest.repository.Repository;

public class MapActivityViewModel extends ViewModel {

    private static final String TAG = "MapActivityViewModel";

    private Repository repository;
    private boolean isNetworkAvailable;
    private MutableLiveData<Boolean> isGpsAvailable = new MutableLiveData<>();

    private FusedLocationProviderClient mFusedLocationProviderClient;

    public void initialize(Context context){
            if(repository == null){
                repository = new Repository(context);
            }
//            isNetworkAvailable = repository.getNetworkStatus();
            mFusedLocationProviderClient = new FusedLocationProviderClient(context);
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public LiveData<List<LocationEntity>> getLocation(Location location1, Location location2){
        return repository.getLocation(location1, location2);
    }

    public LiveData<Boolean> getNetworkStatus(){
        return repository.getNetworkStatus();
    }

    public LiveData<Boolean> getGpsStatus(){
        return repository.getLocationAvailability();
    }


    public void insertLocation(Location location1, Location location2){
        repository.insertLocation(location1, location2);
    }

    public FusedLocationProviderClient getFusedLocationProviderClient(){
        return mFusedLocationProviderClient;
    }


    public String calculateDistanceCovered(Location A, Location B){
        String value = "Distance covered: ";
        if(!isNetworkAvailable){
            value = "Distance covered: " + A.distanceTo(B);
        }else{
            float distance = A.distanceTo(B);
            value = "Distance covered: " + distance;
        }
        return value;
    }


}
