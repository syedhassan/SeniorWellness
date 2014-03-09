package com.example.snrwlns;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.Iterator;
import java.util.LinkedHashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snrwlns.threads.PostPicture;
import com.example.snrwlns.threads.PostPictureT;
import com.example.snrwlns.threads.PostVideo;
import com.example.snrwlns.threads.PostVideoT;

@SuppressWarnings("unused")
public class EventTagging extends Activity {
	
	/**
	 **  Called when the user confirms sign up.
	 */
	
	private TextView closestTagText;
	private LinearLayout otherEventsTagLayout;
	private ImageButton cancelButton;
	private byte[] tagData;
	private ProgressDialog pd;
	private String mediaType;
	private String fileName, filePath;
	private int fileType;
	public static final String TAG 												= "SNRWLNS";
	public static final String PREFS_NAME 										= "SNRWLNS";
	private LinearLayout childrenLayout, taggingEventHeaderLayout;
	private static final int choiceShown = 0;
	private static final int choiceHidden = 0;
	private int status = 0;
	private String folder 														= Environment.getExternalStorageDirectory().getPath()+"/SeniorWellness/";
	private String postFileSendServer, thumbnailFileSendServer;
	private LinkedHashMap<Integer, String> tagginData;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {

	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.eventtagging);
	    
	    mediaType = getIntent().getExtras().getString("mediaType");
	    tagData = getIntent().getExtras().getByteArray("data");	
		
