package com.example.snrwlns;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snrwlns.threads.CreateUserAccount;
import com.example.snrwlns.threads.GetEventPicturesObject;

public class ConfirmCreateAccount extends Activity {
	
	/**
	 **  Called when the user confirms sign up.
	 */
	
	private ProgressDialog pd;
	public static final String PREFS_NAME = "SNRWLNS";
	public static final String TAG = "SNRWLNS";
	private EditText editTextFirstName, editTextLastName, editTextPhoneNumber, editTextEmail, editTextPassword;
	private ImageButton confirmButton;
	private TextView myDetailHeaderText;
	private ImageView userImage;
	private String fName, lName, pNumber, email, fileName;
	private SharedPreferences settings;
	
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
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.confirmcreateaccount);
	    
	    settings =  getSharedPreferences(PREFS_NAME, 0);
	    
		fName = getIntent().getExtras().getString("firstName");
		lName = getIntent().getExtras().getString("lastName");
		pNumber = getIntent().getExtras().getString("phoneNumber");
		email = getIntent().getExtras().getString("email");
		fileName = getIntent().getExtras().getString("fileName");
		
		editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
		editTextLastName = (EditText) findViewById(R.id.editTextLastName);
		editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
		editTextEmail = (EditText) findViewById(R.id.editTextEmail);
		editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		confirmButton = (ImageButton) findViewById(R.id.confirmButton);
		userImage = (ImageView) findViewById(R.id.userImage);
		myDetailHeaderText = (TextView) findViewById(R.id.myDetailHeaderText);
		
		myDetailHeaderText.setText(fName+" "+lName);
		editTextFirstName.setText(fName);
		editTextLastName.setText(lName);
		editTextPhoneNumber.setText(pNumber);
		editTextEmail.setText(email);
		editTextPassword.setText(settings.getString("password", ""));
		final File imageFile = new File(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName, options);
		Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
		if(options.outHeight * options.outWidth * 2 >= 16384){
		    double sampleSize = scaleByHeight? options.outHeight / 100: options.outWidth / 100;
		    options.inSampleSize = (int)Math.pow(2d, Math.floor(Math.log(sampleSize)/Math.log(2d)));
		}
		options.inJustDecodeBounds = false; options.inTempStorage = new byte[1024];options.inPurgeable=true;
		Bitmap bm = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName, options);
		
		userImage.setImageBitmap(bm);

		confirmButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pd = new ProgressDialog(ConfirmCreateAccount.this);
				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		        pd.setMessage("Working...");
		        pd.setIndeterminate(true);
		        pd.setCancelable(false);
		        pd.show();
				//Update the details on the server if anything has changed.
				if (!editTextFirstName.getText().toString().equalsIgnoreCase(fName)) {
					Log.i(TAG, editTextFirstName.getText().toString()+"-->"+fName	+"~");
				}
				if (!editTextLastName.getText().toString().equalsIgnoreCase(lName)) {
					Log.i(TAG, editTextLastName.getText().toString()+"-->"+lName+"~");
				}
				if (!editTextPhoneNumber.getText().toString().equalsIgnoreCase(pNumber)) {
					Log.i(TAG, editTextPhoneNumber.getText().toString()+"-->"+pNumber+"~");
				}
				if (!editTextEmail.getText().toString().equalsIgnoreCase(email)) {
					Log.i(TAG, editTextEmail.getText().toString()+"-->"+email+"~");
				}
				if (!editTextPassword.getText().toString().equalsIgnoreCase(settings.getString("password", ""))) {
					Log.i(TAG, editTextPassword.getText().toString()+"-->"+settings.getString("password", "")+"~");
				}
				CreateUserAccount signIn = new CreateUserAccount(ConfirmCreateAccount.this, editTextFirstName.getText().toString(), 
				editTextLastName.getText().toString(), editTextPhoneNumber.getText().toString(), editTextEmail.getText().toString(), 
				byteArrayToHexString(computeHash(editTextPassword.getText().toString())), fileName, imageFile.getAbsolutePath(), messageFromTagThread);
				signIn.start();
			}
		});
	}	
	
	public static byte[] computeHash(String x)	{
		
	    MessageDigest d =null;
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
	  
	private Handler messageFromTagThread = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		Log.i(TAG, "In Handler for SignIn thead, result= "+msg.what+" arg1 = "+msg.arg1);
			pd.dismiss();
    		if (msg.what == 200 & msg.arg1 == 1) {
    			Log.i(TAG, "Object = "+msg.obj);
    			SharedPreferences settings =  getSharedPreferences(PREFS_NAME, 0);
    			SharedPreferences.Editor editor = settings.edit();
    	        editor.putString("userId", (String)msg.obj);
    	        editor.commit();
    	        
    	        GetEventPicturesObject gettingPictures = new GetEventPicturesObject(ConfirmCreateAccount.this, eventHandler);
				gettingPictures.start();
				
    		}
    	}
	};
	
	private Handler eventHandler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		if (msg.what ==200) {
    			Intent mainIntent = new Intent(ConfirmCreateAccount.this, SignIn.class);
    	        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(mainIntent);
    			finish();
    		}
    	}
	};
}
