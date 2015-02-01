PinyinSearchLibrary
===================
	The library of PinyinSearch,a Java Library Which provide data analysis methods, data matching method and so on for T9 pinyin search and Qwerty pinyin search.

### Depend
	The library of pinyin4j: 
	Pinyin4j is a popular Java library supporting convertion between Chinese characters and most popular Pinyin systems. The output format of pinyin could be customized.
	http://pinyin4j.sourceforge.net/
	
### API
    Data structure:PinyinUnit
	PinyinUnit as a base data structure to save the string that Chinese characters  converted to Pinyin characters.
	
	Function:
	public static void chineseStringToPinyinUnit(String chineseString,List<PinyinUnit> pinyinUnit);
	public static boolean matchPinyinUnits(final List<PinyinUnit> pinyinUnits,final String baseData, String search,StringBuffer chineseKeyWord);
	public static String getSortKey(List<PinyinUnit> pinyinUnit)
	public static String getFirstLetter(List<PinyinUnit> pinyinUnit);
	public static String getFirstCharacter(List<PinyinUnit> pinyinUnit);
	public static boolean isKanji(char chr)

	Function call:
	PinyinUtil.chineseStringToPinyinUnit(...);
	T9MatchPinyinUnits.matchPinyinUnits(...);
	QwertyMatchPinyinUnits.matchPinyinUnits(...);
	PinyinUtil.getSortKey(...);
	PinyinUtil.getFirstCharacter(...);
	PinyinUtil.getFirstLetter();
	PinyinUtil.isKanji(...);
	
### Using
	Import packages when use PinyinSearch Library:
	import com.pinyinsearch.util.*;
	import com.pinyinsearch.model.*;
	
	T9 search demo:
	<img src="https://github.com/handsomezhou/PinyinSearchLibrary/blob/master/PinyinSearchDemo/res/drawable/arrow_down.png" />
	Qwerty search demo:
	<img src="https://d13yacurqjgara.cloudfront.net/users/125056/screenshots/1689922/events-menu_1-1-6.gif" />
	
	Function call methods in detail:
	Reference PinyinSearchDemo Project.
	
### License 
	Copyright [handsomezhou] Pinyin search library,for T9 pinyin search and Qwerty pinyin search.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.