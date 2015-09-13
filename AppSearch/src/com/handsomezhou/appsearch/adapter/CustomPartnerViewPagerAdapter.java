package com.handsomezhou.appsearch.adapter;

import java.util.List;

import com.handsomezhou.appsearch.model.PartnerView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CustomPartnerViewPagerAdapter extends FragmentPagerAdapter {
	List<PartnerView> mPartnerViews;

	public CustomPartnerViewPagerAdapter(FragmentManager fm,
			List<PartnerView> partnerViews) {
		super(fm);
		mPartnerViews=partnerViews;
	}

	@Override
	public Fragment getItem(int pos) {
		PartnerView partnerView=mPartnerViews.get(pos);
		return partnerView.getFragment();
	}

	@Override
	public int getCount() {
		
		return mPartnerViews.size();
	}

}
