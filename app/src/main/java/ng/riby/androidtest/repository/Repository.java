package ng.riby.androidtest.repository;

import androidx.lifecycle.LiveData;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import ng.riby.androidtest.database.LocationDao;
import ng.riby.androidtest.database.LocationDatabase;
import ng.riby.androidtest.database.LocationEntity;
import ng.riby.androidtest.utils.LocationUtil;
import ng.riby.androidtest.utils.NetworkStatus;

public class Repository {

    private LocationDao locationDao;
    private NetworkStatus conStatus;
    private LocationUtil locationUtil;

    public Repository(Context context){
        conStatus = new NetworkStatus(context);
        locationUtil = new LocationUtil(context);
        LocationDatabase locationDatabase = LocationDatabase.getInstance(context);
        locationDao = locationDatabase.locationDao();
    }

    public MutableLiveData<Boolean> getNetworkStatus(){
        MutableLiveData<Boolean> networkStatus = new MutableLiveData<>();
        networkStatus.setValue(conStatus.checkNetworkStatus());
        return networkStatus;
    }

    public MutableLiveData<Boolean> getLocationAvailability(){
        MutableLiveData<Boolean> gpsStatus = new MutableLiveData<>();
        gpsStatus.setValue(locationUtil.checkLocationAvailability());
        return gpsStatus;
    }

    public LiveData<List<LocationEntity>> getLocation(Location location1, Location location2){
//        This should be done on a background thread
         return locationDao.getAllLocations();
    }

    public void insertLocation(Location location1, Location location2){
        new InsertLocationAsync(locationDao).execute(new LocationEntity(location1, location2));
    }

    public void deleteAllLocations(){
        new DeleteAllLocationsAsync(locationDao).execute();
    }



    private static class InsertLocationAsync extends AsyncTask<LocationEntity, Void, Void>{
        LocationDao locationDao;

        InsertLocationAsync(LocationDao locationDao){
            this.locationDao = locationDao;
        }

        @Override
        protected Void doInBackground(LocationEntity... locationEntities) {
            locationDao.insertLocation(locationEntities[0]);
            return null;
        }
    }

    private static class DeleteAllLocationsAsync extends AsyncTask<Void, Void, Void>{
        LocationDao locationDao;

        DeleteAllLocationsAsync(LocationDao locationDao){
            this.locationDao = locationDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            locationDao.deleteAllLocations();
            return null;
        }
    }




}
