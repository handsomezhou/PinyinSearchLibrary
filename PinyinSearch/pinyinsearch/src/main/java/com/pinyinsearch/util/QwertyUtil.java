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

package com.pinyinsearch.util;

import com.pinyinsearch.model.PinyinBaseUnit;
import com.pinyinsearch.model.PinyinSearchUnit;
import com.pinyinsearch.model.PinyinUnit;

import java.util.List;

public class QwertyUtil {
	// private static final String TAG="QwertyMatchPinyinUnits";
	/**
	 * match PinyinSearchUnit
	 * 
	 * @param pinyinSearchUnit
	 * @param keyword
	 * @return true if match success,false otherwise.
	 */
	public static boolean match(PinyinSearchUnit pinyinSearchUnit,String keyword) {
		if ((null == pinyinSearchUnit) || (null == keyword)) {
			return false;
		}
		
		if(null==pinyinSearchUnit.getBaseData()||null==pinyinSearchUnit.getMatchKeyword()){
			return false;
		}

		pinyinSearchUnit.getMatchKeyword().delete(0, pinyinSearchUnit.getMatchKeyword().length());

		//search by  original string
		String searchLowerCase=keyword.toLowerCase();
		int index=pinyinSearchUnit.getBaseData().toLowerCase().indexOf(searchLowerCase);
		if(index>-1){
			pinyinSearchUnit.getMatchKeyword().append(pinyinSearchUnit.getBaseData().substring(index, index+searchLowerCase.length()));
			return true;
		}
		
		//search by pinyin characters
		int pinyinUnitsLength = pinyinSearchUnit.getPinyinUnits().size();
		StringBuffer searchBuffer = new StringBuffer();
		for (int i = 0; i < pinyinUnitsLength; i++) {
			int j = 0;
			pinyinSearchUnit.getMatchKeyword().delete(0,pinyinSearchUnit.getMatchKeyword().length());
			searchBuffer.delete(0, searchBuffer.length());
			searchBuffer.append(searchLowerCase);
			boolean found = findPinyinUnits(pinyinSearchUnit.getPinyinUnits(), i, j, pinyinSearchUnit.getBaseData(),searchBuffer, pinyinSearchUnit.getMatchKeyword());
			if (true == found) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @description match search string with pinyinUnits,if success,save the match keyword.
	 * @param pinyinUnits    		pinyinUnits head node index
	 * @param pinyinUnitIndex		pinyinUint Index
	 * @param qwertyPinyinUnitIndex	pinyinBaseUnit Index
	 * @param baseData				base data for search.
	 * @param searchBuffer			search keyword.
	 * @param matchKeyword		save the match keyword.
	 * @return true if find,false otherwise.
	 */
	private static boolean findPinyinUnits(final List<PinyinUnit> pinyinUnits,
			int pinyinUnitIndex, int qwertyPinyinUnitIndex, final String baseData,
			StringBuffer searchBuffer, StringBuffer matchKeyword) {
		if ((null == pinyinUnits)||(null == baseData)||(null == searchBuffer)||(null == matchKeyword)) {
			return false;
		}

		String search = searchBuffer.toString();
		if (search.length() <= 0) { // match success
			return true;
		}

		if (pinyinUnitIndex >= pinyinUnits.size()) {
			return false;
		}
		PinyinUnit pyUnit = pinyinUnits.get(pinyinUnitIndex);

		if (qwertyPinyinUnitIndex >= pyUnit.getPinyinBaseUnitIndex().size()) {
			return false;
		}

		PinyinBaseUnit pinyinBaseUnit = pyUnit.getPinyinBaseUnitIndex().get(qwertyPinyinUnitIndex);

		if (pyUnit.isPinyin()) {

			if (search.startsWith(String.valueOf(pinyinBaseUnit.getPinyin().charAt(0)))) {// match pinyin first character
				searchBuffer.delete(0, 1);// delete the match character
				matchKeyword.append(baseData.charAt(pyUnit.getStartPosition()));
				boolean found = findPinyinUnits(pinyinUnits,pinyinUnitIndex + 1, 0, baseData, searchBuffer,matchKeyword);
				if (true == found) {
					return true;
				} else {
					searchBuffer.insert(0, pinyinBaseUnit.getPinyin().charAt(0));
					matchKeyword.deleteCharAt(matchKeyword.length() - 1);
				}

			}

			if (pinyinBaseUnit.getPinyin().startsWith(search)) {
				// The string of "search" is the string of pinyinBaseUnit.getPinyin() of a subset. means match success.
				matchKeyword.append(baseData.charAt(pyUnit.getStartPosition()));
				searchBuffer.delete(0, searchBuffer.length());
				return true;

			} else if (search.startsWith(pinyinBaseUnit.getPinyin())) { // match quanpin success
				// The string of pinyinBaseUnit.getPinyin() is the string of "search" of a subset.
				searchBuffer.delete(0, pinyinBaseUnit.getPinyin().length());
				matchKeyword.append(baseData.charAt(pyUnit.getStartPosition()));
				boolean found = findPinyinUnits(pinyinUnits,pinyinUnitIndex+1, 0, baseData, searchBuffer,matchKeyword);
				if (true == found) {
					return true;
				} else {
					searchBuffer.insert(0, pinyinBaseUnit.getPinyin());
					matchKeyword.deleteCharAt(matchKeyword.length()-1);
				}
			} else { // mismatch
				boolean found = findPinyinUnits(pinyinUnits, pinyinUnitIndex,qwertyPinyinUnitIndex+1, baseData, searchBuffer,matchKeyword);
				if (found == true) {
					return true;
				}
			}

		} else { // non-pure Pinyin
			if (pinyinBaseUnit.getPinyin().startsWith(search)) {
				// The string of "search" is the string of pinyinBaseUnit.getPinyin() of a subset.
				int startIndex = 0;
				matchKeyword.append(baseData.substring(startIndex+pyUnit.getStartPosition(), startIndex+pyUnit.getStartPosition() + search.length()));
				searchBuffer.delete(0, searchBuffer.length());
				return true;
			} else if (search.startsWith(pinyinBaseUnit.getPinyin())) { // match all non-pure pinyin
				// The string of pinyinBaseUnit.getPinyin() is the string of "search" of a subset.
				int startIndex = 0;
				searchBuffer.delete(0, pinyinBaseUnit.getPinyin().length());
				matchKeyword.append(baseData.substring(startIndex+pyUnit.getStartPosition(),startIndex+pyUnit.getStartPosition()+pinyinBaseUnit.getPinyin().length()));
				boolean found = findPinyinUnits(pinyinUnits,pinyinUnitIndex+1, 0, baseData, searchBuffer,matchKeyword);
				if (true == found) {
					return true;
				} else {
					searchBuffer.insert(0, pinyinBaseUnit.getPinyin());
					matchKeyword.delete(matchKeyword.length()-pinyinBaseUnit.getPinyin().length(),matchKeyword.length());
				}
			} else if ((matchKeyword.length() <= 0)) {
				if (pinyinBaseUnit.getPinyin().contains(search)) {
					int index = pinyinBaseUnit.getPinyin().indexOf(search);
					matchKeyword.append(baseData.substring(index+pyUnit.getStartPosition(),index+pyUnit.getStartPosition()+search.length()));
					searchBuffer.delete(0, searchBuffer.length());
					return true;
				} else {
					// match case:[Non-Chinese characters]+[Chinese characters]
					// for example:baseData="Tony测试"; match this case:"onycs"
					// start [Non-Chinese characters]+[Chinese characters]
					int numLength = pinyinBaseUnit.getPinyin().length();
					for (int i = 0; i < numLength; i++) {
						String subStr = pinyinBaseUnit.getPinyin().substring(i);
						if (search.startsWith(subStr)) {
							searchBuffer.delete(0, subStr.length());
							matchKeyword.append(baseData.substring(i+pyUnit.getStartPosition(),i+pyUnit.getStartPosition()+subStr.length()));
							boolean found = findPinyinUnits(pinyinUnits,pinyinUnitIndex+1, 0, baseData,searchBuffer, matchKeyword);
							if (true == found) {
								return true;
							} else {
								searchBuffer.insert(0, pinyinBaseUnit.getPinyin().substring(i));
								matchKeyword.delete(matchKeyword.length()-subStr.length(),matchKeyword.length());
							}

						}
					}
					// end [Non-Chinese characters]+[Chinese characters]

					// in fact,if pyUnit.isPinyin()==false, pyUnit.getQwertyPinyinUnitIndex().size()==1. The function of findPinyinUnits() will return false.
					boolean found = findPinyinUnits(pinyinUnits,pinyinUnitIndex, qwertyPinyinUnitIndex + 1, baseData,searchBuffer, matchKeyword);
					if (true == found) {
						return true;
					}
				}
			} else { // mismatch
				// in fact,if pyUnit.isPinyin()==false, pyUnit.getQwertyPinyinUnitIndex().size()==1. The function of findPinyinUnits() will return false.
				boolean found = findPinyinUnits(pinyinUnits, pinyinUnitIndex,qwertyPinyinUnitIndex+1, baseData, searchBuffer,matchKeyword);
				if (true == found) {
					return true;
				}
			}
		}
		return false;
	}
}
