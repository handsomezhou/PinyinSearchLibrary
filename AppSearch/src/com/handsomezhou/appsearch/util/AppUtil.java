package com.handsomezhou.appsearch.util;

import com.handsomezhou.appsearch.R;
import com.handsomezhou.appsearch.model.AppInfo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

public class AppUtil {
	/**
	 * Return true when start app success,otherwise return false.
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean startApp(Context context,String packageName){
		boolean startAppSuccess=false;
		do{
			if((null==context)||TextUtils.isEmpty(packageName)){
				break;
			}
			
			PackageManager pm=context.getPackageManager();
			Intent intent=pm.getLaunchIntentForPackage(packageName);
			
			if(null!=intent){
				context.startActivity(intent);
				startAppSuccess=true;
			}
		}while(false);
		
		
		return startAppSuccess;
	}
	
	/**
	 * start app via appinfo
	 * @param context
	 * @param appInfo
	 */
	public static void startApp(Context context,AppInfo appInfo){
		if((null==context)||(null==appInfo)){
			return;
		}
		
		if(null!=appInfo){
			
			if(!appInfo.getPackageName().equals(context.getPackageName())){
				boolean startAppSuccess=AppUtil.startApp(context, appInfo.getPackageName());
				if(false==startAppSuccess){
					Toast.makeText(context, R.string.app_can_not_be_launched_directly, Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(context, R.string.the_app_has_been_launched, Toast.LENGTH_SHORT).show();
			}
			
		}
	}
	/**
	 * whether app can Launch the main activity.
	 * Return true when can Launch,otherwise return false.
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean appCanLaunchTheMainActivity(Context context,String packageName){
		boolean canLaunchTheMainActivity=false;
		do{
			if((null==context)||TextUtils.isEmpty(packageName)){
				break;
			}
			
			PackageManager pm=context.getPackageManager();
			Intent intent=pm.getLaunchIntentForPackage(packageName);
			canLaunchTheMainActivity=(null==intent)?(false):(true);
		}while(false);
		
		return canLaunchTheMainActivity;
	} 
	
	/**
	 * uninstall app via package name
	 * @param context
	 * @param packageName
	 */
	public static void uninstallApp(Context context,String packageName){
		Uri packageUri = Uri.parse("package:" + packageName);  
		Intent intent = new Intent();  
		intent.setAction(Intent.ACTION_DELETE);  
		intent.setData(packageUri);  
		context.startActivity(intent);  
	}
	
	/**
	 * uninstall app via appInfo
	 * @param context
	 * @param appInfo
	 */
	public static void uninstallApp(Context context,AppInfo appInfo){
		if((null==context)||(null==appInfo)){
			return;
		}
		
		if(null!=appInfo)
		{
			if (!appInfo.getPackageName().equals(context.getPackageName())) {
				AppUtil.uninstallApp(context,appInfo.getPackageName());
			}else{
				Toast.makeText(context, R.string.can_not_to_uninstall_yourself, Toast.LENGTH_SHORT).show();
			}
		}
	}
	/**
	 * get version name via package name
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static String getVersionName(Context context,String packageName){
		String versionName=null;
		do{
			if((null==context)||TextUtils.isEmpty(packageName)){
				break;
			}
			PackageManager pm=context.getPackageManager();
			try {
				PackageInfo pi=pm.getPackageInfo(packageName, 0);
				versionName=pi.versionName;
			} catch (NameNotFoundException e) {
				
				e.printStackTrace();
				break;
			}
			
			
		}while(false);
		
		return versionName;
	}
}
