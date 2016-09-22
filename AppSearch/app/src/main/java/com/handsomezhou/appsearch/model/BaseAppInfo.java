package com.handsomezhou.appsearch.model;

import android.graphics.drawable.Drawable;

public class BaseAppInfo implements Cloneable{
	private String mLabel;
	private Drawable mIcon;
	private String mPackageName;
	
	
	public BaseAppInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BaseAppInfo(String label, Drawable icon, String packageName) {
		super();
		mLabel = label;
		mIcon = icon;
		mPackageName = packageName;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	public String getLabel() {
		return mLabel;
	}
	
	public void setLabel(String label) {
		mLabel = label;
	}
	
	public Drawable getIcon() {
		return mIcon;
	}
	
	public void setIcon(Drawable icon) {
		mIcon = icon;
	}
	
	public String getPackageName() {
		return mPackageName;
	}
	
	public void setPackageName(String packageName) {
		mPackageName = packageName;
	}
	
}
