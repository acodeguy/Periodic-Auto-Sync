package com.reptilesoft.pas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;

public class Preferences extends PreferenceActivity  {

	Preference frequency, duration;
	CheckBoxPreference status;
	boolean preferencesHaveChanged = false, startService = false;
	
	public void onCreate(Bundle b) {
		
		super.onCreate(b);
		addPreferencesFromResource(R.xml.preferences);
		/* implements OnPreferenceChangeListener
		status = (CheckBoxPreference)findPreference("status");
		status.setOnPreferenceChangeListener(this);
		frequency = (Preference)findPreference("frequency");
		frequency.setOnPreferenceChangeListener(this);
		duration = (Preference)findPreference("duration");
		duration.setOnPreferenceChangeListener(this); */
	}
	/*
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		
		preferencesHaveChanged = true;
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean appEnabled = preferences.getBoolean("status", false);
		if(appEnabled)
			startService = true;
			
		return false;
	}*/
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		/*if(preferencesHaveChanged)
		{
			// something has changed, restart the service
			Intent serviceIntent = new Intent(this, PASService.class);
			stopService(serviceIntent);
			// if app enabled, restart
			if(startService)
				startService(serviceIntent);
		}*/
		
		Intent serviceIntent = new Intent(this, PASService.class);
		stopService(serviceIntent);
		startService(serviceIntent);
	}
}
