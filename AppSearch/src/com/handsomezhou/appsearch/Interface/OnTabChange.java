package com.handsomezhou.appsearch.Interface;

/**
 *
 * listener tab change(from one tab change to other tab) and click current
 * tab(click current tab)
 */
public interface OnTabChange {
	public enum TAB_CHANGE_STATE{
		TAB_SELECTED_UNFOCUSED,
		TAB_SELECTED_FOCUSED,
		TAB_UNSELECTED,
	}
	
	public void onChangeToTab(Object fromTab, Object toTab, TAB_CHANGE_STATE tabChangeState);

	public void onClickTab(Object currentTab,TAB_CHANGE_STATE tabChangeState);
}
