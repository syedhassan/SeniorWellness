package com.example.snrwlns.threads;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

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

public class ConfirmSignUpEvent extends Thread implements Runnable {
	
	protected String userId, eventId, text;
	protected Handler handler;
	protected Context context;
	protected int messageId;
	protected static final String serverLink = "http://arcweb.arctry.com:8080/snrwlns_server/Main";
	protected static final String demoServerLink = "http://arcweb.arctry.com:8080/snrwlns_server1/Main";
	public static final String PREFS_NAME = "SNRWLNS";
	protected static final String TAG = "SNRWLNS";
	
	
	public ConfirmSignUpEvent (Context context, String userId, String eventId, String text, Handler handler) {
		this.userId = userId;
		this.eventId = eventId;
		this.text = text;
		this.handler = handler;
		this.context = context;
	}
	
	@Override
	public void run() {
		Log.i(TAG, "Entering the thread to send event confirmation for eventId = "+eventId+" with text = "+text);
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
			post.setQueryString("sendEventConfirmation");
			post.setRequestHeader("userId", userId);
			post.setRequestHeader("eventId", eventId);
			post.setRequestHeader("text", text);
			HttpClient httpclient = new HttpClient();
			int result = httpclient.executeMethod(post);
			Log.i(TAG, "ConfirmSignUpEvent: Response Code = "+result);
			
			responseString = post.getResponseBodyAsString();
			Log.i(TAG, "Response String : " + responseString);
			if (result == 200 & responseString.contains("200-OK")) {
				Log.i(TAG, "Response status code: " + result);
				Log.i(TAG, "Response body :"+post.getResponseBodyAsString());
				msg.what=200;
				msg.arg1=196;
				msg.arg2=Integer.parseInt(responseString.split("~")[1]);
			}
			else {
				Log.e(TAG, "Confirmation was not send");
				Log.e(TAG, "Response Body:"+post.getResponseBodyAsString());
				LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
				View layout = inflater.inflate(R.layout.toasttext, null);
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("Please try again to confirm your event");
				Toast toast = new Toast(context);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();

			}
		} catch(ConnectException e) {
			Log.e(TAG, "Socket exception occured");
			Log.e(TAG, "Mail was not sent to server");
			Log.e(TAG, "Response Body:"+responseString);
			LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
			View layout = inflater.inflate(R.layout.toasttext, null);
			TextView text = (TextView) layout.findViewById(R.id.text);
			text.setText("Please try again to confirm your event");
			Toast toast = new Toast(context);
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast.setDuration(1000);
			toast.setView(layout);
			toast.show();
//			Toast.makeText(context, "Please try again to confirm your event", 1000).show();

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
