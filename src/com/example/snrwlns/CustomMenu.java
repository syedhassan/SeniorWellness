package com.example.snrwlns;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snrwlns.threads.GetNotificationsDataObject;

public class CustomMenu extends Activity{
	public static final String TAG 													= "SNRWLNS";
	public static final String PREFS_NAME 											= "SNRWLNS";
	private ProgressDialog pd;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Custom Menu");
	    setContentView(R.layout.custommenu);
	    
	    RelativeLayout eventsLayout = (RelativeLayout) findViewById(R.id.eventsLayout);
	    eventsLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Applying the filter for EVENTS only");
					Bundle filterInfo = new Bundle();
					filterInfo.putString("filter", "events");
			    	Intent intent = new Intent();
			    	intent.putExtras(filterInfo);
	    			setResult(Activity.RESULT_OK, intent);
					finish();
			}
		});
	    
	    TextView eventsText = (TextView) findViewById(R.id.eventsText);
	    eventsText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Applying the filter for EVENTS only");
				Bundle filterInfo = new Bundle();
				filterInfo.putString("filter", "events");
		    	Intent intent = new Intent();
		    	intent.putExtras(filterInfo);
    			setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	    
	    RelativeLayout classesLayout = (RelativeLayout) findViewById(R.id.classesLayout);
	    classesLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Applying the filter for CLASSES only");
				Bundle filterInfo = new Bundle();
				filterInfo.putString("filter", "classes");
		    	Intent intent = new Intent();
		    	intent.putExtras(filterInfo);
    			setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	    
	    TextView classesText = (TextView) findViewById(R.id.classesText);
	    classesText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Applying the filter for CLASSES only");
				Bundle filterInfo = new Bundle();
				filterInfo.putString("filter", "classes");
		    	Intent intent = new Intent();
		    	intent.putExtras(filterInfo);
    			setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	    
	    RelativeLayout showsLayout = (RelativeLayout) findViewById(R.id.showsLayout);
	    showsLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Applying the filter for SHOWS only");
				Bundle filterInfo = new Bundle();
				filterInfo.putString("filter", "shows");
		    	Intent intent = new Intent();
		    	intent.putExtras(filterInfo);
    			setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	    
	    TextView showsText = (TextView) findViewById(R.id.showsText);
	    showsText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Applying the filter for SHOWS only");
				Bundle filterInfo = new Bundle();
				filterInfo.putString("filter", "shows");
		    	Intent intent = new Intent();
		    	intent.putExtras(filterInfo);
    			setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	    
	    RelativeLayout allEventsLayout = (RelativeLayout) findViewById(R.id.allEventsLayout);
	    allEventsLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Applying the filter for ALL EVENTS");
				Bundle filterInfo = new Bundle();
				filterInfo.putString("filter", "ALL EVENTS");
		    	Intent intent = new Intent();
		    	intent.putExtras(filterInfo);
    			setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	    
	    TextView allEventsText = (TextView) findViewById(R.id.allEventsText);
	    allEventsText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Applying the filter for ALL EVENTS");
				Bundle filterInfo = new Bundle();
				filterInfo.putString("filter", "ALL EVENTS");
		    	Intent intent = new Intent();
		    	intent.putExtras(filterInfo);
    			setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	    
	    RelativeLayout myAccountRL = (RelativeLayout) findViewById(R.id.myAccountLayout);
	    myAccountRL.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "My Account Relative Layout was clicked");
				Intent myAccount = new Intent(CustomMenu.this, MyAccount.class);
	            startActivity(myAccount);
	            finish();
				
			}
		});
	    TextView myaccount = (TextView) findViewById(R.id.myAccountText);
	    myaccount.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "My Account was clicked");
				Intent myAccount = new Intent(CustomMenu.this, MyAccount.class);
	            startActivity(myAccount);
	            finish();
				
			}
		});
	    
	    
	    RelativeLayout refreshRL = (RelativeLayout) findViewById(R.id.refreshLayout);
	    refreshRL.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Refresh Relative Layout was clicked");
				Bundle filterInfo = new Bundle();
				filterInfo.putString("refresh", "refresh");
		    	Intent intent = new Intent();
		    	intent.putExtras(filterInfo);
    			setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	    TextView refresh = (TextView) findViewById(R.id.refreshText);
	    refresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Refresh was clicked");
				Bundle filterInfo = new Bundle();
				filterInfo.putString("refresh", "refresh");
		    	Intent intent = new Intent();
		    	intent.putExtras(filterInfo);
    			setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	    
	    
	    RelativeLayout friendsRL = (RelativeLayout) findViewById(R.id.friendsLayout);
	    friendsRL.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "My Account RElative Layout was clicked");
				Intent friendsShare = new Intent(CustomMenu.this, FriendsShare.class);
	            Bundle friendsBundle = new Bundle();
	            friendsBundle.putByteArray("friendsData", getFriendsObject());
	            friendsShare.putExtras(friendsBundle);
	            startActivity(friendsShare);
	            finish();
				
			}
		});
	    TextView friends = (TextView) findViewById(R.id.friendsText);
	    friends.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Friends was clicked");
				Intent friendsShare = new Intent(CustomMenu.this, FriendsShare.class);
	            Bundle friendsBundle = new Bundle();
	            friendsBundle.putByteArray("friendsData", getFriendsObject());
	            friendsShare.putExtras(friendsBundle);
	            startActivity(friendsShare);
	            finish();
				
			}
		});
	    
	    RelativeLayout notificationsRL = (RelativeLayout) findViewById(R.id.notificationsLayout);
	    notificationsRL.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pd = new ProgressDialog(CustomMenu.this);
	    		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	            pd.setMessage("Working...");
	            pd.setIndeterminate(true);
	            pd.setCancelable(false);
	            pd.show();
	            
	    	    SharedPreferences settings =  getSharedPreferences(PREFS_NAME, 0);
	    	    GetNotificationsDataObject getNotifications = new GetNotificationsDataObject(CustomMenu.this, settings.getString("userId", "100"), handlerNotifications);
	    	    getNotifications.start();
				
			}
		});
	    TextView notifications = (TextView) findViewById(R.id.notificationsText);
	    notifications.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "My Account was clicked");
	        	pd = new ProgressDialog(CustomMenu.this);
	    		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	            pd.setMessage("Working...");
	            pd.setIndeterminate(true);
	            pd.setCancelable(false);
	            pd.show();
	            
	    	    SharedPreferences settings =  getSharedPreferences(PREFS_NAME, 0);
	    	    GetNotificationsDataObject getNotifications = new GetNotificationsDataObject(CustomMenu.this, settings.getString("userId", "100"), handlerNotifications);
	    	    getNotifications.start();
				
			}
		});
	}
	
	private Handler handlerNotifications = new Handler() {
		@Override
    	public void handleMessage(Message msg) {
    		Log.i(TAG, "In Handler for notificationData thead, result= "+msg.what);
			pd.dismiss();
    		if (msg.what == 200 & msg.arg1 == 123) {
    			byte[] data = msg.getData().getByteArray("notificationData");
            	Intent notifications = new Intent(CustomMenu.this, Notifications.class);
            	Bundle bundle = new Bundle();
            	bundle.putByteArray("notificationsData", data);
            	notifications.putExtras(bundle);
            	startActivity(notifications);
    		}
    		else {
    			LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("There was an error fetching your data");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();
//    			Toast.makeText(CustomMenu.this, "There was an error fetching your data", 1000).show();
    		}
    		finish();
    	}
    };
	
    private byte[] getFriendsObject(){
    	FileInputStream in;
    	
    	SharedPreferences settings =  getSharedPreferences(PREFS_NAME, 0);
    	settings.getInt("friendsDataLength", 0);
    	byte[] buffer = new byte[settings.getInt("friendsDataLength", 0)];
		try {
			in = openFileInput("friendData");
	    	in.read(buffer, 0, buffer.length);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return buffer;
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
        	finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
