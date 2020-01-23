package ng.riby.androidtest.database;

import android.location.Location;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@androidx.room.Entity(tableName = "LocationEntity")
public class LocationEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "location1_lat")
    private double location1Lat;

    @ColumnInfo(name = "location1_lng")
    private double location1Lng;

    @ColumnInfo(name = "location2_lat")
    private double location2Lat;

    @ColumnInfo(name = "location2_lng")
    private double location2Lng;

    @ColumnInfo(name = "distance")
    private double distance;


    public LocationEntity(Location location1, Location location2) {
        this.location1Lat = location1.getLatitude();
        this.location1Lng = location1.getLongitude();
        this.location2Lat = location2.getLatitude();
        this.location2Lng = location2.getLongitude();
        distance = location1.distanceTo(location2);
    }

    public LocationEntity(){}


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public double getLocation1Lat() {
        return location1Lat;
    }

    public double getLocation1Lng() {
        return location1Lng;
    }

    public double getLocation2Lat() {
        return location2Lat;
    }

    public double getLocation2Lng() {
        return location2Lng;
    }

    public double getDistance(){
        return distance;
    }

    public void setLocation1Lat(double location1Lat) {
        this.location1Lat = location1Lat;
    }

    public void setLocation1Lng(double location1Lng) {
        this.location1Lng = location1Lng;
    }

    public void setLocation2Lat(double location2Lat) {
        this.location2Lat = location2Lat;
    }

    public void setLocation2Lng(double location2Lng) {
        this.location2Lng = location2Lng;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
