package kgp.tech.interiit.sos;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.util.Vector;

import kgp.tech.interiit.sos.Utils.People;
import lt.lemonlabs.android.expandablebuttonmenu.ExpandableButtonMenu;
import lt.lemonlabs.android.expandablebuttonmenu.ExpandableMenuOverlay;


public class MapsActivity extends AppCompatActivity implements LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView drawerlistView;
    private MyDrawerAdapter adapter1;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user", ParseUser.getCurrentUser());
        installation.saveInBackground();

        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(new SpannableString("Home"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if(Build.VERSION.SDK_INT >=23) {
            if (!(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
//             TODO: Consider calling
//                public void requestPermissions(@NonNull String[] permissions, int requestCode)
//
//               public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                                      int[] grantResults)
//             to handle the case where the user grants the permission. See the documentation
//             for Activity#requestPermissions for more details.

                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

                return;
            }
        }
        else {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }

        setUpMapIfNeeded();

        menuOverlay = (ExpandableMenuOverlay) findViewById(R.id.button_menu);
        menuOverlay.setOnMenuButtonClickListener(new ExpandableButtonMenu.OnMenuButtonClick() {
            @Override
            public void onClick(ExpandableButtonMenu.MenuButton action) {
                switch (action) {
                    case MID:
                        // do stuff and dismiss
                        Intent i = new Intent(MapsActivity.this, Chatlist.class);
                        startActivity(i);
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


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        drawerlistView = (ListView) findViewById(R.id.list_view_drawer);

        adapter1 = new MyDrawerAdapter(this);


        drawerlistView.setAdapter(adapter1);

        drawerlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View v, int position, long id) {
                //Get your item here with the position
                if (position == 0) {
//                    Intent intent = new Intent(TrucklistActivity.this, Account.class);
//                    startActivity(intent);

                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }
                if (position == 1) {
//                    Intent intent = new Intent(TrucklistActivity.this, Contact.class);
//                    startActivity(intent);
                    mDrawerLayout.closeDrawer(Gravity.LEFT);

                }

                if (position == 2) {

//                    Intent intent = new Intent(TrucklistActivity.this, TrackLinkList.class);
//                    startActivity(intent);
                    mDrawerLayout.closeDrawer(Gravity.LEFT);

                }

                if (position == 3) {

//                    getSharedPreferences("MyPref", MODE_PRIVATE).edit().clear().commit();
//
//                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    ParseUser.logOutInBackground();
                    Intent intent = new Intent(MapsActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();

                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    public void openchat(View v)
    {
        Intent i = new Intent(MapsActivity.this, Chatlist.class);
        startActivity(i);
    }
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

    }

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
    }



    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}

class MyDrawerAdapter extends BaseAdapter {

    String[] options;
    String[] items = {"Settings1", "Settings2","Settings3", "Logout"};
    int[] images = {R.drawable.pass, R.drawable.pass,R.drawable.pass, R.drawable.pass};
    private Context context;


    MyDrawerAdapter(Context context) {
        this.context = context;
        options = items;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return options.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return options[position];
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View row = null;


        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.drawer_list_item, parent, false);
        } else {
            row = convertView;
        }
        TextView tv1 = (TextView) row.findViewById(R.id.text1);
        ImageView iv1 = (ImageView) row.findViewById(R.id.image1);
        RelativeLayout lLayout = (RelativeLayout) row.findViewById(R.id.parentLayout);

        tv1.setText(options[position]);
        iv1.setImageResource(images[position]);
        lLayout.setVisibility(View.VISIBLE);


        return row;
    }

}

