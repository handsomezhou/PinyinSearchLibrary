package com.handsomezhou.appsearch.model;

import android.content.Context;

import com.handsomezhou.appsearch.view.IconButtonView;



public class IconButtonData {
    private IconButtonValue mIconButtonValue;
    private IconButtonView mIconButtonView;
   
    public IconButtonData(Context context,IconButtonValue iconButtonValue) {
        super();
        mIconButtonValue = iconButtonValue;
        mIconButtonView = new IconButtonView(context);
        mIconButtonView.getTitleTv().setText(mIconButtonValue.getText());;
        mIconButtonView.getIconIv().setBackgroundResource(mIconButtonValue.getIconUnselected());
    }
    
    public IconButtonValue getIconButtonValue() {
        return mIconButtonValue;
    }
    
    public void setIconButtonValue(IconButtonValue iconButtonValue) {
        mIconButtonValue = iconButtonValue;
    }
    
    public IconButtonView getIconButtonView() {
        return mIconButtonView;
    }
    
    public void setIconButtonView(IconButtonView iconButtonView) {
        mIconButtonView = iconButtonView;
    }
    
}
