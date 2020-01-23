package ng.riby.androidtest.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface LocationDao {

    @Query("SELECT * FROM LocationEntity")
    LiveData<List<LocationEntity>> getAllLocations();

    @Delete
    void deleteLocation(LocationEntity location);

    @Query("DELETE FROM LocationEntity")
    void deleteAllLocations();

    @Insert
    void insertLocation(LocationEntity location);



}
