package com.example.snrwlns.threads;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;

import com.example.snrwlns.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class BookMarkEvent extends Thread implements Runnable {
	
	protected String userId, eventId;
	protected Handler handler;
	protected boolean bookMark;
	protected Context context;
	protected int messageId;
	protected static final String serverLink = "http://arcweb.arctry.com:8080/snrwlns_server/Main";
	protected static final String demoServerLink = "http://arcweb.arctry.com:8080/snrwlns_server1/Main";
	public static final String PREFS_NAME = "SNRWLNS";
	
	protected static final String TAG = "SNRWLNS";
	
	
	public BookMarkEvent (Context context, String userId, String eventId, boolean bookMark, Handler handler) {
		this.userId = userId;
		this.eventId = eventId;
		this.handler = handler;
		this.bookMark = bookMark;
		this.context = context;
	}
	
	@Override
	public void run() {
		Log.i(TAG, "Entering the thread to bookmark an event with eventId = "+eventId+" and userId = "+userId);
		SharedPreferences settings =  context.getSharedPreferences(PREFS_NAME, 0);
		if (settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")) {
			Log.i(TAG, "Demo Server");
		}
		PostMethod post = new PostMethod(settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")? demoServerLink:serverLink);
		Message msg = new Message();
		msg.what = 0;
		msg.arg1 = 0;
		try {
			sleep(10);
			if (bookMark)
				post.setQueryString("bookMarkEvent");
			else
				post.setQueryString("unBookMarkEvent");
			post.setRequestHeader("userId", userId);
			post.setRequestHeader("eventId", eventId);
			HttpClient httpclient = new HttpClient();
			int result = httpclient.executeMethod(post);
			Log.i(TAG, "(Un)BookMarkEvent: Response Code = "+result);
			saveUserInfoObject(IOUtils.toByteArray(post.getResponseBodyAsStream()));
			if (result == 200) {
				Log.i(TAG, "Response status code: " + result);
				msg.what=200;
				if (bookMark)
					msg.arg1=198;
				else
					msg.arg1=199;
			}
			else {
				Log.e(TAG, "Event was not bookmarked");
				Log.e(TAG, "Response Body:"+post.getResponseBodyAsString());
				if (bookMark) {
					LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
					View layout = inflater.inflate(R.layout.toasttext, null);
					TextView text = (TextView) layout.findViewById(R.id.text);
					text.setText("Please try again to bookmark your event");
					Toast toast = new Toast(context);
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
					toast.setDuration(1000);
					toast.setView(layout);
					toast.show();
				}
				else {
					LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
					View layout = inflater.inflate(R.layout.toasttext, null);
					TextView text = (TextView) layout.findViewById(R.id.text);
					text.setText("Please try again to un-bookmark your event");
					Toast toast = new Toast(context);
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
					toast.setDuration(1000);
					toast.setView(layout);
					toast.show();
				}
			}
		} 
		catch(ConnectException e) {
			Log.e(TAG, "Socket exception occured");
			Log.e(TAG, "Mail was not sent to server");
			if (bookMark) {
				LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
				View layout = inflater.inflate(R.layout.toasttext, null);
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("Please try again to bookmark your event");
				Toast toast = new Toast(context);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();
			}
			else {
				LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
				View layout = inflater.inflate(R.layout.toasttext, null);
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("Please try again to un-bookmark your event");
				Toast toast = new Toast(context);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
			handler.sendMessage(msg);
		}
	}
	
    private boolean saveUserInfoObject(byte[] userAccountData) {
    	
		
		SharedPreferences settings =  context.getSharedPreferences("SNRWLNS", 0);
        SharedPreferences.Editor editor = settings.edit();
        Log.i(TAG, "Saving length for userAccountData = "+userAccountData.length);
        editor.putInt("userAccountDataLength", userAccountData.length);
        editor.commit();
        
    	FileOutputStream fos;
		try {
			fos = context.openFileOutput("userAccountData", Context.MODE_PRIVATE);
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
