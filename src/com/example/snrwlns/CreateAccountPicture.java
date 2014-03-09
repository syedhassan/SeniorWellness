package com.example.snrwlns;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CreateAccountPicture extends Activity {
	
	
	public static final String PREFS_NAME 											= "SNRWLNS";
	public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE 					= 0;
	public static final int MEDIA_IMAGE												= 1;
	public static final String TAG = "SNRWLNS";
	private String fName, lName, pNumber, email, pass;
	
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
		
		setContentView(R.layout.addpictureaccount);
		fName = getIntent().getExtras().getString("firstName");
		lName = getIntent().getExtras().getString("lastName");
		pNumber = getIntent().getExtras().getString("phoneNumber");
		email = getIntent().getExtras().getString("email");
		pass = getIntent().getExtras().getString("password");
		
		ImageButton startCamera = (ImageButton) findViewById(R.id.startCamera);
		
		startCamera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String fileName = System.currentTimeMillis() + ".jpg";
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("imageFileName", fileName);
				editor.commit();
    	    	File externalDirectory = new File(Environment.getExternalStorageDirectory()+"/tmp");
    	    	externalDirectory.mkdirs();
    	    	File pictureFile = new File( externalDirectory+"/"+fileName );
    	    	Log.i(TAG, "FileName = "+pictureFile.getName()+" location = "+pictureFile.getAbsolutePath());
		   	    Uri outputFileUri = Uri.fromFile( pictureFile );
		   	    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
		   	    intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );
		   	    intent.putExtra( MediaStore.EXTRA_SHOW_ACTION_ICONS, true);
		   	    startActivityForResult( intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE );
			}
		});
		
		ImageButton skipCamera = (ImageButton) findViewById(R.id.skipCamera);
		skipCamera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String fileName = "profiledefault.png";
				File directory = new File(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/");
				directory.mkdirs();
				File outFile = new File(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName);
				Resources resources = getResources();
				try {
					InputStream defaultFile = resources.getAssets().open(fileName);
					FileOutputStream out = new FileOutputStream(outFile);
					Bitmap bitmap = BitmapFactory.decodeStream(defaultFile);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
				} 
				catch (IOException e1) {
					e1.printStackTrace();
				}
				
				Intent confirmCreateAccount = new Intent(CreateAccountPicture.this, ConfirmCreateAccount.class);
    	    	Bundle bundle = new Bundle();
		        bundle.putString("firstName", fName);
		        bundle.putString("lastName", lName);
		        bundle.putString("phoneNumber", pNumber);
		        bundle.putString("email", email);
		        bundle.putString("password", pass);
		        bundle.putString("fileName", outFile.getName());
		        bundle.putString("filePath", outFile.getAbsolutePath());
		        confirmCreateAccount.putExtras(bundle);
		        startActivity(confirmCreateAccount);
			}
		});
	}
	
	public static byte[] computeHash(String x) {
		
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	File externalDirectory = new File(Environment.getExternalStorageDirectory()+"/SeniorWellness");
    	externalDirectory.mkdirs();
    	if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
    	    if (resultCode == RESULT_OK) {
    	        //use imageUri here to access the image
    	    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    	    	String fileName = settings.getString("imageFileName", "");
    	    	SharedPreferences.Editor editor = settings.edit();
    	    	editor.remove("imageFileName");
    	    	editor.commit();
    	    	File file = new File(Environment.getExternalStorageDirectory()+"/tmp/"+fileName);
    	    	File outFile = new File(Environment.getExternalStorageDirectory()+"/SeniorWellness/"+fileName);
    	    	Log.d(TAG, "File Name = "+fileName+" location = "+file.getAbsolutePath()+" Size = "+file.length());
    	    	
				try {
					FileOutputStream out = new FileOutputStream(outFile);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;
	        		BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/tmp/"+fileName, options);
	        		Boolean scaleByHeight = Math.abs(options.outHeight - 1024) >= Math.abs(options.outWidth - 756);
	        		if(options.outHeight * options.outWidth * 2 >= 16384){
	        		    double sampleSize = scaleByHeight? options.outHeight / 1024: options.outWidth / 756;
	        		    options.inSampleSize = (int)Math.pow(2d, Math.floor(Math.log(sampleSize)/Math.log(2d)));
	        		}
	        		options.inJustDecodeBounds = false; options.inTempStorage = new byte[1024];  options.inPurgeable=true;
					Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
					int tempW = bitmap.getWidth();
	        		int tempH = bitmap.getHeight();
        			if (tempW>tempH) {	
	        		     Matrix mtx = new Matrix();
	        		     bitmap = Bitmap.createBitmap(Bitmap.createBitmap(bitmap, 0, 0, tempW, tempH, mtx, true));
	        		}
					bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
    	    	Intent confirmCreateAccount = new Intent(CreateAccountPicture.this, ConfirmCreateAccount.class);
    	    	confirmCreateAccount.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	    	Bundle bundle = new Bundle();
		        bundle.putString("firstName", fName);
		        bundle.putString("lastName", lName);
		        bundle.putString("phoneNumber", pNumber);
		        bundle.putString("email", email);
		        bundle.putString("password", pass);
		        bundle.putString("fileName", file.getName());
		        bundle.putString("filePath", file.getAbsolutePath());
		        confirmCreateAccount.putExtras(bundle);
		        startActivity(confirmCreateAccount);

    	    } else if (resultCode == RESULT_CANCELED) {
    	    	LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("Picture was not taken");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();
    	    } else {
    	    	LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("Picture was not taken");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();
    	    }
    	}
    }
}