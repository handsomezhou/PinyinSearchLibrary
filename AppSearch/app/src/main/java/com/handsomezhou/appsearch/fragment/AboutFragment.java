package com.handsomezhou.appsearch.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handsomezhou.appsearch.R;
import com.handsomezhou.appsearch.util.AppUtil;
import com.handsomezhou.appsearch.util.ViewUtil;
import com.handsomezhou.appsearch.view.NavigationBarLayout;
import com.handsomezhou.appsearch.view.NavigationBarLayout.OnNavigationBarLayout;

public class AboutFragment extends BaseFragment implements
		OnNavigationBarLayout {

	private NavigationBarLayout mNavigationBarLayout;
	private String mTitle;
	private String mVersionName;

	@Override
	public void onResume() {
		refreshView();
		super.onResume();
	}

	@Override
	protected void initData() {
		setContext(getActivity());

		mTitle = getContext().getString(R.string.about);

		mVersionName = getContext().getString(R.string.version_name)
				+ getContext().getString(R.string.colon)
				+ AppUtil.getVersionName(getContext(), getContext()
						.getPackageName());
	}

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(R.layout.fragment_about, container, false);
		mNavigationBarLayout = (NavigationBarLayout) view
				.findViewById(R.id.navigation_bar_layout);
		mNavigationBarLayout.setOnNavigationBarLayout(this);
		mNavigationBarLayout.setTitle(mTitle);

		return view;
	}

	@Override
	protected void initListener() {

		return;
	}

	/* Start: OnNavigationBarLayout */
	@Override
	public void onBack() {
		back();

	}

	/* End: OnNavigationBarLayout */

	private void refreshView() {

		

	}

	private void back() {
		getActivity().finish();
	}

}
