package com.handsomezhou.pinyinsearchdemo.activity;

import android.support.v4.app.Fragment;

import com.handsomezhou.pinyinsearchdemo.fragment.T9SearchFragment;

public class T9SearchActivity extends BaseSingleFragmentActivity{

	@Override
	protected Fragment createFragment() {
		
		return new T9SearchFragment();
	}

	@Override
	protected boolean isRealTimeLoadFragment() {
	
		return false;
	}}
