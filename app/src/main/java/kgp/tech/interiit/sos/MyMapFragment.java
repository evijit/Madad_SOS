package kgp.tech.interiit.sos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.fabtransitionactivity.SheetLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import kgp.tech.interiit.sos.Utils.NetworkLocationService;
import kgp.tech.interiit.sos.Utils.People;
import kgp.tech.interiit.sos.Utils.Places;
import kgp.tech.interiit.sos.Utils.Utils;
import lt.lemonlabs.android.expandablebuttonmenu.ExpandableButtonMenu;
import lt.lemonlabs.android.expandablebuttonmenu.ExpandableMenuOverlay;


public class MyMapFragment extends Fragment implements LocationListener{

    public static MapView mMapView; // Might be null if Google Play services APK is not available.
    public static GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public static Location pre_hospital_point = null;
    final static Vector<Places> customPlaces = new Vector<Places>(10); // like People it too has a name
    public static boolean isAnimateCamera = true;
    public NetworkLocationService appLocationServiceNet = null;
    public static boolean isAddHospital = false;
    public static boolean isAddPolice = false;
    public static boolean isAddPharmacy = false;

    static private Context context=null;


    private ExpandableMenuOverlay menuOverlay;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.context = context;
    }

    public void getHelpers() {
        SharedPreferences sp = context.getSharedPreferences("SOS", Context.MODE_APPEND | Context.MODE_PRIVATE);
        String SOSid = null;
        if(sp!=null) SOSid = sp.getString("sosID", null);

        if(SOSid!=null)
        {
            ParseQuery<ParseObject> pq = ParseQuery.getQuery("SOS_Users");

            ParseObject sos = new ParseObject("SOS");
            //sos.setObjectId(SOSid);

            pq.include("UserID");
            pq.whereEqualTo("SOSid", SOSid);
            pq.whereEqualTo("hasAccepted", true);


            pq.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if(e!=null)
                    {
                        Utils.showDialog(getActivity(),e.getMessage());
                        return;
                    }
                    for (ParseObject l : list) {
                        ParseUser user = l.getParseUser("UserID");
                        double lat = user.getParseGeoPoint("Geolocation").getLatitude();
                        double lng = user.getParseGeoPoint("Geolocation").getLongitude();
                        Log.d("MyMap","I am in trouble loop");
                        Log.d("MyMap",user.getUsername()+" Pos " + lat + " " +lng);
                        Log.d("MyMap",ParseUser.getCurrentUser().getUsername()+" Pos " +
                                ParseUser.getCurrentUser().getParseGeoPoint("Geolocation").getLatitude() + " " +
                                ParseUser.getCurrentUser().getParseGeoPoint("Geolocation").getLongitude());
                        // Adding to map
                        MarkerOptions mp = new MarkerOptions();
                        mp.title(user.getUsername());
                        mp.position(new LatLng(lat,lng));
                        mp.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        mMap.addMarker(mp);
                    }
                }
            });
        }
        else
        {
            ParseQuery<ParseObject> pq = ParseQuery.getQuery("SOS_Users");
            pq.include("SOSid");
            pq.include("SOSid.UserID");
            pq.whereEqualTo("UserID",ParseUser.getCurrentUser());
            pq.whereEqualTo("hasAccepted", true);


            pq.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if(e!=null)
                    {
                        e.printStackTrace();
                        return;
                    }
                    for (ParseObject l : list) {
                        Log.d("MyMap",l.keySet().toString());
                        if(!(l.getParseObject("SOSid").getBoolean("isActive")))
                            continue;
                        ParseUser user = l.getParseObject("SOSid").getParseUser("UserID");
                        double lat = user.getParseGeoPoint("Geolocation").getLatitude();
                        double lng = user.getParseGeoPoint("Geolocation").getLongitude();

                        Log.d("MyMap","I am helper");
                        Log.d("MyMap",user.getUsername()+" Pos " + lat + " " +lng);
                        Log.d("MyMap",ParseUser.getCurrentUser().getUsername()+" Pos " +
                                ParseUser.getCurrentUser().getParseGeoPoint("Geolocation").getLatitude() + " " +
                                ParseUser.getCurrentUser().getParseGeoPoint("Geolocation").getLongitude());

                        // Adding to map
                        MarkerOptions mp = new MarkerOptions();
                        mp.title(user.getUsername());
                        mp.position(new LatLng(lat,lng));
                        mp.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        mMap.addMarker(mp);
                    }
                }
            });
        }
    }

