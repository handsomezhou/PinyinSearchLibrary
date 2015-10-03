package com.handsomezhou.contactssearch.activity;

import android.support.v4.app.Fragment;

import com.handsomezhou.contactssearch.fragment.MainFragment;


public class MainActivity extends BaseSingleFragmentActivity{

	@Override
	protected Fragment createFragment() {
		return new MainFragment();
	}

	@Override
	protected boolean isRealTimeLoadFragment() {
		return false;
	}
	
}
