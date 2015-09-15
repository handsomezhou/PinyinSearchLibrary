PinyinSearchLibrary
===================
	The library of PinyinSearch,a Java Library which provide data parsing methods, 
	data matching method and so on for T9 search and Qwerty search.

Features
---------------
 * Support T9 search
 * Support Qwerty search
 * Support Chinese character search
 * Support Pinyin search
 * Support English search
 * Support polyphone search
 * Support highlight

Depend
---------------
### The library of pinyin4j: 
	Pinyin4j is a popular Java library supporting convertion between Chinese characters 
	and most popular Pinyin systems. The output format of pinyin could be customized.
[http://pinyin4j.sourceforge.net/](http://pinyin4j.sourceforge.net/)

Renderings
---------------
<img src="https://github.com/handsomezhou/PinyinSearchLibrary/blob/master/external_res/image/PinyinSearchDemo.gif"/>

API
---------------
### Data structure:PinyinUnit
	PinyinUnit as a base data structure to save the string that Chinese characters  
	converted to Pinyin characters.
	
### Function:
	public static void chineseStringToPinyinUnit(String chineseString,List<PinyinUnit> pinyinUnit);
	public static boolean matchPinyinUnits(final List<PinyinUnit> pinyinUnits,
	final String baseData, String search,StringBuffer chineseKeyWord);
	public static String getSortKey(List<PinyinUnit> pinyinUnit)
	public static String getFirstLetter(List<PinyinUnit> pinyinUnit);
	public static String getFirstCharacter(List<PinyinUnit> pinyinUnit);
	public static boolean isKanji(char chr)

### Function call:
	PinyinUtil.chineseStringToPinyinUnit(...);
	T9MatchPinyinUnits.matchPinyinUnits(...);
	QwertyMatchPinyinUnits.matchPinyinUnits(...);
	PinyinUtil.getSortKey(...);
	PinyinUtil.getFirstCharacter(...);
	PinyinUtil.getFirstLetter();
	PinyinUtil.isKanji(...);
	
Usage
---------------	
### Function call in detail:
	Import packages when use PinyinSearch Library(Dependent on pinyin4j-x.x.x.jar):
	import com.pinyinsearch.util.*;
	import com.pinyinsearch.model.*;
	
	The first step:  Data parsing  
	    * (PinyinUtil.chineseStringToPinyinUnit(...))
    The second step: Data matching 
	    * (T9MatchPinyinUnits.matchPinyinUnits(...) or QwertyMatchPinyinUnits.matchPinyinUnits(...))
	
	For details, please see project AppSearch and PinyinSearchDemo

### Support Library
	 * [pinyinsearch.jar](https://github.com/handsomezhou/PinyinSearchLibrary/blob/master/external_res/jar/pinyinsearch.jar?raw=true)
	 * [pinyin4j-2.5.0.jar](https://github.com/handsomezhou/PinyinSearchLibrary/blob/master/external_res/jar/pinyin4j-2.5.0.jar?raw=true)
	 
### Demo Apk
[PinyinSearchDemo](https://github.com/handsomezhou/PinyinSearchLibrary/blob/master/external_res/bin/PinyinSearchDemo.apk?raw=true)
[AppSearch](https://github.com/handsomezhou/PinyinSearchLibrary/blob/master/external_res/bin/AppSearch.apk?raw=true)

Algorithm introduction
---------------
1.[Android Pinyin search contacts analysis and implementation](http://blog.csdn.net/zjqyjg/article/details/41360769)

2.[Android T9 search contacts analysis and implementation](http://blog.csdn.net/zjqyjg/article/details/41182911)

3.[Android Qwerty search contacts analysis and implementation](http://blog.csdn.net/zjqyjg/article/details/41318907)

License 
---------------
	Copyright 2014 handsomezhou

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0
		
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
