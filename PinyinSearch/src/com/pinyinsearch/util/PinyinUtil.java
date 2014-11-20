package com.pinyinsearch.util;

import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import com.pinyinsearch.model.PinyinUnit;
import com.pinyinsearch.model.PinyinBaseUnit;

public class PinyinUtil {
	// init Pinyin Output Format
	private static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

	/**
	 * Convert from Chinese string to a series of PinyinUnit
	 * 
	 * @param chineseString
	 * @param pinyinUnit
	 */
	public static void chineseStringToPinyinUnit(String chineseString,
			List<PinyinUnit> pinyinUnit) {
		if ((null == chineseString) || (null == pinyinUnit)) {
			return;
		}
		
		if(null==format){
			format = new HanyuPinyinOutputFormat();
		}
		
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

		int chineseStringLength = chineseString.length();
		StringBuffer nonPinyinString = new StringBuffer();
		PinyinUnit pyUnit = null;
		String[] pinyinStr = null;
		boolean lastChineseCharacters = true;
		int startPosition=-1;

		for (int i = 0; i < chineseStringLength; i++) {
			char ch = chineseString.charAt(i);
			try {
				pinyinStr = PinyinHelper.toHanyuPinyinStringArray(ch,format);
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				
				e.printStackTrace();
			}

			if (null == pinyinStr) {
				if (true == lastChineseCharacters) {
					pyUnit = new PinyinUnit();
					lastChineseCharacters = false;
					startPosition=i;
					nonPinyinString.delete(0, nonPinyinString.length());
				}
				nonPinyinString.append(ch);
			} else {
				if (false == lastChineseCharacters) {
					// add continuous non-kanji characters to PinyinUnit
					String[] str = { nonPinyinString.toString() };
					addPinyinUnit(pinyinUnit, pyUnit, false, str,startPosition);
					nonPinyinString.delete(0, nonPinyinString.length());
					lastChineseCharacters = true;
				}
				// add single Chinese characters Pinyin(include Multiple Pinyin)
				// to PinyinUnit
				pyUnit = new PinyinUnit();
				startPosition=i;
				addPinyinUnit(pinyinUnit, pyUnit, true, pinyinStr,startPosition);

			}
		}

		if (false == lastChineseCharacters) {
			// add continuous non-kanji characters to PinyinUnit
			String[] str = { nonPinyinString.toString() };
			addPinyinUnit(pinyinUnit, pyUnit, false, str,startPosition);
			nonPinyinString.delete(0, nonPinyinString.length());
			lastChineseCharacters = true;
		}

	}

	private static void addPinyinUnit(List<PinyinUnit> pinyinUnit,
			PinyinUnit pyUnit, boolean pinyin, String[] string,int startPosition) {
		if ((null == pinyinUnit) || (null == pyUnit) || (null == string)) {
			return;
		}

		initPinyinUnit(pyUnit, pinyin, string,startPosition);
		pinyinUnit.add(pyUnit);

		return;

	}

	private static void initPinyinUnit(PinyinUnit pinyinUnit, boolean pinyin,
			String[] string,int startPosition) {
		if ((null == pinyinUnit) || (null == string)) {
			return;
		}
		int i=0;
		int j=0; 
		int k=0;
		int strLength = string.length;
		pinyinUnit.setPinyin(pinyin);
		pinyinUnit.setStartPosition(startPosition);
		
		PinyinBaseUnit pinyinBaseUnit=null;

		if(false==pinyin||strLength<=1){// no more than one pinyin
			for (i = 0; i < strLength; i++) {
				pinyinBaseUnit=new PinyinBaseUnit();
				initT9PinyinUnit(pinyinBaseUnit,string[i]);
				pinyinUnit.getPinyinBaseUnitIndex().add(pinyinBaseUnit);
			}
		}else{ //more than one pinyin.//we must delete the same pinyin string,because pinyin without tone.
			
			pinyinBaseUnit=new PinyinBaseUnit();
			initT9PinyinUnit(pinyinBaseUnit, string[0]);
			pinyinUnit.getPinyinBaseUnitIndex().add(pinyinBaseUnit);
			for( j=1; j<strLength; j++){
				int curStringIndexlength=pinyinUnit.getPinyinBaseUnitIndex().size();
				for( k=0; k<curStringIndexlength; k++){
					if(pinyinUnit.getPinyinBaseUnitIndex().get(k).getPinyin().equals(string[j])){
						break;
					}
				}
				
				if(k==curStringIndexlength){
					pinyinBaseUnit=new PinyinBaseUnit();
					initT9PinyinUnit(pinyinBaseUnit, string[j]);
					pinyinUnit.getPinyinBaseUnitIndex().add(pinyinBaseUnit);
				}
			}
		}
	}
	
	private static void initT9PinyinUnit(PinyinBaseUnit pinyinBaseUnit,String pinyin){
		if((null==pinyinBaseUnit)||(null==pinyin)){
			return;
		}
		
		pinyinBaseUnit.setPinyin(new String(pinyin));
		int pinyinLength=pinyin.length();
		StringBuffer numBuffer=new StringBuffer();
		numBuffer.delete(0, numBuffer.length());
		
		for(int i=0; i<pinyinLength; i++){
			char ch=T9Util.getT9Number(pinyin.charAt(i));
			numBuffer.append(ch);
		}
		
		pinyinBaseUnit.setNumber(new String(numBuffer.toString()));
		numBuffer.delete(0, numBuffer.length());
		
		return;
	}
}
