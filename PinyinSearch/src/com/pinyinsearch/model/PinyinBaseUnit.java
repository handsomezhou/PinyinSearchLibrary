package com.pinyinsearch.model;
/**
 * @description
 * as a single Pinyin units
 * for example:
 * 	"hao"		===>mOriginalString="hao"; mPinyin="hao";mNumber="426";
 *  "???hao" 	===>mOriginalString="???hao"; mPinyin="???hao";mNumber="???426"; 
 *  "周"			===>mOriginalString="周"; mPinyin="zhou";mNumber="9468";
 * @author handsomezhou
 * @date 2014-11-12
 */
public class PinyinBaseUnit {
	private String mOriginalString;
	private String mPinyin;
	private String mNumber;
	
	public PinyinBaseUnit(){
		
	}
	
	public PinyinBaseUnit(String originalString,String pinyin, String number) {
		super();
		mOriginalString=originalString;
		mPinyin = pinyin;
		mNumber = number;
	}
	
	public String getOriginalString() {
		return mOriginalString;
	}

	public void setOriginalString(String originalString) {
		mOriginalString = originalString;
	}
	
	public String getPinyin() {
		return mPinyin;
	}
	
	public void setPinyin(String pinyin) {
		mPinyin = pinyin;
	}
	
	public String getNumber() {
		return mNumber;
	}
	
	public void setNumber(String number) {
		mNumber = number;
	}
}
