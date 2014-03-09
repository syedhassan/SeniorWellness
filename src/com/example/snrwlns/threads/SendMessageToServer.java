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

public class SendMessageToServer extends Thread implements Runnable {
	
	protected String userId, eventId, text;
	protected Context context;
	protected Handler handler;
	protected String registrationId;
	public static final String TAG = "SNRWLNS";
	protected static final String serverLink = "http://arcweb.arctry.com:8080/snrwlns_server/Main";
	protected static final String demoServerLink = "http://arcweb.arctry.com:8080/snrwlns_server1/Main";
	public static final String PREFS_NAME = "SNRWLNS";
	
	public SendMessageToServer (Context context, String userId, String eventId, String text, Handler handler) {
		this.eventId = eventId;
		this.userId = userId;
		this.text = text;
		this.handler = handler;
		this.context = context;
	}
	
	@Override
	public void run() {
		Log.i(TAG, "Entering the thread to send registration Id to server "+text);
		SharedPreferences settings =  context.getSharedPreferences(PREFS_NAME, 0);
		if (settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")) {
			Log.i(TAG, "Demo Server");
		}
		PostMethod post = new PostMethod(settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")? demoServerLink:serverLink);
		String responseString="";
		Message msg = new Message();
		msg.what = 0;
		msg.arg1 = 0;
		msg.obj = "";
		try {
			sleep(10);
			post.setQueryString("insertMessage");
			post.setRequestHeader("userId", userId);
			post.setRequestHeader("eventId", eventId);
			post.setRequestHeader("text", text);
			HttpClient httpclient = new HttpClient();
			int result = httpclient.executeMethod(post);
			Log.i(TAG, "RegId: Response Code = "+result);
			Log.i(TAG, "RegId: Response Code1 = "+post.getStatusCode());
			
			responseString = post.getResponseBodyAsString();
			Log.i(TAG, "RegId: Response String : " + responseString);
			if (responseString.equalsIgnoreCase("200-OK")) {
				Log.i(TAG, "RegId: Response status code: " + result);
				Log.i(TAG, "RegId: Response body :"+post.getResponseBodyAsString());
				msg.what=200;
				msg.arg1=1;
			}
			else {
				Log.e(TAG, "Registration Id was not send");
				Log.e(TAG, "Response Body:"+post.getResponseBodyAsString());
			}
		} catch(ConnectException e) {
			Log.e(TAG, "RegId: Socket exception occured");
			Log.e(TAG, "RegId: Registration Id was not sent to server");
			Log.e(TAG, "RegId: Response Body:"+responseString);
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
