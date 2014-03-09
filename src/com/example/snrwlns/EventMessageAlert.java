package com.example.snrwlns;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

public class EventMessageAlert extends Activity{
	
	public static final String TAG = "SNRWLNS";
	
	public void onCreate(Bundle savedInstanceState) {
		 
		 super.onCreate(savedInstanceState);
	     
	     requestWindowFeature(Window.FEATURE_NO_TITLE);
	     this.setContentView(R.layout.eventmessagealert);
	     updateView();
	}
	
	private void updateView () {
		TextView webText = (TextView) findViewById(R.id.webText);
	    String eventName = this.getIntent().getExtras().getString("eventName");
	    webText.setText("Please sign up for "+eventName+" in order to leave a note");
		 
	    ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
	    backButton.setOnClickListener(new OnClickListener() {
	    	
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
		
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	   updateView();
	}
}
