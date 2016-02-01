package com.handsomezhou.appsearch.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.text.TextUtils;
import android.util.Log;

import com.handsomezhou.appsearch.application.AppSearchApplication;
import com.handsomezhou.appsearch.model.AppInfo;
import com.handsomezhou.appsearch.model.AppInfo.SearchByType;
import com.handsomezhou.appsearch.model.AppType;
import com.handsomezhou.appsearch.util.AppUtil;
import com.pinyinsearch.model.PinyinSearchUnit;
import com.pinyinsearch.util.PinyinUtil;
import com.pinyinsearch.util.QwertyUtil;
import com.pinyinsearch.util.T9Util;


public class AppInfoHelper {
	private static final String TAG="AppInfoHelper";
	private static Character THE_LAST_ALPHABET='z';
	private Context mContext;
	private static AppInfoHelper mInstance;
	
	private AppType mCurrentAppType;
	private List<AppInfo> mBaseAllAppInfos;
	
	private List<AppInfo> mQwertySearchAppInfos;
	private List<AppInfo> mT9SearchAppInfos;
	
	private StringBuffer mFirstNoQwertySearchResultInput=null;
	private StringBuffer mFirstNoT9SearchResultInput=null;
	
	private AsyncTask<Object, Object, List<AppInfo>> mLoadAppInfoTask=null;
	private OnAppInfoLoad mOnAppInfoLoad;
	private boolean mAppInfoChanged=true;

	public interface OnAppInfoLoad{
		void onAppInfoLoadSuccess();
		void onAppInfoLoadFailed();
	}
	
	public static AppInfoHelper getInstance(){
		if(null==mInstance){
			mInstance=new AppInfoHelper();
		}
		
		return mInstance;
	} 
	
	private AppInfoHelper(){
		initAppInfoHelper();
		
		return;
	}
	
	private void initAppInfoHelper(){
		mContext=AppSearchApplication.getContext();
		setCurrentAppType(AppType.ALL_APP);
		
		clearAppInfoData();
		
		return;
	}

	
	public AppType getCurrentAppType() {
		return mCurrentAppType;
	}

	public void setCurrentAppType(AppType currentAppType) {
		mCurrentAppType = currentAppType;
	}
		
	public List<AppInfo> getBaseAllAppInfos() {
		return mBaseAllAppInfos;
	}

	public void setBaseAllAppInfos(List<AppInfo> baseAllAppInfos) {
		mBaseAllAppInfos = baseAllAppInfos;
	}
	
	public List<AppInfo> getQwertySearchAppInfos() {
		return mQwertySearchAppInfos;
	}

	public void setQwertySearchAppInfos(List<AppInfo> qwertySearchAppInfos) {
		mQwertySearchAppInfos = qwertySearchAppInfos;
	}

	public List<AppInfo> getT9SearchAppInfos() {
		return mT9SearchAppInfos;
	}

	public void setT9SearchAppInfos(List<AppInfo> t9SearchAppInfos) {
		mT9SearchAppInfos = t9SearchAppInfos;
	}

	public OnAppInfoLoad getOnAppInfoLoad() {
		return mOnAppInfoLoad;
	}

	public void setOnAppInfoLoad(OnAppInfoLoad onAppInfoLoad) {
		mOnAppInfoLoad = onAppInfoLoad;
	}

	public boolean isAppInfoChanged() {
		return mAppInfoChanged;
	}

	public void setAppInfoChanged(boolean appInfoChanged) {
		mAppInfoChanged = appInfoChanged;
	}
	
	public boolean startLoadAppInfo(){
		if(true==isAppInfoLoading()){
			return false;
		}
		
		if(false==isAppInfoChanged()){
			return false;
		}
		
		clearAppInfoData();
		mLoadAppInfoTask=new AsyncTask<Object, Object, List<AppInfo>>(){

			@Override
			protected List<AppInfo> doInBackground(Object... params) {
				// TODO Auto-generated method stub
				return loadAppInfo(mContext);
			}

			@Override
			protected void onPostExecute(List<AppInfo> result) {
				parseAppInfo(result);
				super.onPostExecute(result);
				//setAppInfoChanged(false);
				mLoadAppInfoTask=null;
			}
			
		}.execute();
		setAppInfoChanged(false);
		return true;
		
	}
	
