package com.example.snrwlns;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyStartupIntentReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("SNRWLNS", "MYSTARTUPINTENTRECEIVER");
		Intent serviceIntent = new Intent();
		serviceIntent.setClassName("com.example.snrwlns", "com.example.snrwlns.AlarmEventComingUp");
		context.startService(serviceIntent);
	}
}