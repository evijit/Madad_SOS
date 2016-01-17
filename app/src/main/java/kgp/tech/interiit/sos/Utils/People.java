package kgp.tech.interiit.sos.Utils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by nishantiam on 17-01-2016.
 */
public class People {
    private String name;
    private LatLng lat_lng;

    // constructor
    public People(String name, double latitude, double longitude) {
        this.name = name;
        this.lat_lng = new LatLng(latitude, longitude);

    }

    public People(String name, LatLng lat_lng) {
        this.name = name;
        this.lat_lng = lat_lng;

    }

    // getter
    public String getName() { return name; }
    public LatLng getLat_lng() { return lat_lng; }
    // setter

    public void setName(String name) { this.name = name; }
    public void setLat_lng(LatLng lat_lng) { this.lat_lng = lat_lng; }
}