	@SuppressLint("DefaultLocale")
	public List<AppInfo> loadAppInfo(Context context){
		List<AppInfo> appInfos=new ArrayList<AppInfo>();
		List<AppInfo> kanjiStartAppInfos = new ArrayList<AppInfo>();
		List<AppInfo> nonKanjiStartAppInfos = new ArrayList<AppInfo>();
		do{
			if(null==context){
				break;
			}
			
			PackageManager pm=context.getPackageManager();
			
			long startLoadTime=System.currentTimeMillis();
			int flags = PackageManager.GET_UNINSTALLED_PACKAGES;
		
		
			List<PackageInfo> packageInfos=pm.getInstalledPackages(flags);
			Log.i(TAG, packageInfos.size()+"");
			for(PackageInfo pi:packageInfos){
				boolean canLaunchTheMainActivity=AppUtil.appCanLaunchTheMainActivity(mContext, pi.packageName);
				if(true==canLaunchTheMainActivity){
					AppInfo appInfo=getAppInfo(pm, pi);
					if(TextUtils.isEmpty(appInfo.getLabel())){
						continue;
					}
					
					appInfo.getLabelPinyinSearchUnit().setBaseData(appInfo.getLabel());
					PinyinUtil.parse(appInfo.getLabelPinyinSearchUnit());
					String sortKey=PinyinUtil.getSortKey(appInfo.getLabelPinyinSearchUnit()).toUpperCase();
					appInfo.setSortKey(praseSortKey(sortKey));
					boolean isKanji=PinyinUtil.isKanji(appInfo.getLabel().charAt(0));
					if(true==isKanji){
						kanjiStartAppInfos.add(appInfo);
					}else{
						nonKanjiStartAppInfos.add(appInfo);
					}
					
				}
			}
			long endLoadTime=System.currentTimeMillis();
			Log.i(TAG, "endLoadTime-startLoadTime["+(endLoadTime-startLoadTime)+"]");
			//Toast.makeText(mContext, "endLoadTime-startLoadTime["+(endLoadTime-startLoadTime)+"]", Toast.LENGTH_LONG).show();
			break;
		}while(false);
		
		long sortStartTime=System.currentTimeMillis();
		
		Collections.sort(kanjiStartAppInfos, AppInfo.mAscComparator);
		Collections.sort(nonKanjiStartAppInfos, AppInfo.mAscComparator);
		
		//appInfos.addAll(nonKanjiStartAppInfos);
		appInfos.addAll(kanjiStartAppInfos);
	
		/*Start: merge nonKanjiStartAppInfos and kanjiStartAppInfos*/
		int lastIndex=0;
		boolean shouldBeAdd=false;
		for(int i=0; i<nonKanjiStartAppInfos.size(); i++){
			String nonKanfirstLetter=PinyinUtil.getFirstLetter(nonKanjiStartAppInfos.get(i).getLabelPinyinSearchUnit());
			//Log.i(TAG, "nonKanfirstLetter=["+nonKanfirstLetter+"]["+nonKanjiStartAppInfos.get(i).getLabel()+"]["+Integer.valueOf(nonKanjiStartAppInfos.get(i).getLabel().charAt(0))+"]");
			int j=0;
			for(j=0+lastIndex; j<appInfos.size(); j++){
				String firstLetter=PinyinUtil.getFirstLetter(appInfos.get(j).getLabelPinyinSearchUnit());
				lastIndex++;
				if(nonKanfirstLetter.charAt(0)<firstLetter.charAt(0)||nonKanfirstLetter.charAt(0)>THE_LAST_ALPHABET){
					shouldBeAdd=true;
					break;
				}else{
					shouldBeAdd=false;
				}
			}
			
			if(lastIndex>=appInfos.size()){
				lastIndex++;
				shouldBeAdd=true;
				//Log.i(TAG, "lastIndex="+lastIndex);
			}
			
			if(true==shouldBeAdd){
				appInfos.add(j, nonKanjiStartAppInfos.get(i));
				shouldBeAdd=false;
			}
		}
		/*End: merge nonKanjiStartAppInfos and kanjiStartAppInfos*/
	
		
/*		for(int i=0; i<appInfos.size(); i++){
			Log.i(TAG, i+"["+appInfos.get(i).getLabel()+"]");
		}*/
		
		long sortEndTime=System.currentTimeMillis();
		Log.i(TAG, "sortEndTime-sortStartTime["+(sortEndTime-sortStartTime)+"]");
	
		Log.i(TAG, "appInfos.size()"+ appInfos.size());
		//Toast.makeText(context,"["+ appInfos.get(0).getLabel()+"]["+appInfos.get(0).getPackageName()+"]", Toast.LENGTH_LONG).show();
		return appInfos;
	}
	
