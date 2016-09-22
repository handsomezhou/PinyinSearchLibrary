package com.handsomezhou.contactssearch.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handsomezhou.contactssearch.R;
import com.handsomezhou.contactssearch.activity.QwertySearchActivity;
import com.handsomezhou.contactssearch.activity.T9SearchActivity;

public class MainFragment extends BaseFragment {

	private Button mT9SearchBtn;
	private Button mQwertySearchBtn;
	
	@Override
	protected void initData() {
		setContext(getActivity());

	}

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container) {
		View view=inflater.inflate(R.layout.fragment_main, container, false);
		mT9SearchBtn=(Button) view.findViewById(R.id.t9_search_btn);
		mQwertySearchBtn=(Button) view.findViewById(R.id.qwerty_search_btn);
		return view;
	}

	@Override
	protected void initListener() {
		mT9SearchBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startT9Search();
			}
		});
		
		mQwertySearchBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startQwertySearch();
			}
		});

	}
	
	private void startT9Search(){
		Intent intent=new Intent(getContext(), T9SearchActivity.class);
		startActivity(intent);
	}

	private void startQwertySearch(){
		Intent intent=new Intent(getContext(), QwertySearchActivity.class);
		startActivity(intent);
	}

}
