package kgp.tech.interiit.sos;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.Vector;

import kgp.tech.interiit.sos.Utils.People;
import lt.lemonlabs.android.expandablebuttonmenu.ExpandableButtonMenu;
import lt.lemonlabs.android.expandablebuttonmenu.ExpandableMenuOverlay;


public class MyMapFragment extends Fragment implements LocationListener {

    private MapView mMapView; // Might be null if Google Play services APK is not available.
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.


    private ExpandableMenuOverlay menuOverlay;

    public Vector<People> getHelpers() {
        // We have to get it from the server
        Vector<People> helpers = new Vector<People>(1);
        helpers.addElement(new People("test loc", 22.33, 87.32));
        return helpers;
    }

    public Vector<MarkerOptions> getMarkers() {
        Vector<People> helpers = getHelpers();
        Vector<MarkerOptions> markers = new Vector<MarkerOptions>(helpers.size());
        MarkerOptions mp;
        for(int i=0;i<helpers.size();i++)
        {
            mp = new MarkerOptions();
            mp.title(helpers.get(i).getName());
            mp.position(helpers.get(i).getLat_lng());
            mp.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            markers.add(mp);
        }
        return markers;
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

                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

            }
        }
        else {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        }

            mMapView = (MapView) v.findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);
            mMap=mMapView.getMap();




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

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
//        setUpMapIfNeeded();
    }


//    private void setUpMapIfNeeded() {
//        // Do a null check to confirm that we have not already instantiated the map.
//
//            if (mMap != null) {
//            }
//    }


    @Override
    public void onLocationChanged(Location location) {
        mMap.clear();

        MarkerOptions mp = new MarkerOptions();

        mp.position(new LatLng(location.getLatitude(), location.getLongitude()));

        mp.title("Your Location");

        mMap.addMarker(mp);
        ////// Adding Markers for helpers
        Vector<MarkerOptions> markers = getMarkers();
        for (int i=0;i<markers.size();i++)
            mMap.addMarker(markers.get(i));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 16));

        if(Build.VERSION.SDK_INT >=23) {
            if (!(getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
//             TODO: Consider calling
//                public void requestPermissions(@NonNull String[] permissions, int requestCode)
//
//               public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                                      int[] grantResults)
//             to handle the case where the user grants the permission. See the documentation
//             for Activity#requestPermissions for more details.

                mMap.setMyLocationEnabled(true);


            }
        }
        else {
            mMap.setMyLocationEnabled(true);

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


}


