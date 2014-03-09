
package com.example.snrwlns;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.example.snrwlns.threads.CheckEventComingUpThread;
 
public class CheckEventComingUp extends Service {
 
	public static final String TAG 													= "SNRWLNS";
	public static final String PREFS_NAME 											= "SNRWLNS";
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
 
	@Override
	public void onCreate() {
		Log.i(TAG, "SERVICE onCreate for CheckEventComingUp");
	}
 
	@Override
	public void onDestroy() {
		//code to execute when the service is shutting down
		Log.i(TAG, "SERVICE onDestroy for CheckEventComingUp");
	}
 
	@Override
	public void onStart(Intent intent, int startid) {
		//code to execute when the service is starting up
		Log.i(TAG, "SERVICE: CheckEventComingUp onStart");
		SharedPreferences settings =  getSharedPreferences(PREFS_NAME, 0);
		CheckEventComingUpThread obj = new CheckEventComingUpThread(CheckEventComingUp.this, settings.getString("userId", "100"));
		obj.start();
	}
}
