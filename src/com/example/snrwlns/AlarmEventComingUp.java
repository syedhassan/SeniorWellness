
package com.example.snrwlns;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
 
public class AlarmEventComingUp extends Service {
 
	public static final String TAG = "SNRWLNS";
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
 
    @Override
    public void onCreate() {
    	Log.i(TAG, "SERVICE onCreate for AlarmEventComingUp");
    }
 
    @Override
    public void onDestroy() {
       //code to execute when the service is shutting down
	   Log.i(TAG, "SERVICE onDestroy for AlarmEventComingUp");
    }
 
    @Override
    public void onStart(Intent intent, int startid) {
    	
        //code to execute when the service is starting up
	    Log.i(TAG, "SERVICE onStart for AlarmEventComingUp ");
       
	    //This alarm will only run between 8AM until 9AM and check every 24hours
	    Intent myIntent = new Intent(AlarmEventComingUp.this, CheckEventComingUp.class);
	    PendingIntent pendingIntent = PendingIntent.getService(AlarmEventComingUp.this, 0, myIntent, 0);
        long currentTimeMillis = System.currentTimeMillis();
        long nextUpdateTimeMillis = currentTimeMillis + 1 * DateUtils.MINUTE_IN_MILLIS;
        Time nextUpdateTime = new Time();
        nextUpdateTime.set(nextUpdateTimeMillis);
        Log.i(TAG, "nextUpdateTime.hour = "+nextUpdateTime.hour);
        if (nextUpdateTime.hour < 8 || nextUpdateTime.hour > 9) {
        	
        	nextUpdateTime.hour = 8;
        	nextUpdateTime.minute = 0;
        	nextUpdateTime.second = 0;
        	nextUpdateTimeMillis = nextUpdateTime.toMillis(false) + DateUtils.DAY_IN_MILLIS;
        }
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Log.i(TAG, "Next Update Time Millis = "+nextUpdateTimeMillis);
        Log.i(TAG, "Difference = "+(nextUpdateTimeMillis-currentTimeMillis));
        alarmManager.setRepeating(AlarmManager.RTC, nextUpdateTimeMillis, 24*60*60*1000, pendingIntent);
    }
}
