package com.handsomezhou.appsearch.view;



import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handsomezhou.appsearch.R;

public class IconButtonView extends RelativeLayout {
	private static final int TITLE_TEXT_VIEW_ID=1;
    private Context mContext;
    private ImageView mIconIv;
    private TextView mTitleTv;
   
    public IconButtonView(Context context) {
        super(context);
        mContext=context;
        initView(false);
    }

    public IconButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        initView(false);
    }
    
    public IconButtonView(Context context, boolean hideIcon){
    	 super(context);
         mContext=context;
         initView(hideIcon);
    }

    public ImageView getIconIv() {
        return mIconIv;
    }

    public void setIconIv(ImageView iconIv) {
        mIconIv = iconIv;
    }

    public TextView getTitleTv() {
        return mTitleTv;
    }

    public void setTitleTv(TextView titleTv) {
        mTitleTv = titleTv;
    }
    
    private void initView(boolean hideIcon){
    	 this.removeAllViews();
        
		mTitleTv = new TextView(mContext);
		mTitleTv.setId(TITLE_TEXT_VIEW_ID);
		// mTitleTv.setTextSize(mContext.getResources().getDimension(R.dimen.tab_index_text_size));

		mTitleTv.setGravity(Gravity.CENTER);
		RelativeLayout.LayoutParams titleTvLp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		titleTvLp.addRule(RelativeLayout.CENTER_VERTICAL);
		titleTvLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		this.addView(mTitleTv, titleTvLp);

		mIconIv = new ImageView(mContext);

		int layoutWidth = (int) mContext.getResources().getDimension(
				R.dimen.tab_index_icon_width);
		int layoutHeight = (int) mContext.getResources().getDimension(
				R.dimen.tab_index_icon_height);
		/*
		 * int layoutWidth= LayoutParams.WRAP_CONTENT;
		 *  intlayoutHeight=LayoutParams.WRAP_CONTENT;
		 */
		RelativeLayout.LayoutParams iconIvLp = new RelativeLayout.LayoutParams(
				layoutWidth, layoutHeight);
		iconIvLp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		iconIvLp.addRule(RelativeLayout.ABOVE, mTitleTv.getId());
		if(hideIcon){
			iconIvLp.addRule(GONE);
		}else{
			iconIvLp.addRule(VISIBLE);
		}
		
         
        this.addView(mIconIv,iconIvLp);
        
        return;
    }
}
