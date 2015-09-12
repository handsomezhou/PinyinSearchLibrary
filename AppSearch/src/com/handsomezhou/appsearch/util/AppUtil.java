package com.handsomezhou.appsearch.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.TextUtils;

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
