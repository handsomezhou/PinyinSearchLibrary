package com.handsomezhou.pinyinsearchdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

/**
 * 
 * @author lake&tony
 * @date 2014-12-15
 */
public class QuickAlphabeticBar extends View {
	// private static final String TAG="QuickAlphabeticBar";
	public static final char DEFAULT_INDEX_CHARACTER = '#';
	private static char[] mSelectCharacters = { DEFAULT_INDEX_CHARACTER, 'A',
			'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
			'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	private SectionIndexer mSectionIndexer;
	private ListView mQuickAlphabeticLv;
	private TextView mSelectCharTv;

	private char mCurrentSelectChar;

	public QuickAlphabeticBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public static char[] getSelectCharacters() {
		return mSelectCharacters;
	}

	/*public static void setSelectCharacters(char[] mSelectCharacters) {
		QuickAlphabeticBar.mSelectCharacters = mSelectCharacters;
	}*/
	
	public SectionIndexer getSectionIndexer() {
		return mSectionIndexer;
	}

	public void setSectionIndexer(SectionIndexer sectionIndexer) {
		mSectionIndexer = sectionIndexer;
	}

	public ListView getQuickAlphabeticLv() {
		return mQuickAlphabeticLv;
	}

	public void setQuickAlphabeticLv(ListView quickAlphabeticLv) {
		mQuickAlphabeticLv = quickAlphabeticLv;
	}

	public TextView getSelectCharTv() {
		return mSelectCharTv;
	}

	public void setSelectCharTv(TextView selectCharTv) {
		mSelectCharTv = selectCharTv;
	}

	public char getCurrentSelectChar() {
		return mCurrentSelectChar;
	}

	public void setCurrentSelectChar(char currentSelectChar) {
		mCurrentSelectChar = currentSelectChar;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		int index = getCurrentIndex(event);
		if((event.getAction()==MotionEvent.ACTION_DOWN)||(event.getAction()==MotionEvent.ACTION_MOVE)){
			if(null!=mSelectCharTv){	//show select char
				mSelectCharTv.setVisibility(View.VISIBLE);
				mSelectCharTv.setText(String.valueOf(mSelectCharacters[index]));
			}
			
			//reference: http://blog.csdn.net/jack_l1/article/details/14165291
			if(null!=mSectionIndexer){
				int position=mSectionIndexer.getPositionForSection(mSelectCharacters[index]);
				if(position<0){
					return true;
				}
				if(null!=mQuickAlphabeticLv){
					mQuickAlphabeticLv.setSelection(position);
				}
			}
		}else if(event.getAction()==MotionEvent.ACTION_UP){
			if(null!=mSelectCharTv){	//hide select char
				mSelectCharTv.setVisibility(View.GONE);
			}
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		float xPos=getMeasuredWidth()/2;
		float yPos=0;
		if(mSelectCharacters.length>0){
			float sigleHeight=getMeasuredHeight()/mSelectCharacters.length;
			for(int i=0; i<mSelectCharacters.length;i++){
				//xPos=getMeasuredWidth()/2- mOtherIndexPaint.measureText(String.valueOf(mSelectCharacters[i])); 
				yPos=(i+1)*sigleHeight;
				canvas.drawText(String.valueOf(mSelectCharacters[i]), xPos,yPos, mSelectCharacters[i] == mCurrentSelectChar ? mCurrentIndexPaint
						: mOtherIndexPaint);
			}
		}
		
		this.invalidate();
		super.onDraw(canvas);
	}

	private int getCurrentIndex(MotionEvent event) {
		if (null == event) {
			return 0;
		}

		int y = (int) event.getY();
		int index = y / (getMeasuredHeight() / mSelectCharacters.length);
		if (index < 0) {
			index = 0;
		} else if (index >= mSelectCharacters.length) {
			index = mSelectCharacters.length - 1;
		}

		return index;
	}
	
	private Paint mCurrentIndexPaint=new Paint();
	{
		mCurrentIndexPaint.setColor(Color.BLUE);
		mCurrentIndexPaint.setTextSize(24);
		mCurrentIndexPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mCurrentIndexPaint.setTextAlign(Paint.Align.CENTER);
		
		
	}
	
	private Paint mOtherIndexPaint=new Paint();
	{
		mOtherIndexPaint.setColor(Color.WHITE);
		mOtherIndexPaint.setTextSize(24);
		mOtherIndexPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mOtherIndexPaint.setTextAlign(Paint.Align.CENTER);
	}
}
