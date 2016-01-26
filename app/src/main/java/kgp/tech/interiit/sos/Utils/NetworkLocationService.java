package kgp.tech.interiit.sos.Utils;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Vector;

import kgp.tech.interiit.sos.HomeActivity;
import kgp.tech.interiit.sos.MyMapFragment;
import kgp.tech.interiit.sos.R;

/**
 * Created by nishantiam on 22-01-2016.
 */
public class NetworkLocationService extends Service implements LocationListener {
    protected LocationManager locationManager;
    Location location;
    private static final long MIN_DISTANCE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;

    public NetworkLocationService(Context context) {

        locationManager = (LocationManager) context
                .getSystemService(LOCATION_SERVICE);
    }

    //the default constructor is basically for startService method
    public NetworkLocationService() {
        super();
    }

    public Location getLocation() {
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        return location;
                    }
                    return null;
                } else {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        return location;
                    }
                    return null;
                }
            }
            else
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    return location;
                }
                return null;
            }
        }
        return null;
    }

    @Override
    public void onLocationChanged(final Location location) {
        final MyMapFragment maps = new MyMapFragment();
        if(maps.mMap!=null) {
            maps.mMap.clear();
            ////// Adding Markers for helpers
            Vector<MarkerOptions> markers = maps.getMarkers();
            for (int i = 0; i < markers.size(); i++)
                maps.mMap.addMarker(markers.get(i));

            if(maps.isAddHospital) { // 's' is for hospital
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        maps.addCustomMarkers(location.getLatitude(), location.getLongitude(),'s');
                    }
                });
                thread.run();
            }

            if(maps.isAddPolice) { // 'l' is for police
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        maps.addCustomMarkers(location.getLatitude(), location.getLongitude(),'l');
                    }
                });
                thread.run();
            }
            if(maps.isAddPharmacy)
            {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        maps.addCustomMarkers(location.getLatitude(), location.getLongitude(),'a');
                    }
                });
                thread.run();
            }

            if (maps.isAnimateCamera) {
                maps.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 16));
                maps.isAnimateCamera = false;
            }
        }

    }



    @Override
    public void onCreate()
    {
        super.onCreate();

        locationManager = (LocationManager) getApplicationContext()
                .getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    }

                } else {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    }

                }
            }
            else
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                }
            }
        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.e("Network Loc Service", "Service running");
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);


        Calendar timeOff9 = Calendar.getInstance();

        Intent intent2 = new Intent(this,MyReceiver.class);

        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent2,
                PendingIntent.FLAG_UPDATE_CURRENT);

        am.set(AlarmManager.RTC_WAKEUP, timeOff9.getTimeInMillis() + 55000, sender);
        ParseGeoPoint pt = new ParseGeoPoint(getLocation().getLatitude(),getLocation().getLongitude());
        ParseUser person = ParseUser.getCurrentUser();
        person.put("Geolocation",pt);
        person.saveInBackground();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Network Loc Service","onDestroy");
    }


    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}