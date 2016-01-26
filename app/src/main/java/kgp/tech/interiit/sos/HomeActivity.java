package kgp.tech.interiit.sos;

import android.content.AsyncQueryHandler;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
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

import com.github.fabtransitionactivity.SheetLayout;
import com.google.android.gms.maps.GoogleMap;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import kgp.tech.interiit.sos.Utils.UserData;


public class HomeActivity extends AppCompatActivity implements SheetLayout.OnFabAnimationEndListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView drawerlistView;
    private MyDrawerAdapter adapter1;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private static final int REQUEST_CODE = 1;

    SheetLayout mSheetLayout;
    FloatingActionButton mFab;


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MyMapFragment(), "MAP");
        adapter.addFragment(new ChatlistFragment(), "CHAT");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartApp.userData = new UserData();
        //StartApp.userData.update();
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(new SpannableString("Home"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }
        //centerToolbarTitle(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        mSheetLayout=(SheetLayout)findViewById(R.id.bottom_sheet);
        mFab=(FloatingActionButton)findViewById(R.id.fab);
        mSheetLayout.setFab(mFab);
        mSheetLayout.setFabAnimationEndListener(this);


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
                    Intent intent = new Intent(HomeActivity.this, TrustedActivity.class);
                    startActivity(intent);
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }

                if (position == 1) {

//                    Intent intent = new Intent(TrucklistActivity.this, TrackLinkList.class);
//                    startActivity(intent);
                    Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    //finish();
                    mDrawerLayout.closeDrawer(Gravity.LEFT);

                }

                if (position == 2) {

//                    getSharedPreferences("MyPref", MODE_PRIVATE).edit().clear().commit();
//
//                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    ParseUser.getCurrentUser().unpinInBackground();
                    ParseUser.logOutInBackground();
                    Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    finish();

                }

            }
        });

        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(android.content.Context);
        //Log.d("prefs dist", String.valueOf(sharedPref.getInt(getString(R.string.pref_distance),0)));

        ParseQuery<ParseObject> pq = new ParseQuery("picture");
        pq.whereEqualTo("user",ParseUser.getCurrentUser());
        pq.fromLocalDatastore();
        pq.getFirstInBackground(new GetCallback<ParseObject>() {

            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    // Locate the objectId from the class
                    Bitmap bmp = BitmapFactory
                            .decodeByteArray(
                                    parseObject.getBytes("picture"), 0,
                                    parseObject.getBytes("picture").length);

                    // Get the ImageView from
                    // main.xml
                    ImageView image = (ImageView) findViewById(R.id.avatar);

                    // Set the Bitmap into the
                    // ImageView
                    image.setImageBitmap(bmp);
                }
            }
        });

        ImageView avatar=(ImageView)findViewById(R.id.avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(HomeActivity.this, AccountDetails.class);
                startActivity(i);
            }
        });

        TextView name = (TextView) findViewById(R.id.drawer_username);
        name.setText(ParseUser.getCurrentUser().getUsername());
        TextView email = (TextView) findViewById(R.id.drawer_email);
        email.setText(ParseUser.getCurrentUser().getEmail());
    }

    @Override
    protected void onResume() {
        super.onResume();

        ParseQuery<ParseObject> pq = new ParseQuery("picture");
        pq.fromLocalDatastore();

        pq.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null && !list.isEmpty()) {
                    // Locate the objectId from the class
                    Bitmap bmp = BitmapFactory
                            .decodeByteArray(
                                    list.get(0).getBytes("picture"), 0,
                                    list.get(0).getBytes("picture").length);

                    // Get the ImageView from
                    // main.xml
                    ImageView image = (ImageView) findViewById(R.id.avatar);

                    // Set the Bitmap into the
                    // ImageView
                    image.setImageBitmap(bmp);
                }
            }
        });
    }

    static void centerToolbarTitle(final Toolbar toolbar) {
        final CharSequence title = toolbar.getTitle();
        final ArrayList<View> outViews = new ArrayList<>(1);
        toolbar.findViewsWithText(outViews, title, View.FIND_VIEWS_WITH_TEXT);
        if (!outViews.isEmpty()) {
            final TextView titleView = (TextView) outViews.get(0);
            titleView.setGravity(Gravity.CENTER);
            final Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) titleView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            toolbar.requestLayout();
            //also you can use titleView for changing font: titleView.setTypeface(Typeface);
        }
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


    protected void enterFromBottomAnimation(){
        overridePendingTransition(R.anim.activity_open_translate_from_bottom, R.anim.activity_no_animation);
    }

    protected void exitToBottomAnimation(){
        overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_close_translate_to_bottom);
    }

    public void onFabClick(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSheetLayout.expandFab();
        }
        else
        {
            Intent intent = new Intent(HomeActivity.this, AnimatedButtons.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    public void onFabAnimationEnd() {
        Intent intent = new Intent(HomeActivity.this, AnimatedButtons.class);
        startActivityForResult(intent, REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            mSheetLayout.contractFab();
        }
    }
}

class MyDrawerAdapter extends BaseAdapter {

    String[] options;
    String[] items = {"Trusted Contacts","Settings", "Logout"};
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

class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

}


