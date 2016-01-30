package kgp.tech.interiit.sos;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
/*
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
*/
//import com.github.mikephil.charting.data.Entry;
import com.github.fabtransitionactivity.SheetLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class FullMap extends AppCompatActivity implements SheetLayout.OnFabAnimationEndListener{

    FloatingActionButton mFab;
    Toolbar mToolbar;
    SheetLayout mSheetLayout;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_map);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        mSheetLayout=(SheetLayout)findViewById(R.id.bottom_sheet);
        mSheetLayout.setFab(mFab);
        mSheetLayout.setFabAnimationEndListener(this);
        setSupportActionBar(mToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        SharedPreferences sp = getSharedPreferences("SOS", Context.MODE_APPEND | Context.MODE_PRIVATE);

        if(sp.getString("sosID", null)!=null)
        {
            Log.d("Message", "SOS active");
            setcolorred();
        }

        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            MyMapFragment mapFragment = new MyMapFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            mapFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mapFragment).commit();
        }
    }

    public void onFabClick(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSheetLayout.expandFab();
        }
        else
        {
            Intent intent = new Intent(FullMap.this, AnimatedButtons.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    public void onFabAnimationEnd() {
        Intent intent = new Intent(FullMap.this, AnimatedButtons.class);
        startActivityForResult(intent, REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            mSheetLayout.contractFab();
        }
    }

    void setcolorred()//use setcolor R.color.red for Self SOS
    {
        Log.d("Message", "Changing color");
        mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
        mFab.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.darkred));
        }
        mSheetLayout.setColor(ContextCompat.getColor(this, R.color.darkred));

        //mFab.setBackgroundTintList(ColorStateList.valueOf(colres));
    }
}