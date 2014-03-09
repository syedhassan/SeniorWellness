package com.example.snrwlns;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snrwlns.threads.DownloadPictures;
import com.example.snrwlns.threads.PostUserPicture;
import com.example.snrwlns.threads.UpdateUserAccount;

public class MyAccount extends Activity {
	
	/**
	 **  Called when the user confirms sign up.
	 */
	
	private ProgressDialog pd;
	public static final String PREFS_NAME = "SNRWLNS";
	public static final String TAG = "SNRWLNS";
	private EditText editTextFirstName, editTextLastName, editTextPhoneNumber, editTextEmail, editTextPassword;
	private TextView myDetailHeaderText;
	private ImageView saveEventImage;
	private ImageButton backButton;
	private HashMap<String, ArrayList<List<String>>> userAccountData;
	private SharedPreferences settings;
	boolean imageChange = false;
	private static final int SELECT_PICTURE 										= 1;
	private static final int DIALOG_REALLY_EXIT_ID 									= 7;

	@SuppressWarnings("unchecked")
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
	    setContentView(R.layout.myaccount);
	    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	    settings =  getSharedPreferences(PREFS_NAME, 0);
	    byte[] userInfo = getUserInfoObject();
	    {
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new ByteArrayInputStream(userInfo));
				userAccountData = (HashMap<String, ArrayList<List<String>>>) ois.readObject();
				//Log.i(TAG, "~~~USER DATA = "+userAccountData);
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		myDetailHeaderText = (TextView) findViewById(R.id.myDetailHeaderText);
		saveEventImage = (ImageView) findViewById(R.id.saveEventImage);
		editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
		editTextLastName = (EditText) findViewById(R.id.editTextLastName);
		editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
		editTextEmail = (EditText) findViewById(R.id.editTextEmail);
		editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		backButton = (ImageButton) findViewById(R.id.backButton);
		
		myDetailHeaderText.setText(userAccountData.get("friendInfo").get(0).get(0)+" "+userAccountData.get("friendInfo").get(0).get(1));
		editTextFirstName.setText(userAccountData.get("friendInfo").get(0).get(0));
		editTextLastName.setText(userAccountData.get("friendInfo").get(0).get(1));
		editTextPhoneNumber.setText(userAccountData.get("friendInfo").get(0).get(2));
		editTextEmail.setText(userAccountData.get("friendInfo").get(0).get(3));
		editTextPassword.setText(settings.getString("password", ""));
		
		HttpGet httpRequest = null;

        try {
        	String url = userAccountData.get("friendInfo").get(0).get(4);
        	String fileName = url.substring(url.lastIndexOf("/")+1);
        	url = url.replace(" ", "%20");
        	File imageFile = new File(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName);
        	Options options = new Options();
        	if (imageFile.exists()) {
        		
        		Log.i(TAG, "File Name = "+fileName+" and Length = "+imageFile.length());
        		options.inJustDecodeBounds = true;
        		BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName, options);
        		Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
        		if(options.outHeight * options.outWidth * 2 >= 16384){
        		    double sampleSize = scaleByHeight? options.outHeight / 100: options.outWidth / 100;
        		    options.inSampleSize = (int)Math.pow(2d, Math.floor(Math.log(sampleSize)/Math.log(2d)));
        		}
        		options.inJustDecodeBounds = false; options.inTempStorage = new byte[1024]; options.inPurgeable=true; 
        		Bitmap bm = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        		int tempW = bm.getWidth();
        		int tempH = bm.getHeight();

        		if (tempW>tempH) {
        		     Matrix mtx = new Matrix();
        		     bm = Bitmap.createBitmap(Bitmap.createBitmap(bm, 0, 0, tempW, tempH, mtx, true));
        		}

        		saveEventImage.setImageBitmap(bm);
        	}
        	else {
	        	httpRequest = new HttpGet(url);
	        	DefaultHttpClient httpclient = new DefaultHttpClient();
	            HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);

	            HttpEntity entity = response.getEntity();
	            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity); 
	            InputStream instream = bufHttpEntity.getContent();
	            options.inJustDecodeBounds = true;
        		BitmapFactory.decodeStream(instream, null, options);
        		Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
        		if(options.outHeight * options.outWidth * 2 >= 16384){
        		    double sampleSize = scaleByHeight? options.outHeight / 100: options.outWidth / 100;
        		    options.inSampleSize = (int)Math.pow(2d, Math.floor(Math.log(sampleSize)/Math.log(2d)));
        		}
        		options.inJustDecodeBounds = false; options.inTempStorage = new byte[1024];  options.inPurgeable=true;
	            Bitmap bm = BitmapFactory.decodeStream(instream, null, options);
	            saveEventImage.setImageBitmap(bm);
				DownloadPictures pictures = new DownloadPictures(this, url, fileName);
				pictures.start();
        	}
        	
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		saveEventImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
			}
		});
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pd = new ProgressDialog(MyAccount.this);
				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		        pd.setMessage("Working...");
		        pd.setIndeterminate(true);
		        pd.setCancelable(false);
		        pd.show();
				//Update the details on the server if anything has changed.
				boolean changeFlag = false;
				if (!editTextFirstName.getText().toString().equalsIgnoreCase(userAccountData.get("friendInfo").get(0).get(0))) {
					Log.i(TAG, editTextFirstName.getText().toString()+"-->"+userAccountData.get("friendInfo").get(0).get(0)+"~");
					changeFlag = true;
				}
				if (!editTextLastName.getText().toString().equalsIgnoreCase(userAccountData.get("friendInfo").get(0).get(1))) {
					Log.i(TAG, editTextLastName.getText().toString()+"-->"+userAccountData.get("friendInfo").get(0).get(1)+"~");
					changeFlag = true;
				}
				if (!editTextPhoneNumber.getText().toString().equalsIgnoreCase(userAccountData.get("friendInfo").get(0).get(2))) {
					Log.i(TAG, editTextPhoneNumber.getText().toString()+"-->"+userAccountData.get("friendInfo").get(0).get(2)+"~");
					changeFlag = true;
				}
				if (!editTextEmail.getText().toString().equalsIgnoreCase(userAccountData.get("friendInfo").get(0).get(3))) {
					Log.i(TAG, editTextEmail.getText().toString()+"-->"+userAccountData.get("friendInfo").get(0).get(3)+"~");
					changeFlag = true;
				}
				if (!editTextPassword.getText().toString().equalsIgnoreCase(settings.getString("password", ""))) {
					Log.i(TAG, editTextPassword.getText().toString()+"-->"+settings.getString("password", "")+"~");
					changeFlag = true;
				}
				if (imageChange) {
					PostUserPicture postPicture = new PostUserPicture(MyAccount.this, imageFileName, imageFilePath, settings.getString("userId", "100"));
					postPicture.start();
					String url = userAccountData.get("friendInfo").get(0).get(4);
        			url = url.substring(0, url.lastIndexOf("/")+1) + imageFileName;
        			userAccountData.get("friendInfo").get(0).set(4, url);
        			saveUserInfoObject(userAccountData);
				}
				if (changeFlag){
					UpdateUserAccount update = new UpdateUserAccount(MyAccount.this, settings.getString("userId", "100"), editTextFirstName.getText().toString(), editTextLastName.getText().toString(),
							editTextPhoneNumber.getText().toString(), editTextEmail.getText().toString(), byteArrayToHexString(computeHash(editTextPassword.getText().toString())), 
							messageFromThread);
					update.start();
					finish();
				}
				else {
					finish();
					pd.dismiss();
				}
			}
		});
	}
	
	protected String imageFileName, imageFilePath;
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK) {
	        if (requestCode == SELECT_PICTURE) {
	        	imageChange = true;
	            Uri selectedImageUri = data.getData();
	            String selectedImagePath = getPath(selectedImageUri);
	            Log.i(TAG, "Selected Image path = "+selectedImagePath);
	            imageFilePath = selectedImagePath;
	            imageFileName = selectedImagePath.substring(selectedImagePath.lastIndexOf("/")+1);
				File originalFile = new File(imageFilePath);
			    File destinationFile = new File(Environment.getExternalStorageDirectory()+"/SeniorWellness/"+imageFileName);
			    Log.i(TAG, "Original file path = "+originalFile.getAbsolutePath()+" Destination File Path = "+destinationFile.getAbsolutePath());
			    Log.i(TAG, "Original File length = "+originalFile.length()+" "+originalFile.exists());
			    BitmapFactory.Options options = new BitmapFactory.Options();
			    if (!destinationFile.getAbsolutePath().equalsIgnoreCase(originalFile.getAbsolutePath())) {
			    	try {
						FileUtils.copyFile(originalFile, destinationFile);
					} catch (IOException e1) {
						Log.e(TAG, "File was not copied");
						e1.printStackTrace();
					}
				    Log.i(TAG, "Copied file length = "+destinationFile.length()+" original file length = "+originalFile.length());
		            
		            options.inJustDecodeBounds = true;
	        		BitmapFactory.decodeFile(selectedImagePath, options);
	        		Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
	        		if(options.outHeight * options.outWidth * 2 >= 16384){
	        		    double sampleSize = scaleByHeight? options.outHeight / 100: options.outWidth / 100;
	        		    options.inSampleSize = (int)Math.pow(2d, Math.floor(Math.log(sampleSize)/Math.log(2d)));
	        		}
	        		options.inJustDecodeBounds = false; options.inTempStorage = new byte[1024];  options.inPurgeable=true;
	        		Log.i(TAG, "Sample size = "+options.inSampleSize);
	        		if (options.inSampleSize > 16) {
	        			options.inSampleSize = 16;
	        		}
			    }
			    else {
			    	Log.i(TAG, "No need to copy any files as the file is already present in the SeniorWellness folder");
			    }


	            Bitmap bm = BitmapFactory.decodeFile(selectedImagePath, options);
	            
//	            FileOutputStream out = null;
//				try {
//					out = new FileOutputStream(destinationFile);
//				} 
//				catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}
//				
//	            Log.i(TAG, "bm = "+bm);
//	            int tempW = bm.getWidth();int tempH = bm.getHeight();
//
//        		if (tempW>tempH) {
//        		     Matrix mtx = new Matrix();
//        		     bm = Bitmap.createBitmap(Bitmap.createBitmap(bm, 0, 0, tempW, tempH, mtx, true));
//        		     if (destinationFile.length() > 102000) {
//        		    	 bm.compress(Bitmap.CompressFormat.JPEG, 40, out);
//        		     }
//        		     else {
//        		    	 bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
//        		     }
//        		}
        		saveEventImage.setImageBitmap(bm);
	        }
	    }
	}
	
	public String getPath(Uri uri) {
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	
	private Handler messageFromThread = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		Log.i(TAG, "In Handler for SignIn thead, result= "+msg.what+" arg1 = "+msg.arg1);
    		if (pd.isShowing())
    			pd.dismiss();
    		if (msg.what == 200 & msg.arg1 == 1) {
    			Log.i(TAG, "Object = "+msg.obj);
    			userAccountData.get("friendInfo").get(0).set(0, editTextFirstName.getText().toString());
    			userAccountData.get("friendInfo").get(0).set(1, editTextLastName.getText().toString());
    			userAccountData.get("friendInfo").get(0).set(2, editTextPhoneNumber.getText().toString());
    			userAccountData.get("friendInfo").get(0).set(3, editTextEmail.getText().toString());
    			saveUserInfoObject(userAccountData);
    			SharedPreferences.Editor editor = settings.edit();
				editor.putString("password", editTextPassword.getText().toString());
				editor.commit();
    			finish();
    		}
    		else {
    			LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("Error occurred in udpating your information. Please try again!!!");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();
//    			Toast.makeText(MyAccount.this, "Error occurred in udpating your information. Please try again!!!", 1000).show();
    		}
    	}
	};
	
	@Override
	public void onBackPressed() {
		if (imageChange) {
			showDialog(DIALOG_REALLY_EXIT_ID);
		}
		else {
			finish();
		}
	    return;
	}

    
    @Override
    protected Dialog onCreateDialog(int id) {
        final Dialog dialog;
        switch(id) {
        case DIALOG_REALLY_EXIT_ID:
            dialog = new AlertDialog.Builder(this).setMessage(
                                "Your image was changed would you like to save it before exiting?")
            .setCancelable(false)
            .setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                	SharedPreferences settings =  MyAccount.this.getSharedPreferences(PREFS_NAME, 0);
                	PostUserPicture postPicture = new PostUserPicture(MyAccount.this, imageFileName, imageFilePath, settings.getString("userId", "100"));
                	String url = userAccountData.get("friendInfo").get(0).get(4);
        			url = url.substring(0, url.lastIndexOf("/")+1) + imageFileName;
        			userAccountData.get("friendInfo").get(0).set(4, url);
        			saveUserInfoObject(userAccountData);
					postPicture.start();
					dialog.cancel();
                    MyAccount.this.finish();
                }
            })
            .setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    MyAccount.this.finish();
                }
            }).create();
            break;
        default:
            dialog = null;
        }
        return dialog;
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
	
    private byte[] getUserInfoObject(){
    	FileInputStream in;
    	
    	settings.getInt("userAccountDataLength", 0);
    	byte[] buffer = new byte[settings.getInt("userAccountDataLength", 0)];
		try {
			in = openFileInput("userAccountData");
	    	in.read(buffer, 0, buffer.length);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return buffer;
    }
    
    private boolean saveUserInfoObject(HashMap<String, ArrayList<List<String>>> userInfo) {
    	
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(userInfo);
		    oos.flush();
		    oos.close();
		    bos.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    
	    byte[] userAccountData = bos.toByteArray();
	    
		
		SharedPreferences settings =  MyAccount.this.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        Log.v(TAG, "SAVING LENGTH = "+userAccountData.length);
        editor.putInt("userAccountDataLength", userAccountData.length);
        editor.commit();
        
    	FileOutputStream fos;
		try {
			fos = MyAccount.this.openFileOutput("userAccountData", Context.MODE_PRIVATE);
			fos.write(userAccountData);
	    	fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return true;
    }

}
