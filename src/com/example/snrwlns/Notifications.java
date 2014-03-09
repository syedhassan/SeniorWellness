package com.example.snrwlns;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snrwlns.adapters.NotificationsAdapter;
import com.example.snrwlns.threads.UpdateUserNotifications;

public class Notifications extends Activity {
	
	/**
	 **  Called when the user confirms sign up.
	 */
	
	public static final String TAG = "SNRWLNS";
	public static final String PREFS_NAME = "SNRWLNS";
	private ArrayList<HashMap<String, String>> notificationsData;
	private ImageButton saveButton;
	private SharedPreferences settings;
	private HashMap<String, String> mapData;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (!isTaskRoot()) {
		    final Intent intent = getIntent();
		    final String intentAction = intent.getAction();
		    if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) &&
		            intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
		        finish();
		    }
		}
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.notifications);
	    mapData = new HashMap<String, String>();
	    byte[] data = getIntent().getExtras().getByteArray("notificationsData");
	    saveButton = (ImageButton)findViewById(R.id.saveButton);

		
		{
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new ByteArrayInputStream(data));
				notificationsData = (ArrayList<HashMap<String, String>>) ois.readObject();
				//Log.i(TAG, "~~~NOTIFICATION DATA = "+notificationsData);
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		Log.i(TAG, "Open the Notifcations activity");
		NotificationsAdapter notifyAdapter = new NotificationsAdapter(Notifications.this, notificationsData);
		ListView list1 = (ListView) findViewById(R.id.notificationListView);
		list1.setAdapter(notifyAdapter);
		list1.setSaveEnabled(true);
		list1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adp, View v, int position, long id) {
			 	Log.i(TAG, "Notification clicked = "+position);
			 	saveButton.setClickable(true);
			 	saveButton.setImageResource(R.drawable.save);
			 	ImageView shareImage = (ImageView) v.findViewById(R.id.checkImage);
			 	if (notificationsData.get(position).get("notificationShare").equalsIgnoreCase("share")) {
			 		shareImage.setImageResource(R.drawable.checkbox75x66);
			 		notificationsData.get(position).put("notificationShare", "unshare");
			 		mapData.put(notificationsData.get(position).get("notificationName"), "N");
			 	}
			 	else {
			 		shareImage.setImageResource(R.drawable.checkboxselected75x66);
			 		notificationsData.get(position).put("notificationShare", "share");
			 		mapData.put(notificationsData.get(position).get("notificationName"), "Y");
			 	}
			 	saveButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						settings =  getSharedPreferences(PREFS_NAME, 0);
						UpdateUserNotifications updateShareInfo = new UpdateUserNotifications(Notifications.this, settings.getString("userId", "100"), mapData, messageFromThread);
						updateShareInfo.start();
						
					}
				});
			 }
		});
		
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent resultIntent = new Intent();
				Bundle bundle = new Bundle();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
			    ObjectOutputStream oos;
				try {
					oos = new ObjectOutputStream(bos);
					oos.writeObject(notificationsData);
				    oos.flush();
				    oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				bundle.putByteArray("notificationData", bos.toByteArray());
				resultIntent.putExtras(bundle);
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
				
			}
		});
	}
	
	private Handler messageFromThread = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		Log.i(TAG, "In Handler for FriendsShare thead, result= "+msg.what+" arg1 = "+msg.arg1);
    		if (msg.what == 200 & msg.arg1 == 1) {
    			Log.i(TAG, "Object = "+msg.obj);
    			Intent resultIntent = new Intent();
				Bundle bundle = new Bundle();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
			    ObjectOutputStream oos;
				try {
					oos = new ObjectOutputStream(bos);
					oos.writeObject(notificationsData);
				    oos.flush();
				    oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				bundle.putByteArray("notificationData", bos.toByteArray());
				resultIntent.putExtras(bundle);
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
    		}
    		else {
    			LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("Error occurred in udpating your information. Please try again!!!");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();
//    			Toast.makeText(Notifications.this, "Error occurred in udpating your information. Please try again!!!", 1000).show();
    		}
    	}
	};
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	Intent resultIntent = new Intent();
			Bundle bundle = new Bundle();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream(bos);
				oos.writeObject(notificationsData);
			    oos.flush();
			    oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			bundle.putByteArray("notificationData", bos.toByteArray());
			resultIntent.putExtras(bundle);
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
