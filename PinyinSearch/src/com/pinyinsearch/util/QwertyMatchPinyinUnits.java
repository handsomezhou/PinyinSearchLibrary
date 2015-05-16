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

import java.util.List;
import android.annotation.SuppressLint;
import com.pinyinsearch.model.PinyinBaseUnit;
import com.pinyinsearch.model.PinyinUnit;

@SuppressLint("DefaultLocale")
public class QwertyMatchPinyinUnits {
	// private static final String TAG="QwertyMatchPinyinUnits";
	/**
	 * Match Pinyin Units
	 * @param pinyinUnits
	 * @param baseData			the original string which be parsed to PinyinUnit
	 * @param search			search key words
	 * @param chineseKeyWord	the sub string of base data
	 * @return true if match success,false otherwise.
	 */
	@SuppressLint("DefaultLocale")
	public static boolean matchPinyinUnits(final List<PinyinUnit> pinyinUnits,
			final String baseData, String search, StringBuffer chineseKeyWord) {
		if ((null == pinyinUnits) || (null == search)
				|| (null == chineseKeyWord)) {
			return false;
		}

		StringBuffer matchSearch = new StringBuffer();
		matchSearch.delete(0, matchSearch.length());
		chineseKeyWord.delete(0, chineseKeyWord.length());

		//search by  original string
		String searchLowerCase=search.toLowerCase();
		int index=baseData.toLowerCase().indexOf(searchLowerCase);
		if(index>-1){
			chineseKeyWord.append(baseData.substring(index, index+searchLowerCase.length()));
			return true;
		}
		
		//search by pinyin characters
		int pinyinUnitsLength = pinyinUnits.size();
		StringBuffer searchBuffer = new StringBuffer();
		for (int i = 0; i < pinyinUnitsLength; i++) {
			int j = 0;
			chineseKeyWord.delete(0, chineseKeyWord.length());
			searchBuffer.delete(0, searchBuffer.length());
			searchBuffer.append(searchLowerCase);
			boolean found = findPinyinUnits(pinyinUnits, i, j, baseData,searchBuffer, chineseKeyWord);
			if (true == found) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @description match search string with pinyinUnits,if success,save the Chinese keywords.
	 * @param pinyinUnits    		pinyinUnits head node index
	 * @param pinyinUnitIndex		pinyinUint Index
	 * @param qwertyPinyinUnitIndex	pinyinBaseUnit Index
	 * @param baseData				base data for search.
	 * @param searchBuffer			search keyword.
	 * @param chineseKeyWord		save the Chinese keyword.
	 * @return true if find,false otherwise.
	 */
	private static boolean findPinyinUnits(final List<PinyinUnit> pinyinUnits,
			int pinyinUnitIndex, int qwertyPinyinUnitIndex, final String baseData,
			StringBuffer searchBuffer, StringBuffer chineseKeyWord) {
		if ((null == pinyinUnits)||(null == baseData)||(null == searchBuffer)||(null == chineseKeyWord)) {
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
				chineseKeyWord.append(baseData.charAt(pyUnit.getStartPosition()));
				boolean found = findPinyinUnits(pinyinUnits,pinyinUnitIndex + 1, 0, baseData, searchBuffer,chineseKeyWord);
				if (true == found) {
					return true;
				} else {
					searchBuffer.insert(0, pinyinBaseUnit.getPinyin().charAt(0));
					chineseKeyWord.deleteCharAt(chineseKeyWord.length() - 1);
				}

			}

			if (pinyinBaseUnit.getPinyin().startsWith(search)) {
				// The string of "search" is the string of pinyinBaseUnit.getPinyin() of a subset. means match success.
				chineseKeyWord.append(baseData.charAt(pyUnit.getStartPosition()));
				searchBuffer.delete(0, searchBuffer.length());
				return true;

			} else if (search.startsWith(pinyinBaseUnit.getPinyin())) { // match quanpin success
				// The string of pinyinBaseUnit.getPinyin() is the string of "search" of a subset.
				searchBuffer.delete(0, pinyinBaseUnit.getPinyin().length());
				chineseKeyWord.append(baseData.charAt(pyUnit.getStartPosition()));
				boolean found = findPinyinUnits(pinyinUnits,pinyinUnitIndex+1, 0, baseData, searchBuffer,chineseKeyWord);
				if (true == found) {
					return true;
				} else {
					searchBuffer.insert(0, pinyinBaseUnit.getPinyin());
					chineseKeyWord.deleteCharAt(chineseKeyWord.length()-1);
				}
			} else { // mismatch
				boolean found = findPinyinUnits(pinyinUnits, pinyinUnitIndex,qwertyPinyinUnitIndex+1, baseData, searchBuffer,chineseKeyWord);
				if (found == true) {
					return true;
				}
			}

		} else { // non-pure Pinyin
			if (pinyinBaseUnit.getPinyin().startsWith(search)) {
				// The string of "search" is the string of pinyinBaseUnit.getPinyin() of a subset.
				int startIndex = 0;
				chineseKeyWord.append(baseData.substring(startIndex+pyUnit.getStartPosition(), startIndex+pyUnit.getStartPosition() + search.length()));
				searchBuffer.delete(0, searchBuffer.length());
				return true;
			} else if (search.startsWith(pinyinBaseUnit.getPinyin())) { // match all non-pure pinyin
				// The string of pinyinBaseUnit.getPinyin() is the string of "search" of a subset.
				int startIndex = 0;
				searchBuffer.delete(0, pinyinBaseUnit.getPinyin().length());
				chineseKeyWord.append(baseData.substring(startIndex+pyUnit.getStartPosition(),startIndex+pyUnit.getStartPosition()+pinyinBaseUnit.getPinyin().length()));
				boolean found = findPinyinUnits(pinyinUnits,pinyinUnitIndex+1, 0, baseData, searchBuffer,chineseKeyWord);
				if (true == found) {
					return true;
				} else {
					searchBuffer.insert(0, pinyinBaseUnit.getPinyin());
					chineseKeyWord.delete(chineseKeyWord.length()-pinyinBaseUnit.getPinyin().length(),chineseKeyWord.length());
				}
			} else if ((chineseKeyWord.length() <= 0)) {
				if (pinyinBaseUnit.getPinyin().contains(search)) {
					int index = pinyinBaseUnit.getPinyin().indexOf(search);
					chineseKeyWord.append(baseData.substring(index+pyUnit.getStartPosition(),index+pyUnit.getStartPosition()+search.length()));
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
							chineseKeyWord.append(baseData.substring(i+pyUnit.getStartPosition(),i+pyUnit.getStartPosition()+subStr.length()));
							boolean found = findPinyinUnits(pinyinUnits,pinyinUnitIndex+1, 0, baseData,searchBuffer, chineseKeyWord);
							if (true == found) {
								return true;
							} else {
								searchBuffer.insert(0, pinyinBaseUnit.getPinyin().substring(i));
								chineseKeyWord.delete(chineseKeyWord.length()-subStr.length(),chineseKeyWord.length());
							}

						}
					}
					// end [Non-Chinese characters]+[Chinese characters]

					// in fact,if pyUnit.isPinyin()==false, pyUnit.getQwertyPinyinUnitIndex().size()==1. The function of findPinyinUnits() will return false.
					boolean found = findPinyinUnits(pinyinUnits,pinyinUnitIndex, qwertyPinyinUnitIndex + 1, baseData,searchBuffer, chineseKeyWord);
					if (true == found) {
						return true;
					}
				}
			} else { // mismatch
				// in fact,if pyUnit.isPinyin()==false, pyUnit.getQwertyPinyinUnitIndex().size()==1. The function of findPinyinUnits() will return false.
				boolean found = findPinyinUnits(pinyinUnits, pinyinUnitIndex,qwertyPinyinUnitIndex+1, baseData, searchBuffer,chineseKeyWord);
				if (true == found) {
					return true;
				}
			}
		}
		return false;
	}
}
