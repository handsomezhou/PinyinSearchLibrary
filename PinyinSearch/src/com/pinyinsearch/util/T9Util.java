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

import com.pinyinsearch.model.PinyinSearchUnit;
import com.pinyinsearch.model.PinyinUnit;
import com.pinyinsearch.model.PinyinBaseUnit;

public class T9Util {
	//private static final String TAG="T9MatchPinyinUnits";
	/**
	 * T9 keyboard 
	 * 1 		2(ABC)	3(DEF) 
	 * 4(GHI) 	5(JKL) 	6(MNO) 
	 * 7(PQRS) 	8(TUV) 	9(WXYZ) 
	 * *		0 		#
	 */

	public static  char getT9Number(char alphabet) {
		char ch = alphabet;

		switch (alphabet) {
		case 'A':
		case 'a':
		case 'B':
		case 'b':
		case 'C':
		case 'c':
			ch = '2';
			break;

		case 'D':
		case 'd':
		case 'E':
		case 'e':
		case 'F':
		case 'f':
			ch = '3';
			break;

		case 'G':
		case 'g':
		case 'H':
		case 'h':
		case 'I':
		case 'i':
			ch = '4';
			break;

		case 'J':
		case 'j':
		case 'K':
		case 'k':
		case 'L':
		case 'l':
			ch = '5';
			break;

		case 'M':
		case 'm':
		case 'N':
		case 'n':
		case 'O':
		case 'o':
			ch = '6';
			break;

		case 'P':
		case 'p':
		case 'Q':
		case 'q':
		case 'R':
		case 'r':
		case 'S':
		case 's':
			ch = '7';
			break;

		case 'T':
		case 't':
		case 'U':
		case 'u':
		case 'V':
		case 'v':
			ch = '8';
			break;

		case 'W':
		case 'w':
		case 'X':
		case 'x':
		case 'Y':
		case 'y':
		case 'Z':
		case 'z':
			ch = '9';
			break;

		default:
			break;
		}

		return ch;
	}
	
	/**
	 * match PinyinSearchUnit
	 * 
	 * @param pinyinSearchUnit
	 * @param keyword
	 * @return true if match success,false otherwise.
	 */
	
	public static boolean match(PinyinSearchUnit pinyinSearchUnit,String keyword){
		if ((null == pinyinSearchUnit) || (null == keyword)) {
			return false;
		}
		
		if(null==pinyinSearchUnit.getBaseData()||null==pinyinSearchUnit.getMatchKeyword()){
			return false;
		}
		
		pinyinSearchUnit.getMatchKeyword().delete(0, pinyinSearchUnit.getMatchKeyword().length());
		
		int pinyinUnitsLength=0;
		pinyinUnitsLength=pinyinSearchUnit.getPinyinUnits().size();
		StringBuffer searchBuffer=new StringBuffer();
		for(int i=0; i<pinyinUnitsLength; i++){
			//pyUnit=pinyinUnits.get(i);
			//for(int j=0; j<pyUnit.getPinyinBaseUnitIndex().size(); j++){
			
			int j=0;
			pinyinSearchUnit.getMatchKeyword().delete(0, pinyinSearchUnit.getMatchKeyword().length());
			searchBuffer.delete(0, searchBuffer.length());
			searchBuffer.append(keyword);
			boolean found = findPinyinUnits(pinyinSearchUnit.getPinyinUnits(), i, j, pinyinSearchUnit.getBaseData(),
					searchBuffer, pinyinSearchUnit.getMatchKeyword());
			if (true == found) {
				return true;
			}
			// }
		}
					
		return false;
	}
	
