package com.example.snrwlns.threads;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UpdateUserNotifications extends Thread implements Runnable {
	
	protected HashMap<String, String> data;
	protected Handler handler;
	protected Context context;
	protected String userId;
	protected static final String serverLink = "http://arcweb.arctry.com:8080/snrwlns_server/Main";
	protected static final String demoServerLink = "http://arcweb.arctry.com:8080/snrwlns_server1/Main";
	public static final String PREFS_NAME = "SNRWLNS";
	protected static final String TAG = "SNRWLNS";
	
	public UpdateUserNotifications (Context context, String userId, HashMap<String, String> data, Handler handler) {
		this.data = data;
		this.userId = userId;
		this.handler = handler;
		this.context = context;
	}
	
	@Override
	public void run() {
		Log.i(TAG, "Entering the thread to updateUserNotifications userId = "+userId);
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
			post.setQueryString("updateUserNotifications");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    ObjectOutputStream oos = new ObjectOutputStream(bos);
		    oos.writeObject(data);
		    oos.flush();
		    oos.close();
		    post.setRequestEntity(new InputStreamRequestEntity(new ByteArrayInputStream(bos.toByteArray())));
			post.setRequestHeader("userId", userId);
			HttpClient httpclient = new HttpClient();
			int result = httpclient.executeMethod(post);
			Log.i(TAG, "updateUserNotifications: Response Code = "+result);
			Log.i(TAG, "updateUserNotifications: Response String = "+post.getResponseBodyAsString());
			responseString = post.getResponseBodyAsString().split("\n")[0];
			Log.i(TAG, "Response String : " + responseString);
			if (result == 200 & responseString.equalsIgnoreCase("200-OK")) {
				Log.i(TAG, "Response status code: " + result);
				Log.i(TAG, "Response body :"+post.getResponseBodyAsString());
				msg.what=200;
				msg.arg1=1;
			}
			else {
				Log.e(TAG, "Application could not sign in");
				Log.e(TAG, "Response Body:"+post.getResponseBodyAsString());
			}
		} 
		catch(ConnectException e) {
			Log.e(TAG, "Socket exception occured");
			Log.e(TAG, "Create Account details were not sent to server");
			Log.e(TAG, "Response Body:"+responseString);
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
