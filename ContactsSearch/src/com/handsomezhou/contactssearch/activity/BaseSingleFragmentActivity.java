package com.handsomezhou.contactssearch.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Window;

import com.handsomezhou.contactssearch.R;

public abstract class BaseSingleFragmentActivity extends FragmentActivity {
	private Context mContext;
	private boolean mFullScreen = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContext(this);
		if (true == isFullScreen()) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		setContentView(R.layout.activity_fragment);
		
		// load fragment
		loadFragment();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	protected abstract Fragment createFragment();
	
	/**
	 * return true, fragment was loaded in real-time.Otherwise,fragment was loaded non-real time.
	 * @return
	 */
	protected abstract boolean isRealTimeLoadFragment();

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context context) {
		mContext = context;
	}

	public boolean isFullScreen() {
		return mFullScreen;
	}

	public void setFullScreen(boolean fullScreen) {
		mFullScreen = fullScreen;
	}

	private void loadFragment() {
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragment_container);
		if (false == isRealTimeLoadFragment()) {
			if (null == fragment) {
				fragment = createFragment();
				fm.beginTransaction().add(R.id.fragment_container, fragment)
						.commit();
			}
		} else {
			fragment = createFragment();
			fm.beginTransaction().replace(R.id.fragment_container, fragment)
					.commit();
		}

	}
}