	/**
	 * @description match search string with pinyinUnits,if success,save the match keyword.
	 * @param pinyinUnits		pinyinUnits head node index
	 * @param pinyinUnitIndex   pinyinUint Index
	 * @param t9PinyinUnitIndex t9PinyinUnit Index 
	 * @param baseData			base data for search.
	 * @param searchBuffer		search keyword.
	 * @param matchKeyword	save the match keyword.
	 * @return true if find,false otherwise.
	 */
	private static boolean findPinyinUnits(final List<PinyinUnit> pinyinUnits,int pinyinUnitIndex,int t9PinyinUnitIndex,final String baseData, StringBuffer searchBuffer,StringBuffer matchKeyword ){
		if((null==pinyinUnits)||(null==baseData)||(null==searchBuffer)||(null==matchKeyword)){
			return false;
		}
		
		String search=searchBuffer.toString();
		if(search.length()<=0){	//match success
			return true;
		}
		
		if(pinyinUnitIndex>=pinyinUnits.size()){
			return false;
		}
		PinyinUnit pyUnit=pinyinUnits.get(pinyinUnitIndex);
		
		if(t9PinyinUnitIndex>=pyUnit.getPinyinBaseUnitIndex().size()){
			return false;
		}
		
		PinyinBaseUnit pinyinBaseUnit=pyUnit.getPinyinBaseUnitIndex().get(t9PinyinUnitIndex);
		
		
		
		if(pyUnit.isPinyin()){
			
			if(search.startsWith(String.valueOf(pinyinBaseUnit.getNumber().charAt(0)))){// match pinyin first character
				searchBuffer.delete(0,1);//delete the match character
				matchKeyword.append(baseData.charAt(pyUnit.getStartPosition()));
				boolean found=findPinyinUnits(pinyinUnits, pinyinUnitIndex+1, 0, baseData, searchBuffer, matchKeyword);
				if(true==found){
					return true; 
				}else{
					searchBuffer.insert(0, pinyinBaseUnit.getNumber().charAt(0));
					matchKeyword.deleteCharAt(matchKeyword.length()-1);
				}
				
			}
			
			if(pinyinBaseUnit.getNumber().startsWith(search)){
				//The string of "search" is the string of t9PinyinUnit.getNumber() of a subset. means match success.
				matchKeyword.append(baseData.charAt(pyUnit.getStartPosition()));
				searchBuffer.delete(0, searchBuffer.length());	
				return true;
				
			}else if(search.startsWith(pinyinBaseUnit.getNumber())){ //match quanpin  success
				//The string of t9PinyinUnit.getNumber() is the string of "search" of a subset.
				searchBuffer.delete(0, pinyinBaseUnit.getNumber().length());
				matchKeyword.append(baseData.charAt(pyUnit.getStartPosition()));
				boolean found=findPinyinUnits(pinyinUnits, pinyinUnitIndex+1, 0, baseData, searchBuffer, matchKeyword);
				if(true==found){
					return true;
				}else{
					searchBuffer.insert(0, pinyinBaseUnit.getNumber());
					matchKeyword.deleteCharAt(matchKeyword.length()-1);
				}
			}else{ //mismatch
				boolean found=findPinyinUnits(pinyinUnits, pinyinUnitIndex, t9PinyinUnitIndex+1, baseData, searchBuffer, matchKeyword);
				if(found==true){
					return true;
				}
			}
			
		}else{ //non-pure Pinyin
			
			if(pinyinBaseUnit.getNumber().startsWith(search)){
				//The string of "search" is the string of t9PinyinUnit.getNumber() of a subset.
				int startIndex=0; 
				matchKeyword.append(baseData.substring(startIndex+pyUnit.getStartPosition(),startIndex+pyUnit.getStartPosition()+ search.length()));
				searchBuffer.delete(0, searchBuffer.length());
				return true;
			}else if(search.startsWith(pinyinBaseUnit.getNumber())){ //match all non-pure pinyin 
				//The string of t9PinyinUnit.getNumber() is the string of "search" of a subset.
				int startIndex=0; 
				searchBuffer.delete(0, pinyinBaseUnit.getNumber().length());
				matchKeyword.append(baseData.substring(startIndex+pyUnit.getStartPosition(),startIndex+pyUnit.getStartPosition()+ pinyinBaseUnit.getNumber().length()));
				boolean found=findPinyinUnits(pinyinUnits, pinyinUnitIndex+1, 0, baseData, searchBuffer, matchKeyword);
				if(true==found){
					return true;
				}else{
					searchBuffer.insert(0, pinyinBaseUnit.getNumber());
					matchKeyword.delete(matchKeyword.length()-pinyinBaseUnit.getNumber().length(), matchKeyword.length());
				}
			}else if((matchKeyword.length()<=0)){
				if(pinyinBaseUnit.getNumber().contains(search)){
					int index=pinyinBaseUnit.getNumber().indexOf(search);
					matchKeyword.append(baseData.substring(index+pyUnit.getStartPosition(),index+pyUnit.getStartPosition()+ search.length()));
					searchBuffer.delete(0, searchBuffer.length());
					return true;
				}else{
					// match case:[Non-Chinese characters]+[Chinese characters]
					//for example:baseData="Tony测试"; match this case:"onycs"<===>"66927" 
					//start [Non-Chinese characters]+[Chinese characters]
					int numLength=pinyinBaseUnit.getNumber().length();
					for(int i=0; i<numLength; i++){
						String subStr=pinyinBaseUnit.getNumber().substring(i);
						if(search.startsWith(subStr)){
							searchBuffer.delete(0, subStr.length());
							matchKeyword.append(baseData.substring(i+pyUnit.getStartPosition(), i+pyUnit.getStartPosition()+subStr.length()));
							boolean found=findPinyinUnits(pinyinUnits, pinyinUnitIndex+1, 0, baseData, searchBuffer, matchKeyword);
							if(true==found){
								return true;
							}else{
								searchBuffer.insert(0, pinyinBaseUnit.getNumber().substring(i));
								matchKeyword.delete(matchKeyword.length()-subStr.length(), matchKeyword.length());
							}
							
						}
					}
					//end [Non-Chinese characters]+[Chinese characters]
					
					//in fact,if pyUnit.isPinyin()==false, pyUnit.getPinyinBaseUnitIndex().size()==1. The function of findPinyinUnits() will return false.
					boolean found=findPinyinUnits(pinyinUnits, pinyinUnitIndex, t9PinyinUnitIndex+1, baseData, searchBuffer, matchKeyword);
					if(true==found){
						return true;
					}
				}
			}else { //mismatch
				//in fact,if pyUnit.isPinyin()==false, pyUnit.getPinyinBaseUnitIndex().size()==1.  The function of findPinyinUnits() will return false.
				boolean found=findPinyinUnits(pinyinUnits, pinyinUnitIndex, t9PinyinUnitIndex+1, baseData, searchBuffer, matchKeyword);
				if(true==found){
					return true;
				}
			}
		}
		return false;
	}
}
