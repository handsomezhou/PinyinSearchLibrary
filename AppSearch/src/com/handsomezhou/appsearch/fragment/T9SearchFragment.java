package com.handsomezhou.appsearch.fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.handsomezhou.appsearch.R;
import com.handsomezhou.appsearch.adapter.AppInfoAdapter;
import com.handsomezhou.appsearch.helper.AppInfoHelper;
import com.handsomezhou.appsearch.model.AppInfo;
import com.handsomezhou.appsearch.model.SearchMode;
import com.handsomezhou.appsearch.util.AppUtil;
import com.handsomezhou.appsearch.util.ViewUtil;
import com.handsomezhou.appsearch.view.T9TelephoneDialpadView;
import com.handsomezhou.appsearch.view.T9TelephoneDialpadView.OnT9TelephoneDialpadView;

public class T9SearchFragment extends BaseFragment implements
		OnT9TelephoneDialpadView {
	private static final String TAG="T9SearchFragment";
	private GridView mT9SearchGv;
	private TextView mSearchResultPromptTv;
	private T9TelephoneDialpadView mT9TelephoneDialpadView;
	private View mKeyboardSwitchLayout;
	private ImageView mKeyboardSwitchIv;
	private AppInfoAdapter mAppInfoAdapter;

	@Override
	public void onResume() {
		refreshView();
		super.onResume();
	}

	@Override
	protected void initData() {
		setContext(getActivity());
		mAppInfoAdapter = new AppInfoAdapter(getContext(),
				R.layout.app_info_grid_item, AppInfoHelper.getInstance()
						.getT9SearchAppInfos());
	}

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(R.layout.fragment_t9_search, container,
				false);
		mT9SearchGv = (GridView) view.findViewById(R.id.t9_search_grid_view);
		mT9SearchGv.setAdapter(mAppInfoAdapter);
		mSearchResultPromptTv = (TextView) view
				.findViewById(R.id.search_result_prompt_text_view);
		mT9TelephoneDialpadView = (T9TelephoneDialpadView) view
				.findViewById(R.id.t9_telephone_dialpad_view);
		mT9TelephoneDialpadView.setOnT9TelephoneDialpadView(this);
		mKeyboardSwitchLayout = view.findViewById(R.id.keyboard_switch_layout);
		mKeyboardSwitchIv = (ImageView) view
				.findViewById(R.id.keyboard_switch_image_view);
		showKeyboard();
		return view;
	}

	@Override
	protected void initListener() {
		mT9SearchGv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				AppInfo appInfo=(AppInfo) parent.getItemAtPosition(position);
				AppUtil.startApp(getContext(), appInfo);
				
			}
		});

		mT9SearchGv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				AppInfo appInfo=(AppInfo) parent.getItemAtPosition(position);
	
				AppUtil.uninstallApp(getContext(), appInfo);
			
				return true;
			}
		});
		
		mKeyboardSwitchLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				switchKeyboard();
			}
		});

		mKeyboardSwitchIv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				switchKeyboard();
			}
		});

	}

	/* start: OnT9TelephoneDialpadView */
	@Override
	public void onAddDialCharacter(String addCharacter) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeleteDialCharacter(String deleteCharacter) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDialInputTextChanged(String curCharacter) {
		updateSearch(curCharacter);
		refreshView();

	}

	@Override
	public void onHideT9TelephoneDialpadView() {
		hideKeyboard();

	}

	/* end: OnT9TelephoneDialpadView */

	public void refreshView() {
		refreshT9SearchGv();
	}

	private void switchKeyboard() {
		if (ViewUtil.getViewVisibility(mT9TelephoneDialpadView) == View.GONE) {
			showKeyboard();
		} else {
			hideKeyboard();
		}
	}

	private void hideKeyboard() {
		ViewUtil.hideView(mT9TelephoneDialpadView);
		mKeyboardSwitchIv
				.setBackgroundResource(R.drawable.keyboard_show_selector);
	}

	private void showKeyboard() {
		ViewUtil.showView(mT9TelephoneDialpadView);
		mKeyboardSwitchIv
				.setBackgroundResource(R.drawable.keyboard_hide_selector);
	}

	private void refreshT9SearchGv() {
		if (null == mT9SearchGv) {
			return;
		}

		BaseAdapter baseAdapter = (BaseAdapter) mT9SearchGv.getAdapter();
		Log.i(TAG, "getCount"+baseAdapter.getCount()+"");
		if (null != baseAdapter) {
			baseAdapter.notifyDataSetChanged();
			if (baseAdapter.getCount() > 0) {
				ViewUtil.showView(mT9SearchGv);
				ViewUtil.hideView(mSearchResultPromptTv);
			} else {
				ViewUtil.hideView(mT9SearchGv);
				ViewUtil.showView(mSearchResultPromptTv);
			}
		}
	}
	
	private void updateSearch(String search) {
		Log.i(TAG, "search=["+search+"]");
		String curCharacter;
		if (null == search) {
			curCharacter = search;
		} else {
			curCharacter = search.trim();
		}
		
		if (TextUtils.isEmpty(curCharacter)) {
			AppInfoHelper.getInstance().getT9SearchAppInfo(null);
		} else {
			AppInfoHelper.getInstance().getT9SearchAppInfo(curCharacter);
		}
	}
	

}
