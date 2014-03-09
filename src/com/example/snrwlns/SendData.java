package com.example.snrwlns;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;


class SendData {
    String fileName;
    Uri uri;
    String contentType;
    long contentLength;

    SendData(Intent intent, ContentResolver contentResolver) {
    	
    	Bundle extras = intent.getExtras();
    	if (extras.containsKey(Intent.EXTRA_STREAM)) {
        Uri uri = this.uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
        String scheme = uri.getScheme();
        if (scheme.equals("content")) {
	        Cursor cursor = contentResolver.query(uri, null, null, null, null);
	        cursor.moveToFirst();
	        this.fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaColumns.DISPLAY_NAME));
	        this.contentType = intent.getType();
	        this.contentLength = cursor.getLong(cursor.getColumnIndexOrThrow(MediaColumns.SIZE));
	        Log.i(Main.TAG, "File path = "+uri.getPath());
	        Log.i(Main.TAG, "File name = "+this.fileName);
	        Log.i(Main.TAG, "File type = "+this.contentType);
	        Log.i(Main.TAG, "File length = "+this.contentLength);
        }
      }
    }
    
    public String getPath() {
    	return uri.getPath();
    }
    
    public String getFileName() {
    	return fileName;
    }
    
    public String getFileType() {
    	return contentType;
    }
    
    public long getFileLength() {
    	return contentLength;
    }
}




