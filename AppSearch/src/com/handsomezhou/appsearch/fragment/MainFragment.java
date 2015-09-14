package com.handsomezhou.appsearch.fragment;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handsomezhou.appsearch.R;
import com.handsomezhou.appsearch.Interface.OnTabChange;
import com.handsomezhou.appsearch.adapter.CustomPartnerViewPagerAdapter;
import com.handsomezhou.appsearch.dialog.BaseProgressDialog;
import com.handsomezhou.appsearch.helper.AppInfoHelper;
import com.handsomezhou.appsearch.helper.AppInfoHelper.OnAppInfoLoad;
import com.handsomezhou.appsearch.model.IconButtonData;
import com.handsomezhou.appsearch.model.IconButtonValue;
import com.handsomezhou.appsearch.model.PartnerView;
import com.handsomezhou.appsearch.model.SearchMode;
import com.handsomezhou.appsearch.view.TopTabView;
import com.handsomezhou.appsearch.view.CustomViewPager;

public class MainFragment extends BaseFragment implements OnAppInfoLoad,
		OnTabChange {
	private static final String TAG = "MainFragment";
	private List<PartnerView> mPartnerViews;
	private TopTabView mTopTabView;
	private CustomViewPager mCustomViewPager;
	private CustomPartnerViewPagerAdapter mCustomPartnerViewPagerAdapter;
	private SearchMode mSearchMode;
	private BaseProgressDialog mBaseProgressDialog;

	@Override
	protected void initData() {
		setContext(getActivity());

		mPartnerViews = new ArrayList<PartnerView>();
		/* start: T9 search view */
		PartnerView t9PartnerView = new PartnerView(SearchMode.T9,
				new T9SearchFragment());
		mPartnerViews.add(t9PartnerView);
		
		/* end: T9 search view */

		/* start: Qwerty search view */
		PartnerView qwertyPartnerView = new PartnerView(SearchMode.QWERTY,
				new QwertySearchFragment());
		mPartnerViews.add(qwertyPartnerView);
		/* end: Qwerty search view */
		

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
		mCustomViewPager.setPagingEnabled(true);

		mTopTabView = (TopTabView) view.findViewById(R.id.top_tab_view);
		mTopTabView.setTextColorFocused(getContext().getResources().getColor(R.color.sea_green4));
		mTopTabView.setTextColorUnfocused(getContext().getResources().getColor(R.color.dim_grey));
		mTopTabView.setTextColorUnselected(getContext().getResources().getColor(R.color.dim_grey));
		mTopTabView.setHideIcon(true);
		mTopTabView.removeAllViews();

		/* start: T9 search tab */
		IconButtonValue t9IconBtnValue = new IconButtonValue(SearchMode.T9,0,  R.string.t9_search);
		t9IconBtnValue.setHideIcon(mTopTabView.isHideIcon());
		IconButtonData t9IconBtnData = new IconButtonData(getContext(),
				t9IconBtnValue);
		mTopTabView.addIconButtonData(t9IconBtnData);
		/* end: T9 search tab */

		/* start: Qwerty search tab */
		IconButtonValue qwertyIconBtnValue = new IconButtonValue(
				SearchMode.QWERTY, 0, R.string.qwerty_search);
		t9IconBtnValue.setHideIcon(mTopTabView.isHideIcon());
		IconButtonData qwertyIconBtnData = new IconButtonData(getContext(),
				qwertyIconBtnValue);
		mTopTabView.addIconButtonData(qwertyIconBtnData);
		/* end: Qwerty search tab */

		mTopTabView.setOnTabChange(this);
		return view;
	}

	@Override
	protected void initListener() {
		FragmentManager fm = getChildFragmentManager();
		mCustomPartnerViewPagerAdapter = new CustomPartnerViewPagerAdapter(fm,
				mPartnerViews);
		mCustomViewPager.setAdapter(mCustomPartnerViewPagerAdapter);
		mCustomViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageSelected(int pos) {

						PartnerView partnerView = mPartnerViews.get(pos);
						// Toast.makeText(getContext(),addressBookView.getTag().toString()+"+++"
						// , Toast.LENGTH_LONG).show();
						mTopTabView.setCurrentTabItem(partnerView.getTag());
						refreshView();
					}

					@Override
					public void onPageScrolled(int pos, float posOffset,
							int posOffsetPixels) {

					}

					@Override
					public void onPageScrollStateChanged(int state) {

					}
				});

	}

	/* start: OnAppInfoLoad */
	@Override
	public void onAppInfoLoadSuccess() {
		getBaseProgressDialog().hide();

		AppInfoHelper.getInstance().getQwertySearchAppInfo(null);
		AppInfoHelper.getInstance().getT9SearchAppInfo(null);

		refreshView();

	}

	@Override
	public void onAppInfoLoadFailed() {
		getBaseProgressDialog().hide();
		refreshView();
	}

	/* end: OnAppInfoLoad */

	/* start: OnTabChange */
	@Override
	public void onChangeToTab(Object fromTab, Object toTab,
			TAB_CHANGE_STATE tabChangeState) {
		int item = getPartnerViewItem(toTab);
		mCustomViewPager.setCurrentItem(item);

	}

	@Override
	public void onClickTab(Object currentTab, TAB_CHANGE_STATE tabChangeState) {
		Fragment fragment = mPartnerViews.get(getPartnerViewItem(currentTab))
				.getFragment();
		switch ((SearchMode) currentTab) {
		case T9:
			if (fragment instanceof T9SearchFragment) {
				// ((T9SearchFragment) fragment).updateView(tabChangeState);
				((T9SearchFragment) fragment).refreshView();
			}
			break;
		case QWERTY:
			if (fragment instanceof QwertySearchFragment) {
				((QwertySearchFragment) fragment).refreshView();
			}
			break;
		default:
			break;
		}

	}

	/* end: OnTabChange */

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


	private void refreshView() {
		Object currentTab = mTopTabView.getCurrentTab();
		int itemIndex = getPartnerViewItem(currentTab);
		Fragment fragment = mPartnerViews.get(itemIndex).getFragment();
		switch ((SearchMode) currentTab) {
		case T9:
			if (fragment instanceof T9SearchFragment) {
				((T9SearchFragment) fragment).refreshView();
			}
			break;
		case QWERTY:
			if (fragment instanceof QwertySearchFragment) {
				((QwertySearchFragment) fragment).refreshView();
			}
			break;
		default:
			break;
		}
	}

	private int getPartnerViewItem(Object tag) {
		int item = 0;
		;
		do {
			if (null == tag) {
				break;
			}

			for (int i = 0; i < mPartnerViews.size(); i++) {
				if (mPartnerViews.get(i).getTag().equals(tag)) {
					item = i;
					break;
				}
			}
		} while (false);

		return item;
	}
}