	    {
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new ByteArrayInputStream(tagData));
				tagginData = (LinkedHashMap<Integer, String>) ois.readObject();
				//Log.i(TAG, "~~~Taggging DATA = "+tagginData);
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	    fileName = getIntent().getExtras().getString("fileName");
	    filePath = getIntent().getExtras().getString("filePath");
	    
	    
	    postFileSendServer = folder+"post_"+fileName;
	    thumbnailFileSendServer = folder+"thumbnail_"+fileName;
	    fileType = getIntent().getExtras().getInt("fileType");
	    if (fileType == 0) {
	    	pd = new ProgressDialog(this);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        pd.setMessage("Working...");
	        pd.setIndeterminate(true);
	        pd.setCancelable(false);
	        pd.show();
	    	Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					File originalFile = new File(filePath);
				    File copyFile = new File(folder+fileName);
				    
				    FileInputStream fis = null;
				    FileOutputStream fos = null;
					try {
						fis = new FileInputStream(originalFile);
						fos = new FileOutputStream(copyFile);
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
					boolean rotateFlag = false;
					try {
	    				ExifInterface exif = new ExifInterface(copyFile.getPath());
	    				if (exif.getAttribute(ExifInterface.TAG_ORIENTATION) != null) {
	    					if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")) {
	    						Log.i(TAG, "File is PORTRAIT hence rotate it by 90");
	    						rotateFlag = true;
	    					}
	    				}
	    			} catch (IOException e) {
	    				e.printStackTrace();
	    			}
					BitmapFactory.Options options = new BitmapFactory.Options();
	        		options.inSampleSize = 4;
	        		try {
						FileOutputStream outT = new FileOutputStream(thumbnailFileSendServer);
						Bitmap bitmapT = BitmapFactory.decodeStream(new FileInputStream(copyFile), null, options);
						int tempW = bitmapT.getWidth();
		        		int tempH = bitmapT.getHeight();
		        		if (rotateFlag) {
		        			Matrix mtx = new Matrix();
   	        		     	mtx.setRotate(90);
		        			bitmapT = Bitmap.createBitmap(bitmapT, 0, 0, tempW, tempH, mtx, true);
		        		}
		        		bitmapT.compress(Bitmap.CompressFormat.JPEG, 70, outT); 
						outT.close();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						FileOutputStream outT = new FileOutputStream(postFileSendServer);
						
						Bitmap bitmapT = BitmapFactory.decodeStream(new FileInputStream(copyFile), null, options);
						bitmapT.getConfig();
						
						int tempW = bitmapT.getWidth();
		        		int tempH = bitmapT.getHeight();
		        		if (rotateFlag) {
		        			Matrix mtx = new Matrix();
   	        		     	mtx.setRotate(90);
		        			bitmapT = Bitmap.createBitmap(bitmapT, 0, 0, tempW, tempH, mtx, true);
		        		}
		        		bitmapT.compress(Bitmap.CompressFormat.JPEG, 95, outT);
						outT.close();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
					rotateFlag = false;
					handler.sendEmptyMessage(0);
				}
			});
	    	thread.start();
	    	
	    }
	    
	    final String[] groups = { "", "Another Event", "Save to my gallery" };
	    Integer key = (Integer) tagginData.keySet().toArray()[0];
	    groups[0] = tagginData.get(key);
	    
	    for (int i=0 ;i<3 ;i++ ){
	    	if (i==1){
	    		
	    		RelativeLayout otherInnerTagLayout = (RelativeLayout) findViewById(R.id.otherInnerTagLayout);
	    		otherInnerTagLayout.setOnClickListener(new OnClickListener() {
				
					@Override
					public void onClick(View v) {
						childrenLayout = (LinearLayout) findViewById(R.id.otherEventLayout);
						
						if (status == 0) {
							status = 1;
							childrenLayout.setVisibility(View.VISIBLE);
						}
						else {
							status = 0;
							childrenLayout.setVisibility(View.GONE);
						}
					}
				});
	    		
	    		childrenLayout = (LinearLayout) findViewById(R.id.otherEventLayout);
	    		childrenLayout.setVisibility(View.GONE);
	    		Iterator<Integer> iterator = tagginData.keySet().iterator();
	    	    iterator.next();
	    	    LayoutParams paramsChildren = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	    	    childrenLayout.setLayoutParams(paramsChildren);
	    	    childrenLayout.setOrientation(LinearLayout.VERTICAL);
	    	    childrenLayout.setVisibility(View.GONE);
	    		
	    	    while (iterator.hasNext()) {
	    	    	final Integer eventId = iterator.next();
	    	    	String eventName= tagginData.get(eventId);
	    	    	LayoutInflater inflater = LayoutInflater.from(EventTagging.this);
	    	    	View v = inflater.inflate(R.layout.eventtaggingrow, null);
	    	    	TextView textView = (TextView)v.findViewById(R.id.taggingEventName);
		    		textView.setText(eventName);
		    		textView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							SharedPreferences setting =  getSharedPreferences(PREFS_NAME, 0);
							if (fileName.contains(".jpg") | fileName.contains(".jpeg") | fileName.contains(".png")) {
								Log.i(TAG, "Starting to post a picture...");
								
								PostPicture postPicture = new PostPicture(EventTagging.this, "post_"+fileName, postFileSendServer, eventId.toString(), setting.getString("userId", "100"));
								postPicture.start();
								PostPictureT postThumbnail = new PostPictureT(EventTagging.this, "thumbnail_"+fileName, thumbnailFileSendServer, eventId.toString(), setting.getString("userId", "100"));
								postThumbnail.start();
							}
							else {
								Log.i(TAG, "Starting to post a video... with Filename = "+fileName+" and path = "+filePath);

								PostVideo postVideo = new PostVideo(EventTagging.this, fileName, filePath, eventId.toString(), setting.getString("userId", "100"));
								postVideo.start();
								String thumbNailFile = "thumbnail_"+fileName.substring(0, fileName.indexOf("."))+".jpg";
								final int THUMBNAIL_SIZE = 100;
								Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, Thumbnails.MINI_KIND);
								File bitmapFile =  new File(folder+thumbNailFile);
								try {
									FileOutputStream out = new FileOutputStream(bitmapFile);
									bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out);
									PostVideoT postThumbnail = new PostVideoT(EventTagging.this, thumbNailFile, folder+thumbNailFile, eventId.toString(), setting.getString("userId", "100"));
									postThumbnail.start();
								}
								catch (FileNotFoundException e) {
									e.printStackTrace();
								}
							}
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							finally {
								finish();
							}
						}
					});
		    		
		    		childrenLayout.addView(v);
	    	    }
	    	}
	    	if (i==0) {
	    		TextView textView = (TextView) findViewById(R.id.closestText);
	    		textView.setText(groups[i]);
	    		
	    		RelativeLayout closestTextLayout = (RelativeLayout) findViewById(R.id.closestTextLayout);
	    		closestTextLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						SharedPreferences setting =  getSharedPreferences(PREFS_NAME, 0);
						String eventId = tagginData.keySet().iterator().next().toString();
						if (fileName.contains(".jpg") | fileName.contains(".jpeg") | fileName.contains(".png")) {
							PostPicture postPicture = new PostPicture(EventTagging.this, "post_"+fileName, postFileSendServer, eventId.toString(), setting.getString("userId", "100"));
							postPicture.start();
							
							PostPictureT postThumbnail = new PostPictureT(EventTagging.this, "thumbnail_"+fileName, thumbnailFileSendServer, eventId.toString(), setting.getString("userId", "100"));
							postThumbnail.start();
						}
						else {

							PostVideo postVideo = new PostVideo(EventTagging.this, fileName, filePath, eventId, setting.getString("userId", "100"));
							postVideo.start();
							String thumbNailFile = "thumbnail_"+fileName.substring(0, fileName.indexOf("."))+".jpg";
							final int THUMBNAIL_SIZE = 100;
							Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, Thumbnails.MINI_KIND);
							File bitmapFile =  new File(folder+thumbNailFile);
							try {
								FileOutputStream out = new FileOutputStream(bitmapFile);
								bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);
								PostVideoT postThumbnail = new PostVideoT(EventTagging.this, thumbNailFile, folder+thumbNailFile, eventId, setting.getString("userId", "100"));
								postThumbnail.start();
							}
							catch (FileNotFoundException e) {
								e.printStackTrace();
							}
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						finally {
							finish();
						}
					}
				});
	    	}
	    	if (i==2) {
	    		TextView textView = (TextView) findViewById(R.id.saveText);
	    		textView.setText(groups[i]);
	    		RelativeLayout saveTagLayout = (RelativeLayout) findViewById(R.id.saveTagLayout);
	    		saveTagLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						finish();
					}
				});
	    	}
	    }
	    
	    cancelButton = (ImageButton) findViewById(R.id.cancelEventTagging);
	    cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	    
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (pd != null){
				if (pd.isShowing())
					pd.dismiss();
			}
		}
	};
	
	@Override
	public void onPause() {
		super.onPause();
		if (pd != null){
			if (pd.isShowing())
				pd.dismiss();
		}
	}
}