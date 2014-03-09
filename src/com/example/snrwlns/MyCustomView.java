package com.example.snrwlns;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class MyCustomView extends TextView {

	public MyCustomView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    Log.i("SNRWLNS", "\t1Width = "+widthMeasureSpec);
	    int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
	    Log.i("SNRWLNS", "\tWidth = "+parentWidth);
	}
}