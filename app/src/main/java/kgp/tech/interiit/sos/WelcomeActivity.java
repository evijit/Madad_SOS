package kgp.tech.interiit.sos;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParseUser;

public class WelcomeActivity extends AppCompatActivity implements ContactItemFragment.OnContactsInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        if (ParseUser.getCurrentUser() != null) {
            // Start an intent for the logged in activity
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
