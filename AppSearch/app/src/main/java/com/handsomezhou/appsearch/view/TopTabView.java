package com.handsomezhou.appsearch.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.handsomezhou.appsearch.R;
import com.handsomezhou.appsearch.Interface.OnTabChange;
import com.handsomezhou.appsearch.Interface.OnTabChange.TAB_CHANGE_STATE;
import com.handsomezhou.appsearch.model.IconButtonData;



public class TopTabView extends LinearLayout implements OnClickListener{
    private static final int PADDING_DEFAULT=0;
    private static final int PADDING_LEFT_DEFAULT=0;
    private static final int PADDING_TOP_DEFAULT=0;
    private static final int PADDING_RIGHT_DEFAULT=0;
    private static final int PADDING_BOTTOM_DEFAULT=0;
    
    private static final int MARGIN_DEFAULT=0;
    private static final int MARGIN_LEFT_DEFAULT=0;
    private static final int MARGIN_TOP_DEFAULT=0;
    private static final int MARGIN_RIGHT_DEFAULT=0;
    private static final int MARGIN_BOTTOM_DEFAULT=0;
 
   
    private Context mContext;
	private List<IconButtonData> mIconButtonData;//data
	private OnTabChange  mOnTabChange;
	private Object mCurrentTab=null;
	private int mLastIconResId;
	
	private int mTextColorFocused;
	private int mTextColorUnfocused;
	private int mTextColorUnselected;
	private boolean mHideIcon;