	public void qwertySearch(String keyword){
		List<AppInfo> baseAppInfos=getBaseAppInfo();
		if(null!=mQwertySearchAppInfos){
			mQwertySearchAppInfos.clear();
		}else{
			mQwertySearchAppInfos=new ArrayList<AppInfo>();
		}
		
		if(TextUtils.isEmpty(keyword)){
			for(AppInfo ai:baseAppInfos){
				ai.setSearchByType(SearchByType.SearchByNull);
				ai.clearMatchKeywords();
				ai.setMatchStartIndex(-1);
				ai.setMatchLength(0);
			}
			mQwertySearchAppInfos.addAll(baseAppInfos);
			
			mFirstNoQwertySearchResultInput.delete(0, mFirstNoQwertySearchResultInput.length());
			Log.i(TAG, "null==search,mFirstNoQwertySearchResultInput.length()="+ mFirstNoQwertySearchResultInput.length());
			return;
		}
		
		if (mFirstNoQwertySearchResultInput.length() > 0) {
			if (keyword.contains(mFirstNoQwertySearchResultInput.toString())) {
				Log.i(TAG,
						"no need  to search,null!=search,mFirstNoQwertySearchResultInput.length()="
								+ mFirstNoQwertySearchResultInput.length() + "["
								+ mFirstNoQwertySearchResultInput.toString() + "]"
								+ ";searchlen=" + keyword.length() + "["
								+ keyword + "]");
				return;
			} else {
				Log.i(TAG,
						"delete  mFirstNoQwertySearchResultInput, null!=search,mFirstNoQwertySearchResultInput.length()="
								+ mFirstNoQwertySearchResultInput.length()
								+ "["
								+ mFirstNoQwertySearchResultInput.toString()
								+ "]"
								+ ";searchlen="
								+ keyword.length()
								+ "["
								+ keyword + "]");
				mFirstNoQwertySearchResultInput.delete(0,mFirstNoQwertySearchResultInput.length());
			}
		}
		
		mQwertySearchAppInfos.clear();
		int baseAppInfosCount=baseAppInfos.size();
		for(int i=0; i<baseAppInfosCount; i++){
			PinyinSearchUnit labelPinyinSearchUnit=baseAppInfos.get(i).getLabelPinyinSearchUnit();
			boolean match=QwertyUtil.match(labelPinyinSearchUnit,keyword);
			
			
			if (true == match) {// search by LabelPinyinUnits;
				AppInfo appInfo = baseAppInfos.get(i);
				appInfo.setSearchByType(SearchByType.SearchByLabel);
				appInfo.setMatchKeywords(labelPinyinSearchUnit.getMatchKeyword().toString());
				appInfo.setMatchStartIndex(appInfo.getLabel().indexOf(appInfo.getMatchKeywords().toString()));
				appInfo.setMatchLength(appInfo.getMatchKeywords().length());
				
				mQwertySearchAppInfos.add(appInfo);

				continue;
			}
		}
		
		if (mQwertySearchAppInfos.size() <= 0) {
			if (mFirstNoQwertySearchResultInput.length() <= 0) {
				mFirstNoQwertySearchResultInput.append(keyword);
				Log.i(TAG,
						"no search result,null!=search,mFirstNoQwertySearchResultInput.length()="
								+ mFirstNoQwertySearchResultInput.length() + "["
								+ mFirstNoQwertySearchResultInput.toString() + "]"
								+ ";searchlen=" + keyword.length() + "["
								+ keyword + "]");
			} else {

			}
		}else{
			Collections.sort(mQwertySearchAppInfos, AppInfo.mSearchComparator);
		}
		return;
	}
	
