package com.handsomezhou.appsearch.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handsomezhou.appsearch.R;

public class NavigationBarLayout extends RelativeLayout {
	private Context mContext;
	private View mNavigationBarView;
	private Button mBackBtn;
	private TextView mTitleTv;
	
	private String mTitle;

	private OnNavigationBarLayout mOnNavigationBarLayout;
	
	public interface OnNavigationBarLayout{
		void onBack();
	}
	
	public NavigationBarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		initView();
		initData();
		initListener();
	}
	
	public OnNavigationBarLayout getOnNavigationBarLayout() {
		return mOnNavigationBarLayout;
	}

	public void setOnNavigationBarLayout(OnNavigationBarLayout onNavigationBarLayout) {
		mOnNavigationBarLayout = onNavigationBarLayout;
	}
	
	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
		mTitleTv.setText(mTitle);
	}
	
	private void initView(){
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mNavigationBarView=(View) inflater.inflate(R.layout.navigation_bar, this);
		mBackBtn=(Button) mNavigationBarView.findViewById(R.id.back_btn);
		mTitleTv=(TextView) mNavigationBarView.findViewById(R.id.title_text_view);
		return;
	}
	
	private void initData(){
		
		return;
	}
	
	private void initListener(){
		mBackBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(null!=mOnNavigationBarLayout){
					mOnNavigationBarLayout.onBack();
				}
				
			}
		});
		return;
	}
	
	

}
