package dev.linhnv.getposition;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DevLinhnv on 11/19/2016.
 */
@IgnoreExtraProperties
public class Position {
    public double latitude;
    public double longtitude;
    public Position(){}
    public Position(double latitude, double longtitude){
        this.latitude = latitude;
        this.longtitude = longtitude;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("latitude", latitude);
        result.put("longtitude", longtitude);
        return result;
    }
}
