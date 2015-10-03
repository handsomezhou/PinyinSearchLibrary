package com.handsomezhou.contactssearch.util;

import android.app.Activity;
import android.text.Html;
import android.text.Spanned;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class ViewUtil {
	public static void showTextNormal(TextView tv,String text){
		if((null==tv)||(null==text)){
			return;
		}
		
		tv.setText(text);
	}
	
	/**
	 * @param tv
	 * @param baseText
	 * @param highlightText
	 * if the string of highlightText is a subset of the string of baseText,highlight the string of highlightText.
	 */
	public static void showTextHighlight(TextView tv,String baseText,String highlightText){
		if((null==tv)||(null==baseText)||(null==highlightText)){
			return;
		}
		
		int index=baseText.indexOf(highlightText);
		if(index<0){
			tv.setText(baseText);
			return;
		}
		
		int len=highlightText.length();
		/**
		 *  "<u><font color=#FF8C00 >"+str+"</font></u>"; 	//with underline
		 *  "<font color=#FF8C00 >"+str+"</font>";			//without underline
		 *  
		 *  <color name="dark_orange">#FF8C00</color>
		 */
		Spanned spanned=Html.fromHtml(baseText.substring(0, index)+"<font color=#FF8C00 >" 
                + baseText.substring(index, index + len) + "</font>" 
                + baseText.substring(index + len, baseText.length()));
		
		tv.setText(spanned);
	}
	
	public static int getViewVisibility(View view) {
		if (null == view) {
			return View.GONE;
		}

		return view.getVisibility();
	}

	public static void showView(View view) {
		if (null == view) {
			return;
		}

		if (View.VISIBLE != view.getVisibility()) {
			view.setVisibility(View.VISIBLE);
		}
	}
	
	public static void invisibleView(View view) {
		if (null == view) {
			return;
		}
		if (View.INVISIBLE != view.getVisibility()) {
			view.setVisibility(View.INVISIBLE);
		}

		return;
	}
	
	public static void hideView(View view) {
		if (null == view) {
			return;
		}
		if (View.GONE != view.getVisibility()) {
			view.setVisibility(View.GONE);
		}

		return;
	}
	
	 /**
     * hide soft keyboard on android after clicking outside EditText
     * 
     * @param view
     */
    public static void setHideIme(final Activity activity,View view) {
        if(null==activity||null==view){
            return;
        }

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {

            view.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ViewUtil.hideSoftKeyboard(activity);
                    return false;
                }

            });
        }

        // If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setHideIme(activity,innerView);
            }
        }
    }
    
    /**
     * hide soft keyboard
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        if(null==activity){
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(null!=inputMethodManager){
            View view=activity.getCurrentFocus();
            if(null!=view){
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
          
        }
    }

}
