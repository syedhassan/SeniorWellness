package com.example.snrwlns.threads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.snrwlns.httpclient.MyHttpClient;

public class DownloadVideos extends Thread implements Runnable{
	protected String url;
	protected Handler handler;
	protected String fileName;
	protected Context context;
	private boolean done = false;
	public static final String TAG = "SNRWLNS";
	
	public DownloadVideos(Context context, String url, String fileName, Handler handler) {
		this.context = context;
		this.url = url;
		this.fileName = fileName;
		this.handler = handler;
	}
	
	@Override
	public void run() {
		url = url.replace(" ", "%20");
		Log.i(TAG, "URL for video = "+url);Log.i(TAG, " Video fileName = "+fileName);
		try {
			DefaultHttpClient client = new MyHttpClient(context);
			HttpGet get = new HttpGet(url);
			Log.i(TAG, "URI = "+url);
			HttpResponse getResponse = client.execute(get);
			File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"SeniorWellness");
			directory.mkdirs();

			File file = new File(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName);
			FileOutputStream fos = new FileOutputStream(file);
            InputStream is = getResponse.getEntity().getContent();
			byte buf[]=new byte[1024];
		    int len;
		    while((len=is.read(buf))>0) {
		    	fos.write(buf,0,len);
		    }
		    fos.close();
		    is.close();
			HttpEntity responseEntity = getResponse.getEntity();
			Log.i(TAG, "Length of the video = "+responseEntity.getContentLength()+" name = "+fileName);
			if (responseEntity.getContentLength() < 0)
				Log.e(TAG, "File Name was not downloaded = "+fileName);
		} catch (ClientProtocolException e) {
			Log.e(TAG, "Error in Client Protocl Exception");
			e.printStackTrace();
		} catch (NoHttpResponseException e) {
			Log.e(TAG, "Error in No Http Response Exception "+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "Error in IO Exception");
			e.printStackTrace();
		} catch (Exception e) {
			Log.e(TAG, "Some other Exception has occured");
			e.printStackTrace();
		} finally {
			done = true;
			Message msg = new Message();
			msg.obj = fileName;
			handler.sendMessage(msg);
		}
	}
	public boolean isDone() {
		return done;
	}
}