	public void t9Search(String keyword){
		List<AppInfo> baseAppInfos=getBaseAppInfo();
		Log.i(TAG, "baseAppInfos["+baseAppInfos.size()+"]");
		if(null!=mT9SearchAppInfos){
			mT9SearchAppInfos.clear();
		}else{
			mT9SearchAppInfos=new ArrayList<AppInfo>();
		}
		
		if(TextUtils.isEmpty(keyword)){
			for(AppInfo ai:baseAppInfos){
				ai.setSearchByType(SearchByType.SearchByNull);
				ai.clearMatchKeywords();
				ai.setMatchStartIndex(-1);
				ai.setMatchLength(0);
			}
			
			mT9SearchAppInfos.addAll(baseAppInfos);
			
			mFirstNoT9SearchResultInput.delete(0, mFirstNoT9SearchResultInput.length());
			Log.i(TAG, "null==search,mFirstNoT9SearchResultInput.length()="+ mFirstNoT9SearchResultInput.length());
			return;
		}
		
		if (mFirstNoT9SearchResultInput.length() > 0) {
			if (keyword.contains(mFirstNoT9SearchResultInput.toString())) {
				Log.i(TAG,
						"no need  to search,null!=search,mFirstNoT9SearchResultInput.length()="
								+ mFirstNoT9SearchResultInput.length() + "["
								+ mFirstNoT9SearchResultInput.toString() + "]"
								+ ";searchlen=" + keyword.length() + "["
								+ keyword + "]");
				return;
			} else {
				Log.i(TAG,
						"delete  mFirstNoT9SearchResultInput, null!=search,mFirstNoT9SearchResultInput.length()="
								+ mFirstNoT9SearchResultInput.length()
								+ "["
								+ mFirstNoT9SearchResultInput.toString()
								+ "]"
								+ ";searchlen="
								+ keyword.length()
								+ "["
								+ keyword + "]");
				mFirstNoT9SearchResultInput.delete(0,mFirstNoT9SearchResultInput.length());
			}
		}
		
		mT9SearchAppInfos.clear();
		int baseAppInfosCount=baseAppInfos.size();
		for(int i=0; i<baseAppInfosCount; i++){
			PinyinSearchUnit labelPinyinSearchUnit=baseAppInfos.get(i).getLabelPinyinSearchUnit();
		
			boolean match=T9Util.match(labelPinyinSearchUnit,keyword);
			
			if (true == match) {// search by LabelPinyinUnits;
				AppInfo appInfo = baseAppInfos.get(i);
				appInfo.setSearchByType(SearchByType.SearchByLabel);
				appInfo.setMatchKeywords(labelPinyinSearchUnit.getMatchKeyword().toString());
				appInfo.setMatchStartIndex(appInfo.getLabel().indexOf(appInfo.getMatchKeywords().toString()));
				appInfo.setMatchLength(appInfo.getMatchKeywords().length());
				mT9SearchAppInfos.add(appInfo);

				continue;
			}
		}
		
		if (mT9SearchAppInfos.size() <= 0) {
			if (mFirstNoT9SearchResultInput.length() <= 0) {
				mFirstNoT9SearchResultInput.append(keyword);
				Log.i(TAG,
						"no search result,null!=search,mFirstNoT9SearchResultInput.length()="
								+ mFirstNoT9SearchResultInput.length() + "["
								+ mFirstNoT9SearchResultInput.toString() + "]"
								+ ";searchlen=" + keyword.length() + "["
								+ keyword + "]");
			} else {

			}
		}else{
			Collections.sort(mT9SearchAppInfos, AppInfo.mSearchComparator);
		}
		return;
	}
	
