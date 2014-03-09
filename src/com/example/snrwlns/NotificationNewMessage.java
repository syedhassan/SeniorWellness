package com.example.snrwlns;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificationNewMessage extends FragmentActivity{

	public static final String PREFS_NAME 											= "SNRWLNS";
	public static final String TAG													= "SNRWLNS";
	private int eventPosition;	

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notificationnewmessage);
		
		String msg1 = getIntent().getExtras().getString("message1");
		String msg2 = getIntent().getExtras().getString("message2");
		eventPosition = getIntent().getExtras().getInt("position");
		
		TextView message1 = (TextView) findViewById(R.id.message1);
		TextView message2 = (TextView) findViewById(R.id.message2);
		message1.setText(msg1);
		message2.setText(msg2);
		
		ImageView detailsButton  = (ImageView) findViewById(R.id.detailsButton);
		detailsButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				SharedPreferences settings =  NotificationNewMessage.this.getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("newNotificationMessage", true);
				editor.putInt("newNotificationPosition", eventPosition);
				editor.commit();
				

				Intent mainIntent = new Intent(NotificationNewMessage.this, Main.class);
				mainIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    			startActivity(mainIntent);
    			finish();
			}
		});
		
		ImageView okButton = (ImageView) findViewById(R.id.okButton);
		okButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
