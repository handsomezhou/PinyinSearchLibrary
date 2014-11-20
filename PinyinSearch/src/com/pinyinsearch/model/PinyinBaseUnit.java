package com.pinyinsearch.model;
/**
 * @description
 * as a single Pinyin units
 * for example:
 * 	"hao"	 ===> mPinyin="hao";mNumber="426";
 *  "???hao" ===> mPinyin="???hao";mNumber="???426"; 
 * @author handsomezhou
 * @date 2014-11-12
 */
public class PinyinBaseUnit {
	
	private String mPinyin;
	private String mNumber;
	
	public PinyinBaseUnit(){
		
	}
	
	public PinyinBaseUnit(String pinyin, String number) {
		super();
		mPinyin = pinyin;
		mNumber = number;
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
