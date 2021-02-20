package com.reptilesoft.pas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	SharedPreferences preferences;
	final static String TAG = "BootReceiver";
	ConnectivityManager cManager;
	
	// recieve boot notification and start service
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean onBootEnabled = preferences.getBoolean("status", false);
		//long frequency = Long.parseLong(preferences.getString("frequency", "0"));
		//long duration = Long.parseLong(preferences.getString("duration", "0"));
		
		// start the service up?
		if(onBootEnabled)
		{
			Intent serviceIntent = new Intent(context, PASService.class);
			//serviceIntent.putExtra("frequency", frequency);
			//serviceIntent.putExtra("duration", duration);
			//Log.i(TAG, "starting service with f=" + frequency + ", and d=" +duration);
			//serviceIntent.setAction("com.reptilesoft.pas.PASService");
			context.startService(serviceIntent);
		}
		else
			Log.i(TAG,"service not starting");
	}
}
