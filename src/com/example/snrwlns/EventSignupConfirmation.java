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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snrwlns.threads.ConfirmSignUpEvent;
import com.example.snrwlns.threads.GetMeDataObject;

public class EventSignupConfirmation extends Activity{
	
	public static final String TAG 													= "SNRWLNS";
	public static final String PREFS_NAME 											= "SNRWLNS";
	private String eventId, userId;
	private ProgressDialog pd;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Confirminggggg");
	    
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    this.setContentView(R.layout.eventsignupconfirmfragment);
	    
	    LinearLayout headerLayout = (LinearLayout) findViewById(R.id.headerLayout);
	    if (this.getResources().getConfiguration().orientation == 1) {
	    	
		}
		else {
			LinearLayout.LayoutParams paramsNew = new LinearLayout.LayoutParams(529, LinearLayout.LayoutParams.FILL_PARENT);
			paramsNew.leftMargin = 8;
			paramsNew.topMargin = 32;
			headerLayout.setLayoutParams(paramsNew);
		}
	    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		userId = getIntent().getExtras().getString("userId");
		eventId = getIntent().getExtras().getString("eventId");
		Log.i(TAG, "Event id = "+eventId+" User id = "+userId);
	
		ImageButton confirm = (ImageButton) findViewById(R.id.confirmSignUp);
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pd = new ProgressDialog(EventSignupConfirmation.this);
				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pd.setMessage("Working...");
				pd.setIndeterminate(true);
				pd.setCancelable(false);
				pd.show();
				EditText textEntered = (EditText) findViewById(R.id.messageText);
				ConfirmSignUpEvent confirm = new ConfirmSignUpEvent(EventSignupConfirmation.this, userId, eventId, textEntered.getText().toString(), messageFromThread);
				confirm.start();
				
			}
		});
		 
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
    		Log.i(TAG, "In Handler for EventSignUpConfirmation, result= "+msg.what);
    		if (pd != null)
    			pd.dismiss();
    		if (msg.what == 200 & msg.arg1 == 196) {
    			Log.i(TAG, "Count = "+msg.arg2);
    			LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("Success! Your message has been sent!");
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
    			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    			GetMeDataObject obj = new GetMeDataObject(null, EventSignupConfirmation.this, settings.getString("userId", "100"), null);
    			obj.start();
    			ObjectInputStream ois;
    			LinkedHashMap<Integer, LinkedHashMap<String, String>> dataEvent;
    			try {
    				ois = new ObjectInputStream(new ByteArrayInputStream(eventData));
    				dataEvent = (LinkedHashMap<Integer, LinkedHashMap<String, String>>) ois.readObject();
    				if (dataEvent.containsKey(Integer.parseInt(eventId))) {
    					dataEvent.get(Integer.parseInt(eventId)).put("count", ""+msg.arg2);
    		            dataEvent.get(Integer.parseInt(eventId)).put("response", "yes");
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
    			} 
    			catch (StreamCorruptedException e) {
    				e.printStackTrace();
    			} 
    			catch (IOException e) {
    				e.printStackTrace();
    			} 
    			catch (ClassNotFoundException e) {
    				e.printStackTrace();
    			}
    			finish();
    		}

    		else {
    			LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("Error! Your message WAS NOT sent!");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();
    			Log.e(TAG, "EventSignupConfirmationFragment: Some error occurred and msg.what = "+msg.what+" and message was not sent");
    		}
    	}
	};
}
