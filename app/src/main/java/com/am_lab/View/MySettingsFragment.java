package com.am_lab.View;
import com.am_lab.R;


import android.content.SharedPreferences;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.prefs.PreferenceChangeListener;


public class MySettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = MySettingsFragment.class.getSimpleName();
    //settings
    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //preferences list stored in res/xml/settings_pref
        setPreferencesFromResource(R.xml.settings_pref, rootKey);

        //region setting preference summary to its value
        Preference IPAddress = findPreference("IP_address");
        IPAddress.setSummary(sharedPreferences.getString("IP_address", ""));

        Preference SamplingTime =findPreference("Sampling_Time");
        SamplingTime.setSummary(sharedPreferences.getString("Sampling_Time", ""));

       Preference LedColor = findPreference("Default_LED_Color");
        LedColor.setSummary(sharedPreferences.getString("Default_LED_Color", ""));
        //endregion

    }


    @Override
    public void onResume() {
        super.onResume();
        //unregister the preferenceChange listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Listener awaiting preference changes
     * @param sharedPreferences settings
     * @param key preference key
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        preference.setSummary(sharedPreferences.getString(key, ""));
    }
}