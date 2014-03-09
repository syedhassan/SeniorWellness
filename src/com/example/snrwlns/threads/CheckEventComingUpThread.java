package com.example.snrwlns.threads;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class CheckEventComingUpThread extends Thread implements Runnable{
	
	
	protected String userId;
	protected Context context;
	protected String TAG = "SNRWLNS";
	protected String serverLink = "http://arcweb.arctry.com:8080/snrwlns_server/Main";
	protected static final String demoServerLink = "http://arcweb.arctry.com:8080/snrwlns_server1/Main";
	public static final String PREFS_NAME = "SNRWLNS";
	
	public CheckEventComingUpThread (Context context, String userId) {
		this.userId = userId;
	}
	
	@Override
	public void run() {
		Log.i(TAG, "Entering the thread to send Get Friend's data item from server");
		SharedPreferences settings =  context.getSharedPreferences(PREFS_NAME, 0);
		if (settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")) {
			Log.i(TAG, "Demo Server");
		}
		PostMethod post = new PostMethod(settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")? demoServerLink:serverLink);
		InputStream responseString= null;
		try {
			sleep(10);
			post.setQueryString("checkEventComingUp");
			post.setRequestHeader("userId", userId);
			HttpClient httpclient = new HttpClient();
			int result = httpclient.executeMethod(post);
			Log.i(TAG, "Check Event Coming Up: Response Code = "+result);
		} catch(ConnectException e) {
			Log.e(TAG, "Get Friend Event Data: Socket exception occured");
			Log.e(TAG, "Get Friend Event Data: Get friendEventData item was not sent to server");
			Log.e(TAG, "Get Friend Event Data: Response Body:"+responseString);
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
		}
	}
}
