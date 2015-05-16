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
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.annotation.SuppressLint;
import com.pinyinsearch.model.PinyinUnit;
import com.pinyinsearch.model.PinyinBaseUnit;

@SuppressLint("DefaultLocale")
public class PinyinUtil {
	// init Pinyin Output Format
	private static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

	/**
	 * Convert from Chinese string to a series of PinyinUnit
	 * 
	 * @param chineseString
	 * @param pinyinUnit
	 */
	public static void chineseStringToPinyinUnit(String chineseString,List<PinyinUnit> pinyinUnit) {
		if ((null == chineseString) || (null == pinyinUnit)) {
			return;
		}

		String chineseStr = chineseString.toLowerCase();

		if (null == format) {
			format = new HanyuPinyinOutputFormat();
		}

		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

		int chineseStringLength = chineseStr.length();
		StringBuffer nonPinyinString = new StringBuffer();
		PinyinUnit pyUnit = null;
		String originalString = null;
		String[] pinyinStr = null;
		boolean lastChineseCharacters = true;
		int startPosition = -1;

		for (int i = 0; i < chineseStringLength; i++) {
			char ch = chineseStr.charAt(i);
			try {
				pinyinStr = PinyinHelper.toHanyuPinyinStringArray(ch, format);
			} catch (BadHanyuPinyinOutputFormatCombination e) {

				e.printStackTrace();
			}

			if (null == pinyinStr) {
				if (true == lastChineseCharacters) {
					pyUnit = new PinyinUnit();
					lastChineseCharacters = false;
					startPosition = i;
					nonPinyinString.delete(0, nonPinyinString.length());
				}
				nonPinyinString.append(ch);
			} else {
				if (false == lastChineseCharacters) {
					// add continuous non-kanji characters to PinyinUnit
					originalString = nonPinyinString.toString();
					String[] str = { nonPinyinString.toString() };
					addPinyinUnit(pinyinUnit, pyUnit, false, originalString,str, startPosition);
					nonPinyinString.delete(0, nonPinyinString.length());
					lastChineseCharacters = true;
				}
				// add single Chinese characters Pinyin(include Multiple Pinyin)
				// to PinyinUnit
				pyUnit = new PinyinUnit();
				startPosition = i;
				originalString = String.valueOf(ch);
				addPinyinUnit(pinyinUnit, pyUnit, true, originalString,pinyinStr, startPosition);

			}
		}

		if (false == lastChineseCharacters) {
			// add continuous non-kanji characters to PinyinUnit
			originalString = nonPinyinString.toString();
			String[] str = { nonPinyinString.toString() };
			addPinyinUnit(pinyinUnit, pyUnit, false, originalString, str,startPosition);
			nonPinyinString.delete(0, nonPinyinString.length());
			lastChineseCharacters = true;
		}

	}

	/**
	 * get the first letter from original string
	 * 
	 * @param pinyinUnit
	 * @return return the first letter of original string,otherwise return null.
	 */
	public static String getFirstLetter(List<PinyinUnit> pinyinUnit) {
		do {
			if (null == pinyinUnit || pinyinUnit.size() <= 0) {
				break;
			}

			List<PinyinBaseUnit> pinyinBaseUnit = pinyinUnit.get(0).getPinyinBaseUnitIndex();
			if (null == pinyinBaseUnit || pinyinBaseUnit.size() <= 0) {
				break;
			}

			String pinyin = pinyinBaseUnit.get(0).getPinyin();
			if (null == pinyin || pinyin.length() <= 0) {
				break;
			}

			return String.valueOf(pinyin.charAt(0));

		} while (false);

		return null;
	}

	/**
	 * get the first character from original string
	 * 
	 * @param pinyinUnit
	 * @return return the first character of original string,otherwise return
	 *         null.
	 */
	public static String getFirstCharacter(List<PinyinUnit> pinyinUnit) {
		do {
			if (null == pinyinUnit || pinyinUnit.size() <= 0) {
				break;
			}

			List<PinyinBaseUnit> pinyinBaseUnit = pinyinUnit.get(0).getPinyinBaseUnitIndex();
			if (null == pinyinBaseUnit || pinyinBaseUnit.size() <= 0) {
				break;
			}

			String originalString = pinyinBaseUnit.get(0).getOriginalString();
			if (null == originalString || originalString.length() <= 0) {
				break;
			}

			return String.valueOf(originalString.charAt(0));

		} while (false);

		return null;
	}
	
