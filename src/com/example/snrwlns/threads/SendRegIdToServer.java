package com.example.snrwlns.threads;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SendRegIdToServer extends Thread implements Runnable {
	
	protected String emailId, userId;
	protected Context context;
	protected String registrationId;
	public static final String TAG = "SNRWLNS";
	protected static final String serverLink = "http://arcweb.arctry.com:8080/snrwlns_server/Main";
	protected static final String demoServerLink = "http://arcweb.arctry.com:8080/snrwlns_server1/Main";
	public static final String PREFS_NAME = "SNRWLNS";
	
	public SendRegIdToServer (Context context, String emailId, String registrationId, String userId) {
		this.emailId = emailId;
		this.userId = userId;
		this.context = context;
		this.registrationId = registrationId;
	}
	
	@Override
	public void run() {
		Log.i(TAG, "Entering the thread to send registration Id to server");
		SharedPreferences settings =  context.getSharedPreferences(PREFS_NAME, 0);
		if (settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")) {
			Log.i(TAG, "Demo Server");
		}
		PostMethod post = new PostMethod(settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")? demoServerLink:serverLink);
		String responseString="";
		try {
			sleep(10);
			post.setQueryString("sendRegIdPhone");
			post.setRequestHeader("emailId", emailId);
			post.setRequestHeader("userId", userId);
			post.setRequestHeader("registrationId", registrationId);
			HttpClient httpclient = new HttpClient();
			int result = httpclient.executeMethod(post);
			Log.i(TAG, "RegId: Response Code = "+result);
			Log.i(TAG, "RegId: Response Code1 = "+post.getStatusCode());
			
			responseString = post.getResponseBodyAsString();
			Log.i(TAG, "RegId: Response String : " + responseString);
			if (responseString.equalsIgnoreCase("200-OK")) {
				Log.i(TAG, "RegId: Response status code: " + result);
				Log.i(TAG, "RegId: Response body :"+post.getResponseBodyAsString());
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
		}
	}
}
