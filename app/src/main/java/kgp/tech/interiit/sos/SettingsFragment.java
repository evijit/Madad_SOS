package kgp.tech.interiit.sos;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by akshaygupta on 25/01/16.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}