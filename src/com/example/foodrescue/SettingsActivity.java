package com.example.foodrescue;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by anishpc on 8/19/14.
 */
public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.pref_general);        
	    }
	 
	 public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
		        String key) {		        
		  Preference connectionPref = findPreference(key);		            
		  connectionPref.setSummary(sharedPreferences.getString(key, ""));		        
	 }

	


}