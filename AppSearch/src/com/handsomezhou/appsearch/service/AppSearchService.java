package com.handsomezhou.appsearch.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.handsomezhou.appsearch.helper.AppInfoHelper;

public class AppSearchService extends Service{
	private static final String TAG="AppSearchService";
	public static final String ACTION_APP_SEARCH_SERVICE="com.handsomezhou.appsearch.service.APP_SEARCH_SERVICE";
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		super.onCreate();
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		AppInfoHelper.getInstance().startLoadAppInfo();
		Log.i(TAG, "onStartCommand");

		
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		startEasyHelperService();
		super.onDestroy();
	}
	

	
	private void startEasyHelperService(){
		Intent intent=new Intent();
		intent.setAction(ACTION_APP_SEARCH_SERVICE);
		startService(intent);
	}
	
	public static void startEasyHelperService(Context context){
		Intent easyHelperServiceIntent=new Intent(context,AppSearchService.class);
		easyHelperServiceIntent.setAction(AppSearchService.ACTION_APP_SEARCH_SERVICE);
		context.startService(easyHelperServiceIntent);
		
		
	}
	
	public static void stopEasyHelperService(Context context){
		Intent easyHelperServiceIntent=new Intent(context,AppSearchService.class);
		context.stopService(easyHelperServiceIntent);
	}

}