	/**
	 * get sort key, as sort keyword
	 * @param pinyinUnit
	 * @return return sort key,otherwise return null.
	 */
	public static String getSortKey(List<PinyinUnit> pinyinUnit) {
		StringBuffer sortKeyBuffer=new StringBuffer();
		sortKeyBuffer.delete(0, sortKeyBuffer.length());
		String splitSymbol=" ";
		do {
			if ((null == pinyinUnit) || (pinyinUnit.size() <= 0)) {
				break;
			}
			
			for(PinyinUnit pu:pinyinUnit){
				if(pu.isPinyin()){
					sortKeyBuffer.append(pu.getPinyinBaseUnitIndex().get(0).getPinyin()).append(splitSymbol);
					sortKeyBuffer.append(pu.getPinyinBaseUnitIndex().get(0).getOriginalString()).append(splitSymbol);
				}else{
					sortKeyBuffer.append(pu.getPinyinBaseUnitIndex().get(0).getOriginalString()).append(splitSymbol);
				}
			}
			
			return sortKeyBuffer.toString();
		} while (false);

		return null;
	}

	/**
	 * judge chr is kanji
	 * @param chr
	 * @return Is kanji return true,otherwise return false.
	 */
	public static boolean isKanji(char chr){
		String[] pinyinStr = null;
		try {
			pinyinStr = PinyinHelper.toHanyuPinyinStringArray(chr, format);
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		
		return (null==pinyinStr)?(false):(true);
	}
	
	private static void addPinyinUnit(List<PinyinUnit> pinyinUnit,PinyinUnit pyUnit, boolean pinyin, String originalString,String[] string, int startPosition) {
		if ((null == pinyinUnit) || (null == pyUnit)
				|| (null == originalString) || (null == string)) {
			return;
		}

		initPinyinUnit(pyUnit, pinyin, originalString, string, startPosition);
		pinyinUnit.add(pyUnit);

		return;

	}

	private static void initPinyinUnit(PinyinUnit pinyinUnit, boolean pinyin,String originalString, String[] string, int startPosition) {
		if ((null == pinyinUnit) || (null == originalString)
				|| (null == string)) {
			return;
		}
		int i = 0;
		int j = 0;
		int k = 0;
		int strLength = string.length;
		pinyinUnit.setPinyin(pinyin);
		pinyinUnit.setStartPosition(startPosition);

		PinyinBaseUnit pinyinBaseUnit = null;

		if (false == pinyin || strLength <= 1) {// no more than one pinyin
			for (i = 0; i < strLength; i++) {
				pinyinBaseUnit = new PinyinBaseUnit();
				initPinyinBaseUnit(pinyinBaseUnit, originalString, string[i]);
				pinyinUnit.getPinyinBaseUnitIndex().add(pinyinBaseUnit);
			}
		} else { // more than one pinyin.//we must delete the same pinyin
					// string,because pinyin without tone.

			pinyinBaseUnit = new PinyinBaseUnit();
			initPinyinBaseUnit(pinyinBaseUnit, originalString, string[0]);
			pinyinUnit.getPinyinBaseUnitIndex().add(pinyinBaseUnit);
			for (j = 1; j < strLength; j++) {
				int curStringIndexlength = pinyinUnit.getPinyinBaseUnitIndex().size();
				for (k = 0; k < curStringIndexlength; k++) {
					if (pinyinUnit.getPinyinBaseUnitIndex().get(k).getPinyin().equals(string[j])) {
						break;
					}
				}

				if (k == curStringIndexlength) {
					pinyinBaseUnit = new PinyinBaseUnit();
					initPinyinBaseUnit(pinyinBaseUnit, originalString, string[j]);
					pinyinUnit.getPinyinBaseUnitIndex().add(pinyinBaseUnit);
				}
			}
		}
	}

	private static void initPinyinBaseUnit(PinyinBaseUnit pinyinBaseUnit,String originalString, String pinyin) {
		if ((null == pinyinBaseUnit) || (null == originalString)
				|| (null == pinyin)) {
			return;
		}

		pinyinBaseUnit.setOriginalString(new String(originalString));
		pinyinBaseUnit.setPinyin(new String(pinyin));
		int pinyinLength = pinyin.length();
		StringBuffer numBuffer = new StringBuffer();
		numBuffer.delete(0, numBuffer.length());

		for (int i = 0; i < pinyinLength; i++) {
			char ch = T9Util.getT9Number(pinyin.charAt(i));
			numBuffer.append(ch);
		}

		pinyinBaseUnit.setNumber(new String(numBuffer.toString()));
		numBuffer.delete(0, numBuffer.length());

		return;
	}
}
