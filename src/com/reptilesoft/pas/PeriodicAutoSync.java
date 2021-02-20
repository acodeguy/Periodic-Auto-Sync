package com.reptilesoft.pas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class PeriodicAutoSync extends Activity implements OnClickListener {
	
	final static String TAG = "Periodic Auto Sync";
	static long frequency = 0, duration = 0;
	boolean appStatus = false;
	SharedPreferences preferences;
	static TextView textViewPreferences;
	ConnectivityManager cManager;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        // set view
        setContentView(R.layout.main);
        
        // get the background data setting
        cManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean backgroundDataEnabled = cManager.getBackgroundDataSetting();
        
        // sort out buttons
        Button buttonPreferences = (Button)findViewById(R.id.button_preferences);
        buttonPreferences.setOnClickListener(this);
        ImageButton buttonReptilesoft = (ImageButton)findViewById(R.id.button_rs_logo);
        buttonReptilesoft.setOnClickListener(this);
        TextView textViewVersion = (TextView)findViewById(R.id.textViewVersion);
        textViewPreferences = (TextView)findViewById(R.id.textViewPreferences);
        Button buttonStartNow = (Button)findViewById(R.id.buttonStartNow);
        buttonStartNow.setOnClickListener(this);
        Button buttonStopNow = (Button)findViewById(R.id.buttonStopNow);
        buttonStopNow.setOnClickListener(this);
        Button buttonBackgroundDataToggle = (Button)findViewById(R.id.buttonBackgroundDataToggle);
        buttonBackgroundDataToggle.setOnClickListener(this);
        TextView textViewWarning = (TextView)findViewById(R.id.textViewWarning);
        
        // get prefs
        getPreferences();
        
        // if bg data disabled, show text (and button?) 
        if(!backgroundDataEnabled)  
        {
        	textViewWarning.setVisibility(View.VISIBLE);
        	//buttonBackgroundDataToggle.setVisibility(View.VISIBLE);
        	buttonStartNow.setVisibility(View.GONE);
        	buttonStopNow.setVisibility(View.GONE);
        	//buttonBackgroundDataToggle.setEnabled(true);
        	
        	// warn user via dialog
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("This app will NOT run until you enable background data in Settings -> Accounts & Sync!")
        	       .setCancelable(false)
        	       .setPositiveButton("I understand", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                
        	           }
        	       });
        	AlertDialog alert = builder.create();
        	alert.show();
        }
        
        // get package details
        PackageManager packageManager;
        PackageInfo packageInfo;
        
        packageManager = getPackageManager();
		try{
			packageInfo = packageManager.getPackageInfo("com.reptilesoft.pas",0);
			/* set the ver */
			textViewVersion.setText("Periodic Auto Sync v" + packageInfo.versionName);
		}catch(NameNotFoundException e){
			e.printStackTrace();
		}

    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	//stopService(new Intent(this, PASService.class));
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	// get app refs again
    	getPreferences();
    }
    
    public void showPreferences() {
    	
    	// start the preferences
    	startActivity(new Intent(this, Preferences.class));
    }

	public void onClick(View v) {
		
		switch(v.getId())
		{
		case R.id.button_preferences:
			showPreferences();
			break;
		case R.id.button_rs_logo:
			/* go to market to view all my apps */
			startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("market://search?q=pub:\"Reptile Soft\"")));
			break;
		case R.id.buttonStartNow:
		{	
			// is app enabled?
			if(appStatus)
			{
				// stop service, then start service, passing values to intent
				Intent serviceIntent = new Intent(this, PASService.class);
				stopService(serviceIntent);
				serviceIntent.putExtra("frequency", frequency);
				serviceIntent.putExtra("duration", duration);
				//serviceIntent.setAction("com.reptilesoft.pas.PASService");
				startService(serviceIntent);
			}
			else
				Toast.makeText(this, "app is not enabled, change this is preferences if you want to use Periodic Auto Sync!", Toast.LENGTH_LONG).show();
		}
			break;
		case R.id.buttonStopNow:
		{
			Intent serviceIntent = new Intent(this, PASService.class);
			stopService(serviceIntent);
		}
		break;
			default: break;
		}
		
	}
	
	public void getPreferences() {
		
		// get app prefs
		PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false);
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		frequency = Long.parseLong(preferences.getString("frequency", "0"));
		duration = Long.parseLong(preferences.getString("duration", "0"));
		appStatus = preferences.getBoolean("status", false);
		textViewPreferences.setText("Periodic Auto Sync will run every " + ((frequency/1000)/60) + " minutes for " + ((duration/1000)/60) + " minutes." );
		Log.i(TAG,"getPreferences(), f=" + frequency + ", d=" + duration);
		
	}
}