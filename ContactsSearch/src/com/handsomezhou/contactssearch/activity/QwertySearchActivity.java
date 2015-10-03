package com.handsomezhou.contactssearch.activity;

import android.support.v4.app.Fragment;

import com.handsomezhou.contactssearch.fragment.QwertySearchFragment;

public class QwertySearchActivity extends BaseSingleFragmentActivity{

	@Override
	protected Fragment createFragment() {

		return new QwertySearchFragment();
	}

	@Override
	protected boolean isRealTimeLoadFragment() {

		return false;
	}
	
}
