package com.handsomezhou.appsearch.model;

import android.support.v4.app.Fragment;

public class PartnerView {
	private Object mTag;
	private Fragment mFragment;

	public PartnerView(Object tag, Fragment fragment) {
		super();
		mTag = tag;
		mFragment = fragment;
	}

	public Object getTag() {
		return mTag;
	}

	public void setTag(Object tag) {
		mTag = tag;
	}

	public Fragment getFragment() {
		return mFragment;
	}

	public void setFragment(Fragment fragment) {
		mFragment = fragment;
	}
}
