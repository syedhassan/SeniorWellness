package com.example.snrwlns.c2dm;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.snrwlns.Main;
import com.example.snrwlns.threads.C2dmGetEventDataObject;
import com.example.snrwlns.threads.SendRegIdToServer;

public class MyC2dmReceiver extends C2DMBaseReceiver {
	
	private static final int HELLO_ID 												= 1;
	public static final String TAG 													= "SNRWLNS";
	public static final String PREFS_NAME 											= "SNRWLNS";
	
	private SharedPreferences settings;
	
	public MyC2dmReceiver() {
		super(Main.SENDERS_EMAIL_ID);
	}

	@Override
	public void onRegistered(Context context, String registrationId) throws java.io.IOException {
		Log.i(TAG, "Registration ID arrived: Fantastic!!!");
		Log.i(TAG, registrationId);
		SharedPreferences settings =  getSharedPreferences(Main.PREFS_NAME, 0);
		String possibleEmail = null;
		Account[] accounts = AccountManager.get(this).getAccounts();
		for (Account account : accounts) {
		  possibleEmail = account.name;
		  if (account.type.equalsIgnoreCase("com.google") & (account.name.contains("@gmail.com") | account.name.contains("@google.com"))) {
			  possibleEmail = account.name;
			  Log.i(TAG, "Account name ="+possibleEmail+"~"+account.type);
			  break;
		  }
		}
		SendRegIdToServer sendRegId = new SendRegIdToServer(context, possibleEmail, registrationId, settings.getString("userId", "100"));
		sendRegId.start();
	};

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Message: Fantastic!!!");

		Bundle extras = intent.getExtras();
		if (extras != null) {
			Log.i(TAG, "New Friend Sign Up Message = "+extras.get("newFriendSignUp"));
			Log.i(TAG, "New Message of an event = "+extras.get("newMessage"));
			Log.i(TAG, "Event Added = "+extras.get("newEvent"));
			Log.i(TAG, "Picture = "+extras.get("picture"));
			settings =  getSharedPreferences(PREFS_NAME, 0);
			
			if (extras.get("newFriendSignUp") != null) {
				Intent newFriendSignUp = new Intent ();
				newFriendSignUp.setClassName("com.example.snrwlns", "com.example.snrwlns.NotificationNewFriendSignUp");
				newFriendSignUp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				String message = extras.get("newFriendSignUp").toString();
				String[] msg = message.split("~~");
				Bundle bundle = new Bundle();
				bundle.putString("message1", msg[0]);
				bundle.putString("message2", msg[1].split(">")[0]);
				bundle.putInt("position", Integer.parseInt(msg[1].split(">")[1]));
				newFriendSignUp.putExtras(bundle);
				startActivity(newFriendSignUp);
			}
			else if (extras.get("newMessage") != null) {
				Intent newMessage = new Intent ();
				newMessage.setClassName("com.example.snrwlns", "com.example.snrwlns.NotificationNewMessage");
				newMessage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				String message = extras.get("newMessage").toString();
				String[] msg = message.split("~~");
				Bundle bundle = new Bundle();
				bundle.putString("message1", msg[0]);
				bundle.putString("message2", msg[1].split(">")[0]);
				bundle.putInt("position", Integer.parseInt(msg[1].split(">")[1]));
				newMessage.putExtras(bundle);
				startActivity(newMessage);

			}
			else if (extras.get("newEvent") != null) {
				Intent newEvent = new Intent ();
				newEvent.setClassName("com.example.snrwlns", "com.example.snrwlns.NotificationNewEvent");
				newEvent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				String message = extras.get("newEvent").toString();
				String[] msg = message.split("~~");
				Bundle bundle = new Bundle();
				bundle.putString("message1", msg[0]);
				bundle.putString("message2", msg[1].split(">")[0]);
				bundle.putInt("position", Integer.parseInt(msg[1].split(">")[1]));
				newEvent.putExtras(bundle);
				startActivity(newEvent);
			}
			else if (extras.get("eventReminder") != null) {
				Intent newEvent = new Intent ();
				newEvent.setClassName("com.example.snrwlns", "com.example.snrwlns.NotificationEventComingUp");
				newEvent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				String message = extras.get("eventReminder").toString();
				String[] msg = message.split("~~");
				Bundle bundle = new Bundle();
				bundle.putString("message1", msg[0]);
				bundle.putString("message2", msg[1].split(">")[0]);
				bundle.putInt("position", Integer.parseInt(msg[1].split(">")[1]));
				newEvent.putExtras(bundle);
				startActivity(newEvent);
			}

			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification();
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notification.ledARGB = 0xff00ff00;
			notification.ledOnMS = 300;
			notification.ledOffMS = 1000;
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
			mNotificationManager.notify(HELLO_ID, notification);
		}
		
		C2dmGetEventDataObject thread = new C2dmGetEventDataObject(this, settings.getString("userId", "100"), handler);
		thread.start();
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.e(TAG, "Error occured!!! id = "+errorId);
	}
	
	private Handler handler = new Handler() {
		@Override
    	public void handleMessage(Message msg) {
			
			if (msg.what == 2001) {
				Log.i(TAG, "C2dm handler checking for events details that was refreshed");
//				byte[] data = msg.getData().getByteArray("mainData");
//				LinkedHashMap<Integer, LinkedHashMap<String, String>> adapterData = null;
//				ObjectInputStream ois;
//				try {
//					ois = new ObjectInputStream(new ByteArrayInputStream(data));
//					adapterData = (LinkedHashMap<Integer, LinkedHashMap<String, String>>) ois.readObject();
//					Iterator<Integer> eventKey = adapterData.keySet().iterator();
//					while(eventKey.hasNext()) {
//						Integer key = eventKey.next();
//						Log.i(TAG, "Event Id = "+key);
//						Log.i(TAG, "Data = "+adapterData.get(key));
//					}
//				} catch (StreamCorruptedException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				} catch (ClassNotFoundException e) {
//					e.printStackTrace();
//				}
			}
		}
	};
}