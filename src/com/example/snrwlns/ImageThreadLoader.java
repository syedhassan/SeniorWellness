package com.example.snrwlns;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.State;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

public class ImageThreadLoader {
	private static final String TAG = "SNRWLNS";

	private final HashMap<String, SoftReference<Bitmap>> Cache = new HashMap<String,  SoftReference<Bitmap>>();

	private final class QueueItem {
		public URL url;
		public ImageLoadedListener listener;
	}
	private final ArrayList<QueueItem> Queue = new ArrayList<QueueItem>();

	private final Handler handler = new Handler();	
	private Thread thread;
	private QueueRunner runner = new QueueRunner();;

	public ImageThreadLoader() {
		thread = new Thread(runner);
	}

	public interface ImageLoadedListener {
		public void imageLoaded(Bitmap imageBitmap );
	}

	private class QueueRunner implements Runnable {
		public void run() {
			synchronized(this) {
				while(Queue.size() > 0) {
					final QueueItem item = Queue.remove(0);
					String fileName = item.url.toString().substring(item.url.toString().lastIndexOf("/")+1);
		        	File imageFile = new File(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName);
					if( Cache.containsKey(item.url) && Cache.get(item.url) != null) {
						handler.post(new Runnable() {
							public void run() {
								if( item.listener != null ) {
									SoftReference<Bitmap> ref = Cache.get(item.url);
									if( ref != null ) {
										item.listener.imageLoaded(ref.get());
									}
								}
							}
						});
					} 
					else if (imageFile.exists()) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = true;
		        		BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
		        		Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
		        		if(options.outHeight * options.outWidth * 2 >= 16384){
		        		    double sampleSize = scaleByHeight? options.outHeight / 100: options.outWidth / 100;
		        		    options.inSampleSize = (int)Math.pow(2d, Math.floor(Math.log(sampleSize)/Math.log(2d)));
		        		}
		        		options.inJustDecodeBounds = false; options.inTempStorage = new byte[1024];  options.inPurgeable=true;
		        		final Bitmap bm = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
		        		if( bm != null ) {
							Cache.put(item.url.toString(), new SoftReference<Bitmap>(bm));

							handler.post(new Runnable() {
								public void run() {
									if( item.listener != null ) {
										item.listener.imageLoaded(bm);
									}
								}
							});
						}
					}
					else {
						Log.d(TAG, "Image Loader from network = "+item.url);
						final Bitmap bmp = readBitmapFromNetwork(item.url);
						if( bmp != null ) {
							Cache.put(item.url.toString(), new SoftReference<Bitmap>(bmp));

							handler.post(new Runnable() {
								public void run() {
									if( item.listener != null ) {
										item.listener.imageLoaded(bmp);
									}
								}
							});
						}
					}
				}
			}
		}
	}

	public Bitmap loadImage( final String uri, final ImageLoadedListener listener) throws MalformedURLException {
		if( Cache.containsKey(uri)) {
			SoftReference<Bitmap> ref = Cache.get(uri);
			if( ref != null ) {
				return ref.get();
			}
		}

		QueueItem item = new QueueItem();
		item.url = new URL(uri);
		item.listener = listener;
		Queue.add(item);

		if( thread.getState() == State.NEW) {
			thread.start();
		} else if( thread.getState() == State.TERMINATED) {
			thread = new Thread(runner);
			thread.start();
		}
		return null;
	}

	public static Bitmap readBitmapFromNetwork( URL url ) {
		InputStream is = null;
		BufferedInputStream bis = null;
		Bitmap bmp = null;
		try {
			URLConnection conn = url.openConnection();
			conn.connect();
			is = conn.getInputStream();
			bis = new BufferedInputStream(is);
			
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 4;
			bmp = BitmapFactory.decodeStream(bis, null, options);
		} catch (MalformedURLException e) {
			Log.e(TAG, "Bad ad URL", e);
		} catch (IOException e) {
			Log.e(TAG, "Could not get remote ad image", e);
		} finally {
			try {
				if( is != null )
					is.close();
				if( bis != null )
					bis.close();
			} catch (IOException e) {
				Log.w(TAG, "Error closing stream.");
			}
		}
		return bmp;
	}
}
