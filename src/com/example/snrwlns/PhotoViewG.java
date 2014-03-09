package com.example.snrwlns;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snrwlns.threads.DownloadPictures;
import com.example.snrwlns.threads.DownloadPicturesH;

public class PhotoViewG extends Activity {
	
	private ImageButton cancelButton;
	private byte[] eventVideos;
	private ProgressDialog pd;
	private int count;
	public static final String TAG = "SNRWLNS";
	private ArrayList<String> eventVideoList;
	private ImageAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.eventphotolayout);
	    updateView();
	}
	
	@SuppressWarnings("unchecked")
	private void updateView() {
		
	    eventVideos = getIntent().getExtras().getByteArray("eventPhotosUri");	
	    count = getIntent().getExtras().getInt("count");
	    
	    if (count > 1) {
	    	((TextView)findViewById(R.id.signOutText)).setText(count+" PHOTOS");
	    }
	    else {
	    	((TextView)findViewById(R.id.signOutText)).setText(count+" PHOTO");
	    }
	    
	    {
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new ByteArrayInputStream(eventVideos));
				eventVideoList = (ArrayList<String>) ois.readObject();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		
		String[] thumbNails =  new String[eventVideoList.size()/2];
		final String[] videoUri =  new String[eventVideoList.size()/2];
		int counter = 0;
		Iterator<String> it2 = eventVideoList.iterator();
		while (it2.hasNext()) {
			videoUri[counter] = it2.next();
			thumbNails[counter] = it2.next().replace(" ", "%20");
			counter++;
	    }
        
	    cancelButton = (ImageButton) findViewById(R.id.cancelEventTagging);
	    cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	    
        GridView g = (GridView) findViewById(R.id.myGrid);
        adapter = new ImageAdapter(this, thumbNails);
        g.setAdapter(adapter);
        g.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				
                String url = videoUri[position];
		        try {
		        	String fileName = url.substring(url.lastIndexOf("/")+1);
		        	url = url.replace(" ", "%20");
		        	File imageFile = new File(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName);
		        	if (imageFile.exists()) {
	                	String imageExtension = fileName.substring(fileName.lastIndexOf(".")+1);
	                	Intent intent = new Intent(Intent.ACTION_VIEW); 
	                	fileName = "file://"+Environment.getExternalStorageDirectory()+"/SeniorWellness/"+fileName;
	                	intent.setDataAndType(Uri.parse(fileName), "image/"+imageExtension);
	                	startActivity(intent);
		        	}
		        	else {
		        		pd = new ProgressDialog(PhotoViewG.this);
						pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				        pd.setMessage("Working...");
				        pd.setIndeterminate(true);
				        pd.setCancelable(false);
				        pd.show();
						DownloadPicturesH pictures = new DownloadPicturesH(PhotoViewG.this, url, fileName, messageFromThread);
						pictures.start();
		        	}
		        }
		        catch (Exception e) {
		        	e.printStackTrace();
		        }
			}
		});
	}
	
	 @Override
	 public void onConfigurationChanged(Configuration newConfig) {
	   super.onConfigurationChanged(newConfig);
	   if (newConfig.orientation == 2) {
		   RelativeLayout headerLayout = (RelativeLayout) findViewById(R.id.headerLayout);
		    MarginLayoutParams params = new MarginLayoutParams(headerLayout.getLayoutParams());
		    params.topMargin=20;params.bottomMargin=15;params.leftMargin=12;params.rightMargin=12;
		    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(params);
		    headerLayout.setLayoutParams(layoutParams);
	   }
	   else {
		   RelativeLayout headerLayout = (RelativeLayout) findViewById(R.id.headerLayout);
		    MarginLayoutParams params = new MarginLayoutParams(headerLayout.getLayoutParams());
		    params.topMargin=60;params.bottomMargin=15;params.leftMargin=12;params.rightMargin=12;
		    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(params);
		    headerLayout.setLayoutParams(layoutParams);
	   }
	   updateView();
	 }
	
	private Handler messageFromThread = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
			pd.dismiss();
    		if (msg.what == 0) {
    			String fileName = msg.obj.toString();
    			String imageExtension = fileName.substring(fileName.lastIndexOf(".")+1);
            	Intent intent = new Intent(Intent.ACTION_VIEW); 
            	fileName = "file://"+Environment.getExternalStorageDirectory()+"/SeniorWellness/"+fileName;
            	intent.setDataAndType(Uri.parse(fileName), "image/"+imageExtension);
            	startActivity(intent);
    		}
    		else {
    			LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				View layout = inflater.inflate(R.layout.toasttext, (ViewGroup) findViewById(R.id.toast_layout_root));
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("An error occureed in Photo Viewing");
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(1000);
				toast.setView(layout);
				toast.show();
    		}
    	}
    };
	
    public class ImageAdapter extends BaseAdapter {

        private Context mContext;
        private String[] uri;
        public ImageAdapter(Context c, String[] uri) {
            mContext = c;
            this.uri = uri;
        }

        public int getCount() {
            return uri.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
        	
        	final ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(80, 80));
                imageView.setAdjustViewBounds(false);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) convertView;
            }
            HttpGet httpRequest = null;

 	        try {
 	        	String url = uri[position];
 	        	String fileName = url.substring(url.lastIndexOf("/")+1);
 	        	url = url.replace(" ", "%20");
 	        	File imageFile = new File(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName);
 	        	BitmapFactory.Options options = new BitmapFactory.Options();
 	        	if (imageFile.exists()) {
 	        		options.inJustDecodeBounds = true;
 	        		BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName, options);
 	        		Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
 	        		if(options.outHeight * options.outWidth * 2 >= 16384){
 	        		    double sampleSize = scaleByHeight? options.outHeight / 100: options.outWidth / 100;
 	        		    options.inSampleSize = (int)Math.pow(2d, Math.floor(Math.log(sampleSize)/Math.log(2d)));
 	        		}
 	        		options.inJustDecodeBounds = false; options.inTempStorage = new byte[1024];  options.inPurgeable=true;
 	        		Bitmap bm = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/"+fileName, options);
 	        		imageView.setImageBitmap(bm);
 	        	}
 	        	else {
 		        	httpRequest = new HttpGet(url);
 		        	DefaultHttpClient httpclient = new DefaultHttpClient();
 		            HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
 	
 		            HttpEntity entity = response.getEntity();
 		            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity); 
 		            InputStream instream = bufHttpEntity.getContent();
 		            options.inJustDecodeBounds = true;
 	        		BitmapFactory.decodeStream(instream, null, options);
 	        		Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
 	        		if(options.outHeight * options.outWidth * 2 >= 16384){
 	        		    double sampleSize = scaleByHeight? options.outHeight / 100: options.outWidth / 100;
 	        		    options.inSampleSize = (int)Math.pow(2d, Math.floor(Math.log(sampleSize)/Math.log(2d)));
 	        		}
 	        		options.inJustDecodeBounds = false; options.inTempStorage = new byte[1024];  options.inPurgeable=true;
 		            Bitmap bm = BitmapFactory.decodeStream(instream, null, options);
 		            imageView.setImageBitmap(bm);
 					DownloadPictures pictures = new DownloadPictures(mContext, url, fileName);
 					pictures.start();
 	        	}
 	        	imageView.setTag(url);
 			} catch (MalformedURLException e) {
 				e.printStackTrace();
 			} catch (IOException e) {
 				e.printStackTrace();
 			}
             imageView.setTag(uri[position]);
             return imageView;
        }
    }
}
