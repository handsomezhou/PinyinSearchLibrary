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

import java.util.ArrayList;
import java.util.List;

/**
 * PinyinUnit as a base data structure to save the string that Chinese characters  converted to Pinyin characters.
 * for example:
 * Reference: http://www.cnblogs.com/bomo/archive/2012/12/25/2833081.html
 * Chinese characters:"Hi你说了什么git???"
 * Pinyin:
 * 		Hi-Hi					===>mPinyin=false, 	mPinyinBaseUnitIndex.size=1, mStartPosition=0	
 * 		{[mPinyinBaseUnitIndex.get(0).getPinyin()="Hi",mPinyinBaseUnitIndex.get(0).getNumber="Hi"];}
 * 
 * 		你->ni3					===>mPinyin=true, 	mPinyinBaseUnitIndex.size=1,	mStartPosition=2
 * 		{[mPinyinBaseUnitIndex.get(0).getPinyin()="ni",mPinyinBaseUnitIndex.get(0).getNumber="64"];}
 * 
 *     	说->shuo1,shui4,yue4 	===>mPinyin=true,	mPinyinBaseUnitIndex.size=3,	mStartPosition=3
 *     	{
 *     	[mPinyinBaseUnitIndex.get(0).getPinyin()="shuo",mPinyinBaseUnitIndex.get(0).getNumber="7486"];
 *     	[mPinyinBaseUnitIndex.get(1).getPinyin()="shui",mPinyinBaseUnitIndex.get(1).getNumber="7484"];
 *     	[mPinyinBaseUnitIndex.get(2).getPinyin()="yue",mPinyinBaseUnitIndex.get(2).getNumber="983"];}
 *     
 *      了->le5,liao3,liao4  	===>mPinyin=true, 	mPinyinBaseUnitIndex.size=2,	mStartPosition=4
 *     	{
 *     	[mPinyinBaseUnitIndex.get(0).getPinyin()="le",mPinyinBaseUnitIndex.get(0).getNumber="53"];
 *     	[mPinyinBaseUnitIndex.get(1).getPinyin()="liao",mPinyinBaseUnitIndex.get(1).getNumber="5426"];}
 *     
 * 		什->shen2,shi2,she2		===>mPinyin=true, 	mPinyinBaseUnitIndex.size=3, mStartPosition=5
 * 		{
 *     	[mPinyinBaseUnitIndex.get(0).getPinyin()="shen",mPinyinBaseUnitIndex.get(0).getNumber="7436"];
 *     	[mPinyinBaseUnitIndex.get(1).getPinyin()="shi",mPinyinBaseUnitIndex.get(1).getNumber="744"];
 *     	[mPinyinBaseUnitIndex.get(2).getPinyin()="she",mPinyinBaseUnitIndex.get(2).getNumber="743"];}
 * 
 * 		么->me5,ma5,yao1			===>mPinyin=true,	mPinyinBaseUnitIndex.size=3, mStartPosition=6
 * 		{
 *     	[mPinyinBaseUnitIndex.get(0).getPinyin()="me",mPinyinBaseUnitIndex.get(0).getNumber="63"];
 *     	[mPinyinBaseUnitIndex.get(1).getPinyin()="ma",mPinyinBaseUnitIndex.get(1).getNumber="62"];
 *     	[mPinyinBaseUnitIndex.get(2).getPinyin()="yao",mPinyinBaseUnitIndex.get(2).getNumber="926"];}
 * 
 * 		git???->git???				===>mPinyin=false, 	mPinyinBaseUnitIndex.size=1, mStartPosition=7	
 * 		{[mPinyinBaseUnitIndex.get(0).getPinyin()="git???",mPinyinBaseUnitIndex.get(0).getNumber="448???"];}
 * 
 */

public class PinyinUnit implements Cloneable{
	//Whether Pinyin
	private boolean mPinyin;
	private int mStartPosition; //save starting index position that the variables in the original string. 
	/*
	 * save the string which single Chinese characters Pinyin(include Multiple Pinyin),or continuous non-kanji characters.
	 * if mPinyinBaseUnitIndex.size not more than 1, it means the is not Polyphonic characters.
	 */
	private List<PinyinBaseUnit> mPinyinBaseUnitIndex;

	public PinyinUnit() {
		mPinyin=false;
		mStartPosition=-1;
		mPinyinBaseUnitIndex=new ArrayList<PinyinBaseUnit>();
	}

	public boolean isPinyin() {
		return mPinyin;
	}

	public void setPinyin(boolean pinyin) {
		mPinyin = pinyin;
	}

	public int getStartPosition() {
		return mStartPosition;
	}

	public void setStartPosition(int startPosition) {
		mStartPosition = startPosition;
	}
	
	public List<PinyinBaseUnit> getPinyinBaseUnitIndex() {
		return mPinyinBaseUnitIndex;
	}

	public void setStringIndex(List<PinyinBaseUnit> stringIndex) {
		mPinyinBaseUnitIndex = stringIndex;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		PinyinUnit obj=(PinyinUnit)super.clone();
		
		obj.mPinyinBaseUnitIndex=new ArrayList<PinyinBaseUnit>();
		for(PinyinBaseUnit pbu:mPinyinBaseUnitIndex){
			obj.mPinyinBaseUnitIndex.add((PinyinBaseUnit)pbu.clone());
		}
		
		return obj;
	}
	
}
