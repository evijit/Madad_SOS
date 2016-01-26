package kgp.tech.interiit.sos.Utils;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseUser;

/**
 * Created by nishantiam on 17-01-2016.
 */

public class Places{
    private String name;
    private LatLng lat_lng;
    private char type;
    // 's' for hospital
    // 'l' for police
    // 'a' for pharmacy
    // the third character

    // constructor
    public Places(String name, double latitude, double longitude, char type) {
        this.name = name;
        this.lat_lng = new LatLng(latitude, longitude);
        this.type = type;

    }

    public Places(String name, LatLng lat_lng, char type) {
        this.name = name;
        this.lat_lng = lat_lng;
        this.type = type;
    }

    // getter
    public String getName() { return name; }
    public LatLng getLat_lng() { return lat_lng; }
    public char getType() {return type;}
    // setter

    public void setName(String name) { this.name = name; }
    public void setLat_lng(LatLng lat_lng) { this.lat_lng = lat_lng; }
}
