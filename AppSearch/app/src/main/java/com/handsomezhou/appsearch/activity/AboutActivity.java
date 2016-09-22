package com.handsomezhou.appsearch.activity;

import android.support.v4.app.Fragment;

import com.handsomezhou.appsearch.fragment.AboutFragment;

public class AboutActivity extends BaseSingleFragmentActivity {

	@Override
	protected Fragment createFragment() {

		return new AboutFragment();
	}

	@Override
	protected boolean isRealTimeLoadFragment() {

		return false;
	}

}
