package com.handsomezhou.appsearch.fragment;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handsomezhou.appsearch.R;
import com.handsomezhou.appsearch.adapter.CustomFragmentPagerAdapter;
import com.handsomezhou.appsearch.dialog.BaseProgressDialog;
import com.handsomezhou.appsearch.helper.AppInfoHelper;
import com.handsomezhou.appsearch.helper.AppInfoHelper.OnAppInfoLoad;
import com.handsomezhou.appsearch.model.SearchMode;
import com.handsomezhou.appsearch.view.CustomViewPager;

public class MainFragment extends BaseFragment implements OnAppInfoLoad{
	private static final String TAG="MainFragment";
	private List<Fragment> mFragments;
	private CustomViewPager mCustomViewPager;
	private CustomFragmentPagerAdapter mCustomFragmentPagerAdapter;
	private SearchMode mSearchMode;
	private BaseProgressDialog mBaseProgressDialog;
	
	@Override
	protected void initData() {
		setContext(getActivity());
		if (null == mFragments) {
			mFragments = new ArrayList<Fragment>();
			Fragment t9SearchFragment = new T9SearchFragment();
			if (null != t9SearchFragment) {
				mFragments.add(SearchMode.T9.ordinal(),t9SearchFragment);
			}

			Fragment qwertySearchFragment = new QwertySearchFragment();
			if (null != qwertySearchFragment) {
				mFragments.add(SearchMode.QWERTY.ordinal(),qwertySearchFragment);
			}
		}
		
		boolean startLoadSuccess = AppInfoHelper.getInstance()
				.startLoadAppInfo();
		if (true == startLoadSuccess) {
			getBaseProgressDialog().show(
					getContext().getString(R.string.app_info_loading));
		}
		AppInfoHelper.getInstance().setOnAppInfoLoad(this);

	}

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		mCustomViewPager = (CustomViewPager) view
				.findViewById(R.id.custom_view_pager);
		return view;
	}

	@Override
	protected void initListener() {
		FragmentManager fm = getChildFragmentManager();
		mCustomFragmentPagerAdapter = new CustomFragmentPagerAdapter(fm,
				mFragments);
		mCustomViewPager.setAdapter(mCustomFragmentPagerAdapter);
		mCustomViewPager.setPagingEnabled(true);
		mCustomViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageSelected(int pos) {
						//setCurrentAppInfoFragmentIndex(pos);
						SearchMode searchMode=getSearchMode(pos);
						setSearchMode(searchMode);
						setCurrentFragment(getSearchMode().ordinal());
						refreshView();
					}

					@Override
					public void onPageScrolled(int pos, float posOffset,
							int posOffsetPixels) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onPageScrollStateChanged(int state) {
						// TODO Auto-generated method stub

					}
				});


		setSearchMode(SearchMode.T9);
		setCurrentFragment(getSearchMode().ordinal());
		//SearchMode
		//setCurrentFragment();

	}

	/*start: OnAppInfoLoad*/
	@Override
	public void onAppInfoLoadSuccess() {
		getBaseProgressDialog().hide();
		if (null == mFragments) {
			return;
		}
		
		Log.i(TAG, "app count"+ AppInfoHelper.getInstance()
				.getT9SearchAppInfos().size());
		Fragment fragment=mFragments.get(getSearchMode().ordinal());
		
		AppInfoHelper.getInstance().getQwertySearchAppInfo(null);
		AppInfoHelper.getInstance().getT9SearchAppInfo(null);
		
		if(fragment instanceof T9SearchFragment){
			((T9SearchFragment) fragment).refreshView();
		}else if(fragment instanceof QwertySearchFragment){
			((QwertySearchFragment) fragment).refreshView();
		}
		
		
	}

	@Override
	public void onAppInfoLoadFailed() {
		getBaseProgressDialog().hide();
		if (null == mFragments) {
			return;
		}
		
		Fragment fragment=mFragments.get(getSearchMode().ordinal());
		if(fragment instanceof T9SearchFragment){
			((T9SearchFragment) fragment).refreshView();
		}else if(fragment instanceof QwertySearchFragment){
			((QwertySearchFragment) fragment).refreshView();
		}
	}
	/*end: OnAppInfoLoad*/

	public SearchMode getSearchMode() {
		return mSearchMode;
	}

	public void setSearchMode(SearchMode searchMode) {
		mSearchMode = searchMode;
	}

	public BaseProgressDialog getBaseProgressDialog() {
		if (null == mBaseProgressDialog) {
			mBaseProgressDialog = new BaseProgressDialog(getContext());
		}
		return mBaseProgressDialog;
	}

	public void setBaseProgressDialog(BaseProgressDialog baseProgressDialog) {
		mBaseProgressDialog = baseProgressDialog;
	}
	
	public void setCurrentFragment(int fragmentIndex) {
		if (fragmentIndex < 0 || fragmentIndex >= mFragments.size()) {
			return;
		}
		mCustomViewPager.setCurrentItem(fragmentIndex);
		//setCurrentAppInfoFragmentIndex(fragmentIndex);
	}
	
	private SearchMode getSearchMode(int postion){
		SearchMode searchMode=SearchMode.T9;
		if(postion==SearchMode.QWERTY.ordinal()){
			searchMode=SearchMode.QWERTY;
		}
		
		return searchMode;
	}
	
	private void refreshView(){
		if (null == mFragments) {
			return;
		}
		
		Fragment fragment=mFragments.get(getSearchMode().ordinal());
		if(fragment instanceof T9SearchFragment){
			((T9SearchFragment) fragment).refreshView();
		}else if(fragment instanceof QwertySearchFragment){
			((QwertySearchFragment) fragment).refreshView();
		}
	}
}
