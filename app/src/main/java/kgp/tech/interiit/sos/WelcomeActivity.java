package kgp.tech.interiit.sos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import java.util.List;

public class WelcomeActivity extends AppCompatActivity implements ContactItemFragment.OnContactsInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        if (ParseUser.getCurrentUser() != null) {
            // Start an intent for the logged in activity
            ParseUser.getCurrentUser().pinInBackground();

            // Locate the objectId from the class
            ParseFile fileObject =  ParseUser.getCurrentUser().getParseFile("profilePic");
            if(fileObject!=null)
            {
                fileObject.getDataInBackground(new GetDataCallback() {

                    public void done(final byte[] data,
                                     ParseException e) {
                        if (e == null) {
                            Log.i("test",
                                    "We've got data in data.");
                            // Decode the Byte[] into
                            // Bitmap

                            ParseQuery<ParseObject> query = ParseQuery.getQuery("picture");
                            query.fromLocalDatastore();
                            query.whereEqualTo("user", ParseUser.getCurrentUser());
                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject parseObject, ParseException e) {
                                    if(e == null) {
                                        Log.i("test", parseObject.toString());
                                        parseObject.put("user", ParseUser.getCurrentUser());
                                        parseObject.put("picture", data);
                                        parseObject.pinInBackground();
                                    }else{
                                        Log.i("test","error");
                                        ParseObject pp = new ParseObject("picture");
                                        pp.put("user", ParseUser.getCurrentUser());
                                        pp.put("picture", data);
                                        pp.pinInBackground();
                                    }
                                }
                            });

                        } else {
                            Log.d("test",
                                    "There was a problem downloading the data.");
                        }
                    }
                });
            }


            ParseQuery<ParseObject> query = ParseQuery.getQuery("Trusted");
            query.whereEqualTo("UserId", ParseUser.getCurrentUser());

            // Query for new results from the network.
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(final List<ParseObject> scores, ParseException e) {
                    // Remove the previously cached results.
                    ParseObject.unpinAllInBackground("trusted", new DeleteCallback() {
                        public void done(ParseException e) {
                            // Cache the new results.
                            ParseObject.pinAllInBackground("trusted", scores);
                            Log.d("welcome", String.valueOf(scores.size()));
                        }
                    });
                }
            });

            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            // Start and intent for the logged out activity
            startActivity(new Intent(this, Login.class));
            finish();
        }
//        if (findViewById(R.id.fragment_container) != null) {
//
//            // However, if we're being restored from a previous state,
//            // then we don't need to do anything and should return or else
//            // we could end up with overlapping fragments.
//            if (savedInstanceState != null) {
//                return;
//            }
//
//            // Create a new Fragment to be placed in the activity layout
//            ContactListFragment listFragment = new ContactListFragment();
//
//            // In case this activity was started with special instructions from an
//            // Intent, pass the Intent's extras to the fragment as arguments
//            listFragment.setArguments(getIntent().getExtras());
//
//            // Add the fragment to the 'fragment_container' FrameLayout
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragment_container, listFragment).commit();
//        }
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