//    public Vector<MarkerOptions> getMarkers() {
//        Vector<People> helpers = getHelpers();
//        Vector<MarkerOptions> markers = new Vector<MarkerOptions>(helpers.size());
//        MarkerOptions mp;
//        for(int i=0;i<helpers.size();i++)
//        {
//            mp = new MarkerOptions();
//            mp.title(helpers.get(i).getName());
//            mp.position(helpers.get(i).getLat_lng());
//            mp.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//            markers.add(mp);
//        }
//        return markers;
//    }

    public void addCustomMarkers(final double lat,final double lng, final char type)
    {
        final BitmapDescriptor bitmap ;
        if(type=='l') {
            bitmap = BitmapDescriptorFactory.fromResource(R.drawable.police_marker);
        }
        else if(type=='s') {
            bitmap = BitmapDescriptorFactory.fromResource(R.drawable.hospital_marker);
        }
        else if(type == 'a') {
            bitmap = BitmapDescriptorFactory.fromResource(R.drawable.pharmacy_marker);
        }
        else{
            bitmap = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
        }
        boolean was_null = false;
        if(pre_hospital_point == null) {
            was_null =true;
            pre_hospital_point = new Location("loc");
            pre_hospital_point.setLongitude(lng);
            pre_hospital_point.setLatitude(lat);
        }
        Location l = new Location("loc");
        l.setLatitude(lat);
        l.setLongitude(lng);
        if(l.distanceTo(pre_hospital_point) >= 1000 || was_null == true)
        {
            pre_hospital_point = l;
            customPlaces.clear();
            Thread thread = new Thread(new Runnable() {


                @Override
                public void run() {
                    try {
                        String key = "AIzaSyCTDMaJIhXDmGiz7dlJcmghD2LoVgKkTpI";
//                      //  String key = "AIzaSyD8JnsCfZdpM_SJ-nzdF-fiJA_3YPfXdYs";
                        String url = "https://maps.googleapis.com/maps/api/place/search/json?location=" + lat + "," + lng + "&rankby=distance&types=hospital|police|pharmacy&key=" + key;
                        String data = getJSON(url, 3000);
                        try {
                            JSONObject jsonRootObject = new JSONObject(data);

                            //Get the instance of JSONArray that contains JSONObjects
                            JSONArray jsonArray = jsonRootObject.getJSONArray("results");

                            //Iterate the jsonArray and print the info of JSONObjects
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                Double lat = Double.parseDouble(jsonObject.getJSONObject("geometry").getJSONObject("location").optString("lat"));
                                Double lng = Double.parseDouble(jsonObject.getJSONObject("geometry").getJSONObject("location").optString("lng"));
                                String name = jsonObject.optString("name").toString();

                                String types = jsonObject.getString("types");
                                customPlaces.addElement(new Places(name, lat, lng, types.charAt(4)));
                                if(types.charAt(4)== type ) {
                                    MarkerOptions mp = new MarkerOptions();
                                    mp.title(customPlaces.get(i).getName());
                                    mp.position(customPlaces.get(i).getLat_lng());
                                    mp.icon(bitmap);
                                    mMap.addMarker(mp);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Your code goes here
                    } catch (Exception e) {
                        pre_hospital_point = null;
                        e.printStackTrace();
                    }
                }
            });
            thread.run();
        }
        else
        {
            for (int i = 0; i < customPlaces.size(); i++) {
                char type_from_places = customPlaces.get(i).getType();
                if(type_from_places== type ) {
                    MarkerOptions mp = new MarkerOptions();
                    mp.title(customPlaces.get(i).getName());
                    mp.position(customPlaces.get(i).getLat_lng());
                    mp.icon(bitmap);
                    mMap.addMarker(mp);
                }
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.frag_maps, container, false);
        // Inflate the layout for this fragment
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user", ParseUser.getCurrentUser());
        installation.saveInBackground();

//        HashMap<String, Object> params = new HashMap<>();
//        ParseCloud.callFunctionInBackground("sendSOS", params, new FunctionCallback<Float>() {
//            public void done(Float ratings, ParseException e) {
//                if (e == null) {
//                    // ratings is 4.5
//                }
//            }
//        });

        if (appLocationServiceNet == null)
            appLocationServiceNet = new NetworkLocationService(
                    getContext());
        boolean gps_enabled =false;

        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


        if(Build.VERSION.SDK_INT >=23) {
            if (!(getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
//             TODO: Consider calling
//                public void requestPermissions(@NonNull String[] permissions, int requestCode)
//
//               public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                                      int[] grantResults)
//             to handle the case where the user grants the permission. See the documentation
//             for Activity#requestPermissions for more details.

                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, appLocationServiceNet);
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

            }
            else
            {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, appLocationServiceNet);
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

            }
        }
        else {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, appLocationServiceNet);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        }


        mMapView = (MapView) v.findViewById(R.id.map);

        mMapView.onCreate(savedInstanceState);
        mMap=mMapView.getMap();
        mMap.setMyLocationEnabled(true);




//        setUpMapIfNeeded();



        menuOverlay = (ExpandableMenuOverlay) v.findViewById(R.id.button_menu);
        menuOverlay.setOnMenuButtonClickListener(new ExpandableButtonMenu.OnMenuButtonClick() {
            @Override
            public void onClick(ExpandableButtonMenu.MenuButton action) {
                switch (action) {
                    case MID:
                        // do stuff and dismiss
//                        Intent i = new Intent(MapsActivity.this, Chatlist.class);
//                        startActivity(i);
                        //sendSOS();
                        menuOverlay.getButtonMenu().toggle();
                        break;
                    case LEFT:
                        // do stuff
                        break;
                    case RIGHT:
                        // do stuff
                        break;
                }
            }
        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public String getJSON(String url, int timeout) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        isAnimateCamera = true;

//        setUpMapIfNeeded();
    }


//    private void setUpMapIfNeeded() {
//        // Do a null check to confirm that we have not already instantiated the map.
//
//            if (mMap != null) {
//            }
//    }

    @Override
    public void onLocationChanged(final Location location) {
        if(location.getAccuracy() <= appLocationServiceNet.getLocation().getAccuracy())
            return;
        if(mMap!=null) {
            mMap.clear();

            ////// Adding Markers for helpers
            //Vector<MarkerOptions> markers = getMarkers();
//            for (int i = 0; i < markers.size(); i++)
//                mMap.addMarker(markers.get(i));
            getHelpers();
            if(isAddHospital) { // 's' is for hospital
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        addCustomMarkers(location.getLatitude(), location.getLongitude(),'s');
                    }
                });
                thread.run();
            }

            if(isAddPolice) { // 'l' is for police
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        addCustomMarkers(location.getLatitude(), location.getLongitude(),'l');
                    }
                });
                thread.run();
            }
            if(isAddPharmacy) // 'a' for pharmacy
            {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        addCustomMarkers(location.getLatitude(), location.getLongitude(),'a');
                    }
                });
                thread.run();
            }

            if (isAnimateCamera) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 16));
                isAnimateCamera = false;
            }
        }
    }



    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }



    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
/*
    public void showSettingsAlert(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                getContext());

        alertDialog.setTitle(provider + " SETTINGS");

        alertDialog
                .setMessage(provider + " is not enabled! Want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MyMapFragment.this.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

*/


}


