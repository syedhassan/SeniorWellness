package com.example.snrwlns;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.snrwlns.ImageThreadLoader.ImageLoadedListener;
import com.example.snrwlns.threads.DownloadVideos;

@SuppressWarnings("unused")
public class VideoViewG extends Activity {
	
	/**
	 **  Called when the user presses the thumbnails for videos.
	 */
	
	private TextView closestTagText;
	private ImageButton cancelButton;
	private byte[] eventVideos;
	private ProgressDialog pd;
	private int count;
	public static final String TAG = "SNRWLNS";
	private static final int choiceShown = 0;
	private static final int choiceHidden = 0;
	private int status = 0;
	private ArrayList<String> eventVideoList;
	protected ImageAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.eventphotolayout);

	    updateView();
	}
	
	@SuppressWarnings("unchecked")
	private void updateView() {
		eventVideos = getIntent().getExtras().getByteArray("eventVideosUri");	
	    count = getIntent().getExtras().getInt("count");
	    
	    if (count > 1) {
	    	((TextView)findViewById(R.id.signOutText)).setText(count+" VIDEOS");
	    }
	    else {
	    	((TextView)findViewById(R.id.signOutText)).setText(count+" VIDEO");
	    }
	    
	    {
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new ByteArrayInputStream(eventVideos));
				eventVideoList = (ArrayList<String>) ois.readObject();
				//Log.i(TAG, "~~~FRIEND DATA MAP= "+eventVideoList);
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		String[] thumbNails =  new String[eventVideoList.size()/2];
		String[] videoUri =  new String[eventVideoList.size()/2];
		int counter = 0;
		Iterator<String> it2 = eventVideoList.iterator();
		while (it2.hasNext()) {
			videoUri[counter] = it2.next().replace(" ", "%20");
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
        adapter = new ImageAdapter(this, thumbNails, videoUri);
        g.setAdapter(adapter);
        
        g.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {

				ImageView self = (ImageView) v;
                String ds = (String) self.getTag();
                String fileName = ds.substring(ds.lastIndexOf("/")+1);
                String pathToFile = Environment.getExternalStorageDirectory() + "/SeniorWellness/"+fileName;
                if (new File(pathToFile).exists()) {
                	String videoFileExtension = fileName.substring(fileName.lastIndexOf(".")+1);
                	Intent intent = new Intent(Intent.ACTION_VIEW); 
                    intent.setDataAndType(Uri.parse("file://"+pathToFile), "video/"+videoFileExtension);
                    startActivity(intent);
                }
                else {
                	pd = new ProgressDialog(VideoViewG.this);
    				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		        pd.setMessage("Downloading video...");
    		        pd.setIndeterminate(true);
    		        pd.setCancelable(false);
    		        pd.show();
                	DownloadVideos downVideo = new DownloadVideos(getApplicationContext(), ds, fileName, handler);
                    downVideo.start();
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
	 
	private Handler handler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		//Log.i(TAG, "In Handler for Video Downloading thead, result= "+msg.what);
			pd.dismiss();
    		if (msg.what == 0) {
    			String fileName = (String) msg.obj;
    			//Log.i(TAG, "FILENAME = "+fileName);
    			String videoFileExtension = fileName.substring(fileName.lastIndexOf(".")+1);
                if (videoFileExtension.equalsIgnoreCase("3gp")) {
                	videoFileExtension = "3gpp";
                }
    			Intent intent = new Intent(Intent.ACTION_VIEW); 
                intent.setDataAndType(Uri.parse("file://"+Environment.getExternalStorageDirectory()+"/SeniorWellness/"+fileName), "video/"+videoFileExtension);
                startActivity(intent);

    		}
    	}
    };
	
    public class ImageAdapter extends BaseAdapter {
    	 private ImageThreadLoader imageLoader = new ImageThreadLoader();
    	 private HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();
         public ImageAdapter(Context c, String[] uri, String[] videoUri) {
            mContext = c;
            this.uri = uri;
            this.videoUri = videoUri;
            options = new BitmapFactory.Options();
            
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
        	   if (imageView != null) {
                   Bitmap cachedImage = null;
	               	try {
	                       cachedImage = imageLoader.loadImage(uri[position], new ImageLoadedListener() {
	                           public void imageLoaded(Bitmap imageBitmap) {
	                         	  imageView.setImageBitmap(imageBitmap);
	                         	  notifyDataSetChanged();
	                         	  //Log.d(TAG, "Image successfully loaded: "+imageBitmap);
	                           }
	                       });
	                } 
	               	catch (MalformedURLException e) {
	                       //Log.e(TAG, "Bad remote image URL: " + uri[position], e);
	                }
	
	                if( cachedImage != null ) {
	                 	cache.put(uri[position], cachedImage);
	                 	imageView.setImageBitmap(cachedImage);
	                }
               }
            imageView.setTag(videoUri[position]);
            return imageView;
        }

        private Context mContext;
        private String[] uri, videoUri;
        private BitmapFactory.Options options;
        
        public void clearCache() {
            //clear memory cache
            cache.clear();
        }
	
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	adapter.clearCache();
    }
    
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e(TAG, "LOW MEMORY");
        adapter.clearCache();
    }

}
