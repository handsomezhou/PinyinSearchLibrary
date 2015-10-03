package com.handsomezhou.contactssearch.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handsomezhou.contactssearch.util.ViewUtil;

public abstract class BaseFragment extends Fragment {
	private Context mContext;
	private boolean mHideImeTouchOutsideEditText=true;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	
		View view =initView(inflater,container);
		if(isHideImeTouchOutsideEditText()){
            ViewUtil.setHideIme(getActivity(), view);
        }
		
		initListener();
		
		return view;
	}

	/**
	 * init data in onCreate()
	 * 
	 * initData()->initView()->initListener()
	 */
	protected abstract void initData();
	
	/**
	 * init view in onCreate()
	 * 
	 * initData()->initView()->initListener()
	 * @return 
	 */
	protected abstract View initView(LayoutInflater inflater, ViewGroup container);
		
	
	/**
	 * init Listener in onCreate()
	 * 
	 * initData()->initView()->initListener()
	 */
	protected abstract void initListener();
	
	public Context getContext() {
		return mContext;
	}

	public void setContext(Context context) {
		mContext = context;
	}
	
    public boolean isHideImeTouchOutsideEditText() {
        return mHideImeTouchOutsideEditText;
    }

    public void setHideImeTouchOutsideEditText(boolean hideImeTouchOutsideEditText) {
        mHideImeTouchOutsideEditText = hideImeTouchOutsideEditText;
    }
}
