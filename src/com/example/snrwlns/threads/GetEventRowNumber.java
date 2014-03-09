package com.example.snrwlns.threads;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

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

import com.example.snrwlns.R;

public class GetEventRowNumber extends Thread implements Runnable {
	
	protected String eventId;
	protected Handler handler;
	protected Context context;
	protected static final String serverLink = "http://arcweb.arctry.com:8080/snrwlns_server/Main";
	protected static final String demoServerLink = "http://arcweb.arctry.com:8080/snrwlns_server1/Main";
	public static final String PREFS_NAME = "SNRWLNS";
	protected static final String TAG = "SNRWLNS";
	
	public GetEventRowNumber (Context context, String eventId, Handler handler) {
		this.eventId = eventId;
		this.handler = handler;
		this.context = context;
	}
	
	@Override
	public void run() {
		Log.i(TAG, "Entering the thread to get event row number with eventId = "+eventId);
		SharedPreferences settings =  context.getSharedPreferences(PREFS_NAME, 0);
		if (settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")) {
			Log.i(TAG, "Demo Server");
		}
		PostMethod post = new PostMethod(settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")? demoServerLink:serverLink);
		String responseString="";
		Message msg = new Message();
		msg.what = 0;
		msg.arg1 = 0;
		try {
			sleep(10);
			post.setQueryString("getEventRowNumber");
			post.setRequestHeader("eventId", eventId);
			HttpClient httpclient = new HttpClient();
			int result = httpclient.executeMethod(post);
			Log.i(TAG, "(Un)BookMarkEvent: Response Code = "+result);
			responseString = post.getResponseBodyAsString();
			Log.i(TAG, "Response String : " + responseString);
			if (result == 200 & responseString.contains("200-OK")) {
				Log.i(TAG, "Response status code: " + result);
				Log.i(TAG, "Response body :"+post.getResponseBodyAsString());
				msg.what=200;
				msg.arg1=Integer.parseInt(responseString.split("~")[1]);
			}
			else {
				Log.e(TAG, "Event was not fetvched");
				Log.e(TAG, "Response Body:"+post.getResponseBodyAsString());
				LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
				View layout = inflater.inflate(R.layout.toasttext, null);
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("Please try again to get event details!");
				Toast toast = new Toast(context);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();

			}
		} 
		catch(ConnectException e) {
			Log.e(TAG, "Socket exception occured");
			Log.e(TAG, "Mail was not sent to server");
			Log.e(TAG, "Response Body:"+responseString);
			Log.e(TAG, "Event was not fetvched");
			try {
				Log.e(TAG, "Response Body:"+post.getResponseBodyAsString());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
			View layout = inflater.inflate(R.layout.toasttext, null);
			TextView text = (TextView) layout.findViewById(R.id.text);
			text.setText("Please try again to get event details!");
			Toast toast = new Toast(context);
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast.setDuration(1000);
			toast.setView(layout);
			toast.show();

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
}
