package com.reptilesoft.pas;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class PASService extends Service {

	static final String TAG = "PASService";
	Timer onTimer;
	Timer offTimer;
	long frequency = 0, duration = 0;
	boolean isSyncing = false, vibration = false, notification = true;
	SharedPreferences preferences;
	NotificationManager notMgr;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		
		Toast.makeText(this, "pas service running, auto sync will be carried out periodically", Toast.LENGTH_LONG).show();
		
		// notificaiton mgr
		notMgr = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		
		// get prefs
		getPreferences();
		
		// create the main daemon
		createDaemon();
		
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
			
		//Log.i(TAG, "f=" + frequency + ", d=" + duration);
		// get prefs
		//getPreferences();
	}
	
	@Override
    public void onDestroy() {
    	super.onDestroy();
    	
    	// cancel ongoing timers
    	try {
    		onTimer.cancel();
    		offTimer.cancel();
    		syncOff();
    		notMgr.cancelAll();
    	}catch(Exception e){e.printStackTrace();}
    	
    	// announce
    	Toast.makeText(this, "pas service killed, no more auto sync for you.", Toast.LENGTH_SHORT).show();
    }
	
	public void createDaemon() {
		
		// this will do the work of creating the periodic timer
		// sync, exit
		Log.i(TAG,"createDaemon(), frequency = " + frequency);
		onTimer = new Timer();
		onTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				syncOn();
			}
		}, 0,frequency);
	}
	
	public void syncOn() {
		// turn on auto sync
		
		boolean syncStatus = ContentResolver.getMasterSyncAutomatically();
		
		if(vibration)
			vibrate(com.reptilesoft.pas.Constants.VIBRATION_DURATION);
		
		if(notification)
			showNotification("Every " + ((frequency/1000)/60) + " minutes for " + ((duration/1000)/60) + " minutes", "Periodic Auto Sync");
		
		// status? it may already be on...
		if(!syncStatus)
		{
			//Toast.makeText(this, "pas: starting auto sync for " + (duration/1000) + " seconds", Toast.LENGTH_SHORT).show();
			Log.i(TAG,"starting auto sync");
			ContentResolver.setMasterSyncAutomatically(true);
			// sync done, turn off
			offTimer = new Timer();
			offTimer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					Intent intent = new Intent(Intent.ACTION_SYNC);
					sendBroadcast(intent);
						syncOff();
				}
			}, duration, duration);
		}
	}
	
	public void syncOff() {
		
		boolean syncStatus = ContentResolver.getMasterSyncAutomatically();
		
		if(syncStatus)
		{
			//Toast.makeText(this, "pas: stopping auto sync", Toast.LENGTH_SHORT).show();
			Log.i(TAG,"stopping auto sync");
			ContentResolver.setMasterSyncAutomatically(false);
			// off timer not needed right now	
			offTimer.cancel();
		}
		
		if(vibration)
			vibrate(com.reptilesoft.pas.Constants.VIBRATION_DURATION);
	}
	
	public void getPreferences() {
		
		// get frequcny and duraiton, passed in by intent
		//PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		frequency = Long.parseLong(preferences.getString("frequency", "0"));
		duration = Long.parseLong(preferences.getString("duration", "0"));
		vibration = preferences.getBoolean("vibration", true);
		notification = preferences.getBoolean("notification", true);
	}
	
	public void vibrate(long duration) {
		// a short jolt
		// vibrate
		Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(duration);
		vibrator.cancel();
	}
	
public void showNotification(String ticker_text, String content_title) {
		
		Notification notification = new Notification(android.R.drawable.alert_dark_frame, ticker_text, System.currentTimeMillis());

		Intent notIntent = new Intent(this, PeriodicAutoSync.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notIntent, 0);
		
		notification.flags |= Notification.FLAG_NO_CLEAR;
		
		notification.setLatestEventInfo(getApplicationContext(), content_title, ticker_text, contentIntent);
		
		notMgr.notify(com.reptilesoft.pas.Constants.ACTIVE_PROFILE_ID, notification);
	}

}
