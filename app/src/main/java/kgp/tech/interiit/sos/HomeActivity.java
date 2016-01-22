package kgp.tech.interiit.sos;

import android.content.AsyncQueryHandler;
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

import com.google.android.gms.maps.GoogleMap;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import kgp.tech.interiit.sos.Utils.UserData;


public class HomeActivity extends AppCompatActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView drawerlistView;
    private MyDrawerAdapter adapter1;
    private TabLayout tabLayout;
    private ViewPager viewPager;

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

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

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
                    Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();

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
    }

    @Override
    protected void onResume() {
        super.onResume();
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
}

class MyDrawerAdapter extends BaseAdapter {

    String[] options;
    String[] items = {"Trusted Contacts", "Settings2","Settings3", "Logout"};
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


