/*
 * Copyright 2014 handsomezhou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pinyinsearch.model;
/**
 * As a single Pinyin units
 * for example:
 * 	"hao"		===>mOriginalString="hao"; mPinyin="hao";mNumber="426";
 *  "???hao" 	===>mOriginalString="???hao"; mPinyin="???hao";mNumber="???426"; 
 *  "周"			===>mOriginalString="周"; mPinyin="zhou";mNumber="9468";
 */
public class PinyinBaseUnit implements Cloneable{
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
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
