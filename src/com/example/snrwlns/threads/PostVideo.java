package com.example.snrwlns.threads;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

public class PostVideo extends Thread implements Runnable {
	
	protected String videoName, eventId, videoLocation, userId;
	protected Context context;
	public static final String TAG = "SNRWLNS";
	protected static final String serverLink = "http://arcweb.arctry.com:8080/snrwlns_server/Main";
	protected static final String demoServerLink = "http://arcweb.arctry.com:8080/snrwlns_server1/Main";
	public static final String PREFS_NAME = "SNRWLNS";
	
	public PostVideo(Context context, String videoName, String videoLocation, String eventId, String userId){
		this.videoName = videoName;
		this.videoLocation = videoLocation;
		this.eventId = eventId;
		this.userId = userId;
		this.context = context;
	}
	
	@Override
	public void run() {
		Log.i(TAG, "Entering the post video thread, "+videoLocation+" Name = "+videoName+" and file size of video = "+(new File(videoLocation).length()));
		SharedPreferences settings =  context.getSharedPreferences(PREFS_NAME, 0);
		if (settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")) {
			Log.i(TAG, "Demo Server");
		}
		PostMethod post = new PostMethod(settings.getString("serverLink", "demoServer").equalsIgnoreCase("demoServer")? demoServerLink:serverLink);
		String responseString = "";
		File video = null;
		try {
			sleep(10);
			video = new File(videoLocation);
			post.setRequestEntity(new InputStreamRequestEntity(new FileInputStream(video), video.length()));

			post.setRequestHeader("Content-type", "video/3gp");
			post.setQueryString("saveVideo");
			post.setRequestHeader("fileName", videoName);
			post.setRequestHeader("eventId", eventId);
			post.setRequestHeader("userId", userId);
			HttpClient httpclient = new HttpClient();
			int result = httpclient.executeMethod(post);
			responseString = post.getResponseBodyAsString();
			Log.i(TAG, "Result of Video POSTing = "+result);
			if (result == 200) {
				Log.i(TAG, "Response status code: " + result);
				Log.i(TAG, "Response body :"+post.getResponseBodyAsString());
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						File originalFile = new File(videoLocation);
					    File thumbnailFile = new File(Environment.getExternalStorageDirectory()+"/"+videoName);
					    
					    FileInputStream fis = null;
					    FileOutputStream fos = null;
						try {
							fis = new FileInputStream(originalFile);
							fos = new FileOutputStream(thumbnailFile);
						    byte[] buf = new byte[1024];
						    int len;
						    while ((len = fis.read(buf)) > 0) {
						        fos.write(buf, 0, len);
						    }
						    fis.close();
						    fos.close();
						} catch (FileNotFoundException e2) {
							e2.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
			else {
				Log.e(TAG, "Video was not save on the database");
				Log.e(TAG, "Response Body:"+post.getResponseBodyAsString());
			}
		} catch(ConnectException e) {
			Log.e(TAG, "Socket exception occured");
			Log.e(TAG, "Picture was not save on the database");
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
			video.delete();
		}
	}
}