	public boolean isAppExist(String packageName){
		boolean appExist=false;
		do{
			if(TextUtils.isEmpty(packageName)){
				break;
			}
			
			for(AppInfo ai:mBaseAllAppInfos){
				if(ai.getPackageName().equals(packageName)){
					appExist=true;
					break;
				}
			}
		}while(false);
		
		return appExist;
	}
	
	private void clearAppInfoData(){
		
		if(null==mBaseAllAppInfos){
			mBaseAllAppInfos=new ArrayList<AppInfo>();
		}
		mBaseAllAppInfos.clear();
		
		if(null==mQwertySearchAppInfos){
			mQwertySearchAppInfos=new ArrayList<AppInfo>();
		}
		mQwertySearchAppInfos.clear();
		
		if(null==mT9SearchAppInfos){
			mT9SearchAppInfos=new ArrayList<AppInfo>();
		}
		mT9SearchAppInfos.clear();
		
		if(null==mFirstNoQwertySearchResultInput){
			mFirstNoQwertySearchResultInput=new StringBuffer();
		}else{
			mFirstNoQwertySearchResultInput.delete(0, mFirstNoQwertySearchResultInput.length());
		}
		
		if(null==mFirstNoT9SearchResultInput){
			mFirstNoT9SearchResultInput=new StringBuffer();
		}else{
			mFirstNoT9SearchResultInput.delete(0, mFirstNoT9SearchResultInput.length());
		}
		
		return;
	}
	
	private AppInfo getAppInfo(PackageManager pm,PackageInfo packageInfo){
		if((null==pm)||(null==packageInfo)){
			return null;
		}
		AppInfo appInfo=new AppInfo();
		appInfo.setIcon(packageInfo.applicationInfo.loadIcon(pm));
		appInfo.setLabel((String)packageInfo.applicationInfo.loadLabel(pm));
		appInfo.setPackageName(packageInfo.packageName);
		return appInfo;
		
	}
	private boolean isAppInfoLoading(){
		return ((null!=mLoadAppInfoTask)&&(mLoadAppInfoTask.getStatus()==Status.RUNNING));
	}
	
	private void parseAppInfo(List<AppInfo> appInfos){
		Log.i(TAG, "parseAppInfo");
		if(null==appInfos||appInfos.size()<1){
			if(null!=mOnAppInfoLoad){
				mOnAppInfoLoad.onAppInfoLoadFailed();
			}
			return;
		}
		
		Log.i(TAG, "before appInfos.size()"+ appInfos.size());
		mBaseAllAppInfos.clear();
		mBaseAllAppInfos.addAll(appInfos);
		Log.i(TAG, "after appInfos.size()"+ appInfos.size());
		
		if(null!=mOnAppInfoLoad){
			mOnAppInfoLoad.onAppInfoLoadSuccess();
		}
		
		return;
	}
	
	private String praseSortKey(String sortKey) {
		if (null == sortKey || sortKey.length() <= 0) {
			return null;
		}

		if ((sortKey.charAt(0) >= 'a' && sortKey.charAt(0) <= 'z')
				|| (sortKey.charAt(0) >= 'A' && sortKey.charAt(0) <= 'Z')) {
			return sortKey;
		}

		return String.valueOf(/*QuickAlphabeticBar.DEFAULT_INDEX_CHARACTER*/'#')
				+ sortKey;
	}
	
	private List<AppInfo> getBaseAppInfo(){
		List<AppInfo> baseAppInfos=null;
		switch (getCurrentAppType()) {
		//case ALL_APP:
		default:
			baseAppInfos=mBaseAllAppInfos;
			break;
		}
		return baseAppInfos;
	}
}
