package kgp.tech.interiit.sos;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Created by akshaygupta on 25/01/16.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
    }


    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();

        // CHANGE 1: load saved values to set the summaries

        // CHANGE 2: register shared prefs listener in onResume
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pref_distance")) {
            Preference pref = findPreference(key);
            pref.setSummary(sharedPreferences.getString(key, ""));
        }
    }
}