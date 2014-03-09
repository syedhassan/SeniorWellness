package com.example.snrwlns;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snrwlns.threads.GetEventPicturesObject;
import com.example.snrwlns.threads.SignInAccount;

public class SignIn extends Activity {
	
	public static final String PREFS_NAME 													= "SNRWLNS";
	public static final String TAG 															= "SNRWLNS";
	public static final int DIALOG_REALLY_EXIT_ID 											= 12;
	private ProgressDialog pd;
	private EditText editTextEmailAddress, editTextPassword;
	private ImageButton signInButton, createAccountButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		if (!isTaskRoot()) {
		    Intent intent = getIntent();
		    String intentAction = intent.getAction();
		    if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) &&
		            intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
		        finish();
		    }
		}
		super.onCreate(savedInstanceState);

		Intent myIntent = new Intent(this, AlarmEventComingUp.class);
		myIntent.putExtra("extraData", "somedata");
		startService(myIntent);
		
		//Be default make this as demo version.
		SharedPreferences settings =  getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
        editor.putString("serverLink", "demoServer");
        editor.commit();
		
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			
		if (isNetworkAvailable() & isGmailAccountAvailable()) {
			Log.i(TAG, "UserId ="+settings.getString("userId", "100"));
			if (!settings.getString("userId", "100").equalsIgnoreCase("100")) {
				GetEventPicturesObject gettingPictures = new GetEventPicturesObject(SignIn.this, eventHandler);
				gettingPictures.start();
			}
			else {
				setContentView(R.layout.welcome);
				editTextEmailAddress = (EditText) findViewById(R.id.editTextEmailAddress);
				editTextPassword = (EditText) findViewById(R.id.editTextPassword);
				signInButton = (ImageButton) findViewById(R.id.signInButton);
				createAccountButton = (ImageButton) findViewById(R.id.createAccountButton);
				
				signInButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (editTextEmailAddress.getText().length() > 0 & editTextPassword.getText().length() > 0) {
							pd = new ProgressDialog(SignIn.this);
							pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					        pd.setMessage("Working...");
					        pd.setIndeterminate(true);
					        pd.setCancelable(false);
					        pd.show();
					        RadioButton realServer = (RadioButton) findViewById(R.id.realServer);
					        SharedPreferences settings =  getSharedPreferences(PREFS_NAME, 0);
					        if (realServer.isChecked()) {
					        	SharedPreferences.Editor editor = settings.edit();
						        editor.putString("serverLink", "realServer");
						        editor.commit();
					        }
					        else {
					        	SharedPreferences.Editor editor = settings.edit();
						        editor.putString("serverLink", "demoServer");
						        editor.commit();
					        }
					        
							SignInAccount signIn = new SignInAccount(SignIn.this, editTextEmailAddress.getText().toString(), 
									byteArrayToHexString(computeHash(editTextPassword.getText().toString()))
									, messageFromTagThread);
							signIn.start();
						}
						else {
							if (editTextEmailAddress.getText().length() <= 0) {
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
							else {
								editTextEmailAddress.setText("");
								editTextPassword.setText("");
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
				
				createAccountButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent mainIntent = new Intent(SignIn.this, CreateAccount.class);
						mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(mainIntent);
						RadioButton realServer = (RadioButton) findViewById(R.id.realServer);
				        SharedPreferences settings =  getSharedPreferences(PREFS_NAME, 0);
						if (realServer.isChecked()) {
				        	SharedPreferences.Editor editor = settings.edit();
					        editor.putString("serverLink", "realServer");
					        editor.commit();
				        }
				        else {
				        	SharedPreferences.Editor editor = settings.edit();
					        editor.putString("serverLink", "demoServer");
					        editor.commit();
				        }
					}
				});
			}
		}
		else {
			LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
			View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
			TextView text = (TextView) layout.findViewById(R.id.text);
			text.setText("Either you don't have a gmail account setup or you are not connected to the internet. Please check your settings and try again!");
			Toast toast = new Toast(getApplicationContext());
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast.setDuration(5000);
			toast.setView(layout);
			toast.show();
			finish();
		}
	}
	
	public static byte[] computeHash(String x)  {
	    MessageDigest d = null;
	    try {
			d = MessageDigest.getInstance("SHA-1");
		} 
	    catch (NoSuchAlgorithmException e) {
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
	
	private Handler eventHandler = new Handler() {
	    	@Override
	    	public void handleMessage(Message msg) {
	    		if (msg.what ==200) {
				Intent mainIntent = new Intent(SignIn.this, Main.class);
				startActivity(mainIntent);
				finish();
	    	}
	    }
	};
	  
	private Handler messageFromTagThread = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		Log.i(TAG, "SIGN IN : In Handler for SignIn thead, result= "+msg.what+" arg1 = "+msg.arg1);
			pd.dismiss();
    		if (msg.what == 200 & msg.arg1 == 1) {
    			Log.i(TAG, "Object = "+msg.obj);
    			SharedPreferences settings =  getSharedPreferences(PREFS_NAME, 0);
    			SharedPreferences.Editor editor = settings.edit();
		        editor.putString("password", editTextPassword.getText().toString());
    	        editor.putString("userId", (String)msg.obj);
    	        editor.commit();
    	        
    			Intent mainIntent = new Intent(SignIn.this, Main.class);
    			startActivity(mainIntent);
    			finish();
    		}
    		else if (msg.what == 200 & ( msg.arg1 == 2 | msg.arg1 == 3)) {
    			LayoutInflater inflater = LayoutInflater.from(SignIn.this);
				View layout = inflater.inflate(R.layout.toasttext,
				                               (ViewGroup) findViewById(R.id.toast_layout_root));
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("The combination of your Email Address and Password was incorrect. Please try again!!!");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();

    		}	
    		else {
    			LayoutInflater inflater = LayoutInflater.from(SignIn.this);
				View layout = inflater.inflate(R.layout.toasttext,
				                               (ViewGroup) findViewById(R.id.toast_layout_root));
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("An error occurred. Please try again!!!");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(2000);
				toast.setView(layout);
				toast.show();
    			Log.e(TAG, "Value of msg.what = "+msg.what+" arg1 = "+msg.arg1+" obj = "+msg.obj);
    		}
    	}
	};
	
	private boolean isNetworkAvailable() {
		//return true; 
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	    
	}
	
	private boolean isGmailAccountAvailable() {
		Account[] accounts = AccountManager.get(this).getAccounts();
		String possibleEmail = null;
		for (Account account : accounts) {
		  possibleEmail = account.name;
		  if (account.type.equalsIgnoreCase("com.google") & (account.name.contains("@gmail.com") | account.name.contains("@google.com"))) {
			  possibleEmail = account.name;
			  Log.i(TAG, "Account name ="+possibleEmail+"~"+account.type);
			  return true;
		  }
		}
		return false;
	}
	
	@Override
	public void onBackPressed() {
		showDialog(DIALOG_REALLY_EXIT_ID);
	    return;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "SIGNIn On REsume");
		SharedPreferences settings =  getSharedPreferences(PREFS_NAME, 0);
		Log.i(TAG, "UserId ="+settings.getString("userId", "100"));
		if (!settings.getString("userId", "100").equalsIgnoreCase("100")) {
			Intent mainIntent = new Intent(SignIn.this, Main.class);
			startActivity(mainIntent);
			finish();
		}
	}

    @Override
    protected Dialog onCreateDialog(int id) {
        final Dialog dialog;
        switch(id) {
        case DIALOG_REALLY_EXIT_ID:
            dialog = new AlertDialog.Builder(this).setMessage(
                                "Are you sure you want to exit?")
            .setCancelable(false)
            .setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SignIn.this.finish();
                }
            })
            .setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            }).create();
            break;
        default:
            dialog = null;
        }
        return dialog;
    }
}