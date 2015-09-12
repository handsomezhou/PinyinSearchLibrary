package com.handsomezhou.appsearch.broadcastreceiver;

import com.handsomezhou.appsearch.helper.AppInfoHelper;
import com.handsomezhou.appsearch.service.EasyHelperService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AppChangedReceiver extends BroadcastReceiver {
	private static final String TAG="AppChangedReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		//Toast.makeText(context, "AppChangedReceiver["+intent.getAction()+"]", Toast.LENGTH_LONG).show();
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
			//Toast.makeText(context, "AppChangedReceiver ACTION_PACKAGE_ADDED", Toast.LENGTH_LONG).show();
			Log.i(TAG, "ACTION_PACKAGE_ADDED");

			String packageName = intent.getData().getSchemeSpecificPart();
			if(false==AppInfoHelper.getInstance().isAppExist(packageName)){
				AppInfoHelper.getInstance().setAppInfoChanged(true);
			}
			
			EasyHelperService.startEasyHelperService(context);
		} else if (intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED)) {
			//Toast.makeText(context, "AppChangedReceiver ACTION_PACKAGE_CHANGED", Toast.LENGTH_LONG).show();
			Log.i(TAG, "ACTION_PACKAGE_CHANGED");
			//AppInfoHelper.getInstance().setAppInfoChanged(true);
			EasyHelperService.startEasyHelperService(context);
		} else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
			//Toast.makeText(context, "AppChangedReceiver ACTION_PACKAGE_REMOVED", Toast.LENGTH_LONG).show();
			Log.i(TAG, "ACTION_PACKAGE_REMOVED");
			//AppInfoHelper.getInstance().setAppInfoChanged(true);
			EasyHelperService.startEasyHelperService(context);
		}
	}
	


}
