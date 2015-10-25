package com.handsomezhou.contactssearch.application;

import android.app.Application;
import android.content.Context;

public class ContacstSearchApplication extends Application {
	private static Context mContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mContext=getApplicationContext();
	}
	
	public static Context getContextObject(){
		return mContext;
	}
}
