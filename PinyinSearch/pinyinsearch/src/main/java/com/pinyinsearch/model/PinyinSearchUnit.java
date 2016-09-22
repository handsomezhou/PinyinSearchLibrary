/*
 * Copyright 2015 handsomezhou
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

public class PinyinSearchUnit implements Cloneable{
	private String mBaseData;  //the original string
	private List<PinyinUnit> mPinyinUnits;
	private StringBuffer mMatchKeyword;//the sub string of base data which search by key word
	
	
	public PinyinSearchUnit() {
		super();
		initPinyinSearchUnit();
	}

	public PinyinSearchUnit(String baseData) {
		super();
		mBaseData = baseData;
		initPinyinSearchUnit();
	}

	public String getBaseData() {
		return mBaseData;
	}

	public void setBaseData(String baseData) {
		mBaseData = baseData;
	}
	
	public List<PinyinUnit> getPinyinUnits() {
		return mPinyinUnits;
	}

	public void setPinyinUnits(List<PinyinUnit> pinyinUnits) {
		mPinyinUnits = pinyinUnits;
	}
	
	public StringBuffer getMatchKeyword() {
		return mMatchKeyword;
	}

	public void setMatchKeyword(StringBuffer matchKeyword) {
		mMatchKeyword = matchKeyword;
	}

	
	@Override
	public Object clone() throws CloneNotSupportedException {
		PinyinSearchUnit obj=(PinyinSearchUnit) super.clone();
		obj.mPinyinUnits=new ArrayList<PinyinUnit>();
		for(PinyinUnit pu:mPinyinUnits){
			obj.mPinyinUnits.add((PinyinUnit) pu.clone());
		}
		
		return obj;
	}

	private void initPinyinSearchUnit(){
		mPinyinUnits=new ArrayList<PinyinUnit>();
		mMatchKeyword=new StringBuffer();
	}
}
