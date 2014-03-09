package com.example.snrwlns;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.snrwlns.threads.SendMessageToServer;

public class EventMessages extends Activity{
	
	public static final String TAG = "SNRWLNS";
	private ProgressDialog pd;
	
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     requestWindowFeature(Window.FEATURE_NO_TITLE);
	     this.setContentView(R.layout.eventmessagefragment);
	    
	     this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	     final String userId = getIntent().getExtras().getString("userId");
	     final String eventId = getIntent().getExtras().getString("eventId");
	     Log.i(TAG, "Event Id = "+eventId+" User Id = "+userId);
	     
	     final EditText messageText = (EditText) findViewById(R.id.messageText);
	     ImageButton sendMessageButton = (ImageButton) findViewById(R.id.sendMessageButton);
	     sendMessageButton.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				pd = new ProgressDialog(EventMessages.this);
				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		        pd.setMessage("Working...");
		        pd.setIndeterminate(true);
		        pd.setCancelable(false);
		        pd.show();
				Log.i(TAG, "You are sending = "+messageText.getText().toString());
				SendMessageToServer send = new SendMessageToServer(EventMessages.this, userId, eventId, messageText.getText().toString(), messageFromThread);
				send.start();
			}
		});
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	 
	private Handler messageFromThread = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		Log.i(TAG, "In Handler for SignIn thead, result= "+msg.what+" arg1 = "+msg.arg1);
    		if (pd != null)
    			pd.dismiss();
    		if (msg.what == 200 & msg.arg1 == 1) {
    			LayoutInflater inflater = LayoutInflater.from(EventMessages.this);
				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("Your message was successfully sent to the server");
				Toast toast = new Toast(EventMessages.this);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();
    			finish();
    		}
    		else {
    			LayoutInflater inflater = LayoutInflater.from(EventMessages.this);
				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("Error occurred in sending message. Please try again!!!");
				Toast toast = new Toast(EventMessages.this);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();
    		}
    	}
	};
}
