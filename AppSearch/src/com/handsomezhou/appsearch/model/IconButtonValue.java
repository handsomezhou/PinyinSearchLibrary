
package com.handsomezhou.appsearch.model;

/**
 * custom IconButton's value
 * 
 * @author handsomezhou
 */
public class IconButtonValue {

    private Object mTag;// tag
    private int mIconSelectedUnfocused; // selected_unfocused Icon
    private int mIconSelectedFocused;   // selected_focused Icon
    private int mIconUnselected;        // unselected Icon
    private int mText;// text
    
    public IconButtonValue(Object tag, int iconSelectedUnfocused,int text) {
        super();
        initViewOption(tag, iconSelectedUnfocused, iconSelectedUnfocused, iconSelectedUnfocused, text);
    }
    
    public IconButtonValue(Object tag, int iconSelectedUnfocused, int iconUnselected, int text) {
        super();
        initViewOption(tag, iconSelectedUnfocused, iconSelectedUnfocused, iconUnselected, text);
    }

    public IconButtonValue(Object tag,int iconSelectedUnfocused, int iconSelectedFocused, 
            int iconUnselected, int text) {
        super();
        initViewOption(tag, iconSelectedUnfocused, iconSelectedFocused, iconUnselected, text);
    }

    private void initViewOption (Object tag,int iconSelectedUnfocused, int iconSelectedFocused, int iconUnselected, int text){
        setTag(tag);
        setIconSelectedUnfocused(iconSelectedUnfocused);
        setIconSelectedFocused(iconSelectedFocused);
        setIconUnselected(iconUnselected);
        setText(text);
    }
    public Object getTag() {
        return mTag;
    }

    public void setTag(Object tag) {
        mTag = tag;
    }

    public int getIconSelectedUnfocused() {
        return mIconSelectedUnfocused;
    }

    public void setIconSelectedUnfocused(int iconSelectedUnfocused) {
        mIconSelectedUnfocused = iconSelectedUnfocused;
    }
    
    public int getIconSelectedFocused() {
        return mIconSelectedFocused;
    }

    public void setIconSelectedFocused(int iconSelectedFocused) {
        mIconSelectedFocused = iconSelectedFocused;
    }

    public int getIconUnselected() {
        return mIconUnselected;
    }

    public void setIconUnselected(int iconUnselected) {
        mIconUnselected = iconUnselected;
    }

    public int getText() {
        return mText;
    }

    public void setText(int text) {
        mText = text;
    }
}
