package com.handsomezhou.appsearch.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CustomFragmentPagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> mFragments;

	public CustomFragmentPagerAdapter(FragmentManager fm,
			List<Fragment> fragments) {
		super(fm);
		mFragments = fragments;
	}

	@Override
	public Fragment getItem(int pos) {
		return mFragments.get(pos);
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

}
