package com.example.snrwlns;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.LinkedHashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snrwlns.threads.ConfirmSignOutEvent;

public class ConfirmSignOut extends FragmentActivity {
	
	/**
	 **  Called when the user confirms sign up.
	 */
	
	private EditText messageBody;
	private ImageButton yes, no;
	private String userId, eventId;
	private ProgressDialog pd;
	public static final String TAG 													= "SNRWLNS";
	public static final String PREFS_NAME 											= "SNRWLNS";
	
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
	    setContentView(R.layout.eventsignout);
	    LinearLayout headerLayout = (LinearLayout) findViewById(R.id.headerLayout);
	    if (this.getResources().getConfiguration().orientation == 1) {
	    	
		}
		else {
			LinearLayout.LayoutParams paramsNew = new LinearLayout.LayoutParams(529, LinearLayout.LayoutParams.FILL_PARENT);
			paramsNew.leftMargin = 8;
			paramsNew.topMargin = 32;
			headerLayout.setLayoutParams(paramsNew);
		}
	    userId = getIntent().getExtras().getString("userId");
	    eventId = getIntent().getExtras().getString("eventId");
	    
	    messageBody = (EditText) findViewById(R.id.messageText);
	    yes = (ImageButton) findViewById(R.id.confirmYes);
	    no = (ImageButton) findViewById(R.id.confirmNo);
	    
	    yes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "yes was pressed");
				Log.i(TAG, messageBody.getText().toString());
				ConfirmSignOutEvent confirmSignOut = new ConfirmSignOutEvent(ConfirmSignOut.this, userId, eventId, messageBody.getText().toString(), messageFromThread);
				confirmSignOut.start();
				pd = new ProgressDialog(ConfirmSignOut.this);
				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		        pd.setMessage("Working...");
		        pd.setIndeterminate(true);
		        pd.setCancelable(false);
		        pd.show();
			}
		});
	    
	    no.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "no was pressed");
				finish();
			}
		});
	}
	
	public void backgrndListner(View v) {
		int [] location= new int[2];
		Log.i(TAG, "You have pressed the background!!!!!!!!!");
		v.getLocationOnScreen(location);
	}
	
    private byte[] getUserEventDetailsObject(){
    	FileInputStream in;
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    	settings.getInt("userEventDataLength", 0);
    	byte[] buffer = new byte[settings.getInt("userEventDataLength", 0)];
		try {
			in = openFileInput("userEventData");
	    	in.read(buffer, 0, buffer.length);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return buffer;
    }
    
    private boolean saveUserEventDetailsObject(byte[] userAccountData) {
		
		SharedPreferences settings =  getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        Log.v(TAG, "Event File writing length = "+userAccountData.length);
        editor.putInt("userEventDataLength", userAccountData.length);
        editor.commit();
        
    	FileOutputStream fos;
		try {
			fos = openFileOutput("userEventData", Context.MODE_PRIVATE);
			fos.write(userAccountData);
	    	fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return true;
    }
	
	private Handler messageFromThread = new Handler() {
    	@SuppressWarnings("unchecked")
		@Override
    	public void handleMessage(Message msg) {
    		Log.i(TAG, "In Handler for diary thead, result= "+msg.what);
    		pd.dismiss();
    		if (msg.what == 200 & msg.arg1 == 197) {
    			LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
    			View layout = inflater.inflate(R.layout.toasttext,null);
    			TextView text = (TextView) layout.findViewById(R.id.text);
    			text.setText("Success! Your SignOut was successful!");
    			Toast toast = new Toast(getApplicationContext());
    			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
    			toast.setDuration(1000);
    			toast.setView(layout);
    			toast.show();
    			Intent data = new Intent();
    			data.putExtra("count", msg.arg2);
    			data.putExtra("eventId", eventId);
    			setResult(Activity.RESULT_OK, data);
    			byte[] eventData = getUserEventDetailsObject();
    			ObjectInputStream ois;
    			LinkedHashMap<String, LinkedHashMap<String, String>> dataEvent;
    			try {
    				ois = new ObjectInputStream(new ByteArrayInputStream(eventData));
    				dataEvent = (LinkedHashMap<String,LinkedHashMap<String, String>>) ois.readObject();
    				//Log.i(TAG, "~~~EVENT DATA = "+dataEvent);
    				if (dataEvent.containsKey(Integer.parseInt(eventId))) {
    					dataEvent.get(Integer.parseInt(eventId)).put("count", ""+msg.arg2);
    		            dataEvent.get(Integer.parseInt(eventId)).put("response", "no");
    		            ByteArrayOutputStream bos = new ByteArrayOutputStream();
    		    	    ObjectOutputStream oos;
    		    		try {
    		    			oos = new ObjectOutputStream(bos);
    		    			oos.writeObject(dataEvent);
    		    		    oos.flush();
    		    		    oos.close();
    		    		    bos.close();
    		    		} catch (IOException e1) {
    		    			e1.printStackTrace();
    		    		}
    		    		byte[] userEventData = bos.toByteArray();
    		    		saveUserEventDetailsObject(userEventData);
    				}
    			} catch (StreamCorruptedException e) {
    				e.printStackTrace();
    			} catch (IOException e) {
    				e.printStackTrace();
    			} catch (ClassNotFoundException e) {
    				e.printStackTrace();
    			}
    			finish();
    		}

    		else {
    			LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
    			View layout = inflater.inflate(R.layout.toasttext,null);
    			TextView text = (TextView) layout.findViewById(R.id.text);
    			text.setText("Error! There was some problem, please SignOut AGAIN!");
    			Toast toast = new Toast(getApplicationContext());
    			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
    			toast.setDuration(1000);
    			toast.setView(layout);
    			toast.show();
    			Log.e(TAG, "ConfirmSignOut: Some error occurred and msg.what = "+msg.what+" and message was not sent");
    		}
    	}
    };

}
