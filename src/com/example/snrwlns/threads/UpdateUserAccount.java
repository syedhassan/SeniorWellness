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

public class UpdateUserAccount extends Thread implements Runnable {
	
	protected String fName, lName, pNumber, eAddress, passHash, userId;
	protected Handler handler;
	protected Context context;
	protected int messageId;
	protected static final String serverLink = "http://arcweb.arctry.com:8080/snrwlns_server/Main";
	protected static final String demoServerLink = "http://arcweb.arctry.com:8080/snrwlns_server1/Main";
	public static final String PREFS_NAME = "SNRWLNS";
	protected static final String TAG = "SNRWLNS";
	
	
	public UpdateUserAccount (Context context, String userId, String fName, String lName, String pNumber, String eAddress, String passHash, Handler handler) {
		this.fName = fName;
		this.lName = lName;
		this.pNumber = pNumber;
		this.eAddress = eAddress;
		this.passHash = passHash;
		this.userId = userId;
		this.handler = handler;
		this.context = context;
	}
	
	@Override
	public void run() {
		Log.i(TAG, "Entering the thread to signIn with fName = "+fName+" and lName = "+lName);
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
			post.setQueryString("updateUserAccount");
			post.setRequestHeader("fName", fName);
			post.setRequestHeader("lName", lName);
			if (pNumber.startsWith("1"))
				pNumber = pNumber.substring(1);
			post.setRequestHeader("pNumber", pNumber);
			post.setRequestHeader("eAddress", eAddress);
			post.setRequestHeader("passHash", passHash);
			post.setRequestHeader("userId", userId);
			HttpClient httpclient = new HttpClient();
			int result = httpclient.executeMethod(post);
			Log.i(TAG, "Create Account: Response Code = "+result);
			Log.i(TAG, "Create Account: Response String = "+post.getResponseBodyAsString());
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
