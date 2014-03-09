package com.example.snrwlns;

import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CreateAccount extends Activity {
	
	public static final String PREFS_NAME = "SNRWLNS";
	public static final String TAG = "SNRWLNS";
	private EditText editTextFirstName, editTextLastName, editTextPhoneNumber, editTextEmail, editTextPassword, editTextConfirmPassword;
	private ImageButton next;
	
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
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.createaccount);
		editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
		editTextLastName = (EditText) findViewById(R.id.editTextLastName);
		editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
		editTextEmail = (EditText) findViewById(R.id.editTextEmail);
		editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
		next = (ImageButton) findViewById(R.id.next);
		
		next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (editTextFirstName.getText().length() > 0 & editTextLastName.getText().length() > 0 &
						editTextPhoneNumber.getText().length() > 0 & editTextEmail.getText().length() > 0 &
						editTextPassword.getText().length() > 0 & editTextConfirmPassword.getText().length() > 0) {
					//Do the sign in
					if (editTextPassword.getText().toString().equals(editTextConfirmPassword.getText().toString())) {
						if (editTextPhoneNumber.getText().length() >= 10) {
							SharedPreferences settings =  getSharedPreferences(PREFS_NAME, 0);
					        SharedPreferences.Editor editor = settings.edit();
					        editor.putString("password", editTextPassword.getText().toString());
					        editor.commit();
					        Intent createAccountPicture = new Intent(CreateAccount.this, CreateAccountPicture.class);
					        createAccountPicture.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					        Bundle bundle = new Bundle();
					        bundle.putString("firstName", editTextFirstName.getText().toString());
					        bundle.putString("lastName", editTextLastName.getText().toString());
					        bundle.putString("phoneNumber", editTextPhoneNumber.getText().toString());
					        bundle.putString("email", editTextEmail.getText().toString());
					        bundle.putString("password", byteArrayToHexString(computeHash(editTextPassword.getText().toString())));
					        createAccountPicture.putExtras(bundle);
					        startActivity(createAccountPicture);
						}
						else {
							LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
							View layout = inflater.inflate(R.layout.toasttext,
							                               (ViewGroup) findViewById(R.id.toast_layout_root));
							TextView text = (TextView) layout.findViewById(R.id.text);
							text.setText("Phone number has to contain atleast 10 digits!");
							Toast toast = new Toast(getApplicationContext());
							toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
							toast.setDuration(1000);
							toast.setView(layout);
							toast.show();
						}
					}
					else {
						LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
						View layout = inflater.inflate(R.layout.toasttext,
						                               (ViewGroup) findViewById(R.id.toast_layout_root));
						TextView text = (TextView) layout.findViewById(R.id.text);
						text.setText("Passwords do not match!");
						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
						toast.setDuration(1000);
						toast.setView(layout);
						toast.show();
					}
					
				}
				else {
					if (editTextFirstName.getText().length() <= 0) {
						LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
						View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
						TextView text = (TextView) layout.findViewById(R.id.text);
						text.setText("Please enter your First Name");
						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
						toast.setDuration(1000);
						toast.setView(layout);
						toast.show();
					}
					else if (editTextLastName.getText().length() <= 0) {
						LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
						View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
						TextView text = (TextView) layout.findViewById(R.id.text);
						text.setText("Please enter your Last Name");
						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
						toast.setDuration(1000);
						toast.setView(layout);
						toast.show();
					}
					else if (editTextPhoneNumber.getText().length() <= 0) {
						LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
						View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
						TextView text = (TextView) layout.findViewById(R.id.text);
						text.setText("Please enter your Phone Number");
						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
						toast.setDuration(1000);
						toast.setView(layout);
						toast.show();
					}
					else if (editTextEmail.getText().length() <= 0) {
						LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
						View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
						TextView text = (TextView) layout.findViewById(R.id.text);
						text.setText("Please enter your Email Address");
						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
						toast.setDuration(1000);
						toast.setView(layout);
						toast.show();
					}
					else if (editTextPassword.getText().length() <= 0) {
						LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
						View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
						TextView text = (TextView) layout.findViewById(R.id.text);
						text.setText("Please enter your Password");
						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
						toast.setDuration(1000);
						toast.setView(layout);
						toast.show();
					}
					else if (editTextConfirmPassword.getText().length() <= 0) {
						LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
						View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
						TextView text = (TextView) layout.findViewById(R.id.text);
						text.setText("Please confirm your Password");
						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
						toast.setDuration(1000);
						toast.setView(layout);
						toast.show();
					}
					else {
						editTextFirstName.setText("");editTextPhoneNumber.setText("");editTextEmail.setText("");
						editTextLastName.setText("");editTextPassword.setText("");editTextConfirmPassword.setText("");
						LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
						View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
						TextView text = (TextView) layout.findViewById(R.id.text);
						text.setText("Please try again!");
						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
						toast.setDuration(1000);
						toast.setView(layout);
						toast.show();
					}
				}
				
			}
		});
		Log.i(TAG, "Text size = "+editTextFirstName.getTextSize());
		Log.i(TAG, "Text size = "+editTextLastName.getTextSize());
		Log.i(TAG, "Text size = "+editTextPhoneNumber.getTextSize());
		Log.i(TAG, "Text size = "+editTextEmail.getTextSize());
		Log.i(TAG, "Text size = "+editTextPassword.getTextSize());
		Log.i(TAG, "Text size = "+editTextConfirmPassword.getTextSize());
	}
	
	public static byte[] computeHash(String x)	{
		
	    java.security.MessageDigest d =null;
	    try {
			d = java.security.MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	    d.reset();
	    d.update(x.getBytes());
	    return  d.digest();
	}
	  
	public static String byteArrayToHexString(byte[] b){
	    StringBuffer sb = new StringBuffer(b.length * 2);
	    for (int i = 0; i < b.length; i++){
	        int v = b[i] & 0xff;
	        if (v < 16) {
	          sb.append('0');
	        }
	        sb.append(Integer.toHexString(v));
	     }
	     return sb.toString().toUpperCase();
	}
}