	public TopTabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		initData();
		initView();
		initListener();		
	}
	
	/*Start: OnClickListener*/
	@Override
	public void onClick(View v) {
		
		if(null==v||null==v.getTag()){
			return;
		}
		
		Object toTab=v.getTag();
		Object fromTab=getCurrentTab();
		changeToTab(fromTab, toTab);
		
	}
	/*End: OnClickListener*/
	
	public void addIconButtonData(IconButtonData iconButtonData){
	    
	    if(null==mIconButtonData){
	        mIconButtonData=new ArrayList<IconButtonData>();
	    }
	    
	    mIconButtonData.add(iconButtonData);
	    addIconButtonView(mIconButtonData.get(mIconButtonData.size()-1).getIconButtonView());
	    setCurrentTab(mIconButtonData.get(0).getIconButtonValue().getTag());
	    mIconButtonData.get(0).getIconButtonView().getIconIv().setBackgroundResource( mIconButtonData.get(0).getIconButtonValue().getIconSelectedFocused());
	    mIconButtonData.get(0).getIconButtonView().getTitleTv().setTextColor(getTextColorFocused());
	    setLastIconResId(mIconButtonData.get(0).getIconButtonValue().getIconSelectedFocused());
	    
	    mIconButtonData.get(mIconButtonData.size()-1).getIconButtonView().setOnClickListener(this);
	    mIconButtonData.get(mIconButtonData.size()-1).getIconButtonView().setTag(mIconButtonData.get(mIconButtonData.size()-1).getIconButtonValue().getTag());
	    mIconButtonData.get(mIconButtonData.size()-1).getIconButtonView().getTitleTv().setTextColor(getTextColorUnselected());

	    
	    if(isHideIcon()){
	    	mIconButtonData.get(mIconButtonData.size()-1).getIconButtonView().getIconIv().setVisibility(View.GONE);
	    }else{
	    	mIconButtonData.get(mIconButtonData.size()-1).getIconButtonView().getIconIv().setVisibility(View.VISIBLE);
	    }
	    return;
	}
	
	public void setCurrentTabItem(Object tag){
		if(null==tag){
			return;
		}
		
		setCurrentTab(tag);
		for(IconButtonData ibd:mIconButtonData){
			if(ibd.getIconButtonValue().getTag().equals(tag)){
				ibd.getIconButtonView().getIconIv().setBackgroundResource(ibd.getIconButtonValue().getIconSelectedFocused());
				ibd.getIconButtonView().getTitleTv().setTextColor(getTextColorFocused());
				setLastIconResId(ibd.getIconButtonValue().getIconSelectedFocused());
			}else{
				ibd.getIconButtonView().getIconIv().setBackgroundResource(ibd.getIconButtonValue().getIconUnselected());
				ibd.getIconButtonView().getTitleTv().setTextColor(getTextColorUnselected());
			}
		}
	}
	
	public void removeIconButtonData(IconButtonData iconButtonData){
	    
	}
	
	public OnTabChange getOnTabChange() {
		return mOnTabChange;
	}

	public void setOnTabChange(OnTabChange onTabChange) {
		mOnTabChange = onTabChange;
	}
	
	public Object getCurrentTab() {
		return mCurrentTab;
	}

	public void setCurrentTab(Object currentTab) {
		mCurrentTab = currentTab;
	}
	
	public int getTextColorFocused() {
		return mTextColorFocused;
	}

	public void setTextColorFocused(int textColorFocused) {
		mTextColorFocused = textColorFocused;
	}

	public int getTextColorUnfocused() {
		return mTextColorUnfocused;
	}

	public void setTextColorUnfocused(int textColorUnfocused) {
		mTextColorUnfocused = textColorUnfocused;
	}

	public int getTextColorUnselected() {
		return mTextColorUnselected;
	}

	public void setTextColorUnselected(int textColorUnselected) {
		mTextColorUnselected = textColorUnselected;
	}

	
	public boolean isHideIcon() {
		return mHideIcon;
	}

	public void setHideIcon(boolean hideIcon) {
		mHideIcon = hideIcon;
	}

	private void initData(){
	    mIconButtonData=new ArrayList<IconButtonData>();
	    setHideIcon(false);
	    return;
	}
	
	@SuppressWarnings("static-access")
    private void initView(){
	    this.setOrientation(this.HORIZONTAL);
	    //this.setPadding(PADDING_LEFT_DEFAULT, PADDING_TOP_DEFAULT, PADDING_RIGHT_DEFAULT, PADDING_BOTTOM_DEFAULT);
	    LayoutParams lp= new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    //lp.setMargins(MARGIN_LEFT_DEFAULT, MARGIN_TOP_DEFAULT, MARGIN_RIGHT_DEFAULT, MARGIN_BOTTOM_DEFAULT);
	   
	    this.setLayoutParams(lp);
	    this.setBackgroundResource(R.color.ghost_white); 
	    
	   
	    return;
	}
	
	private void initListener(){
	    
	    return;
	}
	
	private int getLastIconResId() {
		return mLastIconResId;
	}

	private void setLastIconResId(int lastIconResId) {
		mLastIconResId = lastIconResId;
	}


	private void addIconButtonView(IconButtonView iconButtonView){
	    if(null==iconButtonView){
	        return;
	    }
	    LayoutParams lp= new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
        lp.setMargins(MARGIN_LEFT_DEFAULT, MARGIN_TOP_DEFAULT, MARGIN_RIGHT_DEFAULT, MARGIN_BOTTOM_DEFAULT);

        iconButtonView.setLayoutParams(lp);
        this.addView(iconButtonView);
	}
	
	private void changeToTab(Object fromTab, Object toTab) {
		if(fromTab!=toTab){//change tab
			setCurrentTab(toTab);
			if(null!=mOnTabChange){
				mOnTabChange.onChangeToTab(fromTab, toTab, TAB_CHANGE_STATE.TAB_SELECTED_FOCUSED);
				IconButtonData fromTabIconButtonData=getIconButtonData(fromTab);
				if(null!=fromTabIconButtonData){
					fromTabIconButtonData.getIconButtonView().getIconIv().setBackgroundResource(fromTabIconButtonData.getIconButtonValue().getIconUnselected());
					fromTabIconButtonData.getIconButtonView().getTitleTv().setTextColor(getTextColorUnselected());
					
				}
				IconButtonData toTabIconButtonData=getIconButtonData(toTab);
				if(null!=toTabIconButtonData){
					toTabIconButtonData.getIconButtonView().getIconIv().setBackgroundResource(toTabIconButtonData.getIconButtonValue().getIconSelectedFocused());
					toTabIconButtonData.getIconButtonView().getTitleTv().setTextColor(getTextColorFocused());
				}
				
				setLastIconResId(toTabIconButtonData.getIconButtonValue().getIconSelectedFocused());
			}
		}else{//click tab
			if(null!=mOnTabChange){
				
				IconButtonData toTabIconButtonData=getIconButtonData(toTab);
				if(null!=toTabIconButtonData){
					if(getLastIconResId()==toTabIconButtonData.getIconButtonValue().getIconSelectedUnfocused()){
						toTabIconButtonData.getIconButtonView().getIconIv().setBackgroundResource(toTabIconButtonData.getIconButtonValue().getIconSelectedFocused());
						toTabIconButtonData.getIconButtonView().getTitleTv().setTextColor(getTextColorFocused());
						setLastIconResId(toTabIconButtonData.getIconButtonValue().getIconSelectedFocused());
						mOnTabChange.onClickTab(toTab,TAB_CHANGE_STATE.TAB_SELECTED_FOCUSED);
					}else{
						toTabIconButtonData.getIconButtonView().getIconIv().setBackgroundResource(toTabIconButtonData.getIconButtonValue().getIconSelectedUnfocused());
						toTabIconButtonData.getIconButtonView().getTitleTv().setTextColor(getTextColorUnfocused());
						setLastIconResId(toTabIconButtonData.getIconButtonValue().getIconSelectedUnfocused());
						mOnTabChange.onClickTab(toTab,TAB_CHANGE_STATE.TAB_SELECTED_UNFOCUSED);
					}
				}
			}
			
		}
	}
	
	private IconButtonData getIconButtonData(Object object){
		IconButtonData iconButtonData=null;
		do{
			if(null==object){
				break;
			}
			
			for(IconButtonData ibd:mIconButtonData){
				if(ibd.getIconButtonValue().getTag().equals(object)){
					iconButtonData=ibd;
					break;
				}
			}
			break;
		}while(false);
		return iconButtonData;
	}
}
