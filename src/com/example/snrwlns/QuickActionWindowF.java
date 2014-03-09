package com.example.snrwlns;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;

public class QuickActionWindowF extends PopupWindow implements KeyEvent.Callback {
	private final Context mContext;
	private final LayoutInflater mInflater;
	private final WindowManager mWindowManager;
	private int Layout;
	int flag = 0;
	View contentView;
	
	private int mScreenWidth;
	
	private int mShadowHoriz;
	
	private ViewGroup mTrack;
	private Animation mTrackAnim;

	private View mPView;
	private Rect mAnchor;
	
	private ImageView callPhone, text, email;
	
	public QuickActionWindowF(Context context, View pView, Rect rect, int Layout, int Animation) {
		super(context);
		
		mPView = pView;
		mAnchor = rect;
		
		mContext = context;
		mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		mInflater = ((Activity)mContext).getLayoutInflater();
		this.Layout = Layout;
		setContentView(Layout);
		
		mScreenWidth = mWindowManager.getDefaultDisplay().getWidth();
		
		setWindowLayoutMode(ViewGroup.LayoutParams.WRAP_CONTENT
				, ViewGroup.LayoutParams.WRAP_CONTENT);
		
		final Resources res = mContext.getResources();

		mShadowHoriz = res.getDimensionPixelSize(R.dimen.quickaction_shadow_horiz);

		if (rect.left != 0) {
			int widthOfGrill = res.getDrawable(R.drawable.phonetextemailgrill391x134).getMinimumWidth();
			mShadowHoriz = -(mAnchor.left - (widthOfGrill-100));
		}
		setWidth(mScreenWidth + mShadowHoriz + mShadowHoriz);
		setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		
		setBackgroundDrawable(new ColorDrawable(0));
		
		callPhone = (ImageView) contentView.findViewById(R.id.callPhone);
		text = (ImageView) contentView.findViewById(R.id.text);
		email = (ImageView) contentView.findViewById(R.id.email);
		
		mTrack = (ViewGroup) contentView.findViewById(R.id.scroll);
		
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		mTrackAnim = AnimationUtils.loadAnimation(mContext, Animation);
		
		mTrackAnim.setInterpolator(new Interpolator() {
			public float getInterpolation(float t) {
				final float inner = (t * 1.55f) - 1.1f;
				return (1.2f - inner * inner);
			}
		});	
	}
	
	public ImageView getCallPhoneContext() {
		return callPhone;
	}
	
	public ImageView getTextContext() {
		return text;
	}
	
	public ImageView getEmailContext() {
		return email;
	}
	private void setContentView(int resId) {
		contentView = mInflater.inflate(resId, null);
		super.setContentView(contentView);
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBackPressed();
			return true;
		}

		return false;
	}
	
	private void onBackPressed() {
			dismiss();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
		return false;
	}
	
	public void show() {
		show(mAnchor.centerX());
	}
	
	public void show(int requestedX) {
		super.showAtLocation(mPView, Gravity.NO_GRAVITY, 0, 0);
		
		if (isShowing()) {
			int x, y, windowAnimations;
			this.getContentView().measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			windowAnimations = R.style.QuickActionBelowAnimation;
			x = -mShadowHoriz;y = mAnchor.bottom;
			final int blockHeight = this.getContentView().getMeasuredHeight();
			if (!(mAnchor.top > blockHeight)) {
				if (this.Layout == R.layout.quickactionfriends)
					y = 50;
			}
			setAnimationStyle(windowAnimations);
			mTrack.startAnimation(mTrackAnim);
			Log.i("SNRWLNS", "QAWF X = "+x+" Y = "+y);
			this.update(x, y, -1, -1);
		}
	}

	@Override
	public boolean onKeyLongPress(int arg0, KeyEvent arg1) {
		return false;
	}
}
