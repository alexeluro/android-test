package ng.riby.androidtest.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {LocationEntity.class}, version = 1, exportSchema = false)
public abstract class LocationDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "location_database.db";
//    LocationDao
    public abstract LocationDao locationDao();

    private static LocationDatabase instance;

    public static synchronized LocationDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    LocationDatabase.class,
                    DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }




}
