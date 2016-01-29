package kgp.tech.interiit.sos;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.List;

import kgp.tech.interiit.sos.Utils.MyReceiver;

public class WelcomeActivity extends AppCompatActivity implements ContactItemFragment.OnContactsInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        final IntentFilter theFilter = new IntentFilter();
        /** System Defined Broadcast */
        theFilter.addAction(Intent.ACTION_SCREEN_ON);
        theFilter.addAction(Intent.ACTION_SCREEN_OFF);
        theFilter.addAction(Intent.ACTION_MEDIA_BUTTON);

        getApplicationContext().registerReceiver(new MyReceiver(), theFilter);
        setContentView(R.layout.activity_welcome);

        if (ParseUser.getCurrentUser() != null) {
            Log.d("Welcome","Logged in");
            // Start an intent for the logged in activity
            ParseUser.getCurrentUser().pinInBackground();

            SharedPreferences sp = getSharedPreferences("SOS", Context.MODE_PRIVATE);
            if(sp.getString("sosID", null)!= null)
            {
                Log.d("Welcome","SOS");
                final Intent intent = new Intent(WelcomeActivity.this, MessageActivity.class);
                intent.putExtra("channelID", sp.getString("channelID", null));
                intent.putExtra("username", sp.getString("username", null));
                intent.putExtra("Description", sp.getString("Description", null));
                intent.putExtra("createdAt", sp.getString("createdAt", null));
                intent.putExtra("displayname", sp.getString("displayName", null));
                startActivity(intent);
                finish();
                return;
            }

            // Locate the objectId from the class
            //////////////////////Background thing start
            AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);


            Calendar timeOff9 = Calendar.getInstance();

            Intent intent = new Intent(this,MyReceiver.class);

            PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            am.set(AlarmManager.RTC_WAKEUP, timeOff9.getTimeInMillis() + 30000, sender);

            //////////////////////Background thing end


            ParseQuery<ParseObject> query = ParseQuery.getQuery("Trusted");
            query.whereEqualTo("UserId", ParseUser.getCurrentUser());

            // Query for new results from the network.
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(final List<ParseObject> scores, ParseException e) {
                    if(e!=null)
                    {
                        e.printStackTrace();
                        return;
                    }
                    // Remove the previously cached results.
                    ParseObject.unpinAllInBackground("trusted", new DeleteCallback() {
                        public void done(ParseException e) {
                            // Cache the new results.
                            if(e!=null) {
                                e.printStackTrace();
                                return;
                            }
                            if(!scores.isEmpty()) {
                                ParseObject.pinAllInBackground("trusted", scores);
                                Log.d("welcome", String.valueOf(scores.size()));
                            }
                        }
                    });
                }
            });

            startActivity(new Intent(this, HomeActivity.class));
            //startActivity(new Intent(this, FirstTimeActivity.class));
            finish();
        } else {
            // Start and intent for the logged out activity
            Log.d("Welcome","Go to Log screen");
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }

    @Override
    public void onContactSelected(String lookupKey) {
        ContactItemFragment itemFragment = new ContactItemFragment();
        Log.i("Info", "");
        itemFragment.setLookupKey(lookupKey);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, itemFragment).commit();
    }

    @Override
    public void onSelectionCleared() {

    }
}
