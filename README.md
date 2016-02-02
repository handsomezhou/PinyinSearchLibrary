PinyinSearchLibrary
===================
	The library of PinyinSearch,a Java Library which provide data parsing methods, 
	data matching method and so on for T9 search and Qwerty search.
	
	PinyinSearch = T9Search + QwertySearch
	
**If you're looking for other versions search project, you can find all of them at the following links:**

Java(Android):
[PinyinSearch](https://github.com/handsomezhou/PinyinSearchLibrary)
[T9Search](https://github.com/handsomezhou/T9SearchLibrary)
[QwertySearch](https://github.com/handsomezhou/QwertySearchLibrary)

c plus plus(Qt):
[PinyinSearch](https://github.com/handsomezhou/pinyinsearch4cpp)


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
<img src="https://github.com/handsomezhou/PinyinSearchLibrary/blob/master/external_res/image/ContactsSearch.gif"/>
<img src="https://github.com/handsomezhou/PinyinSearchLibrary/blob/master/external_res/image/AppSearch.gif"/>

API
---------------
### Data structure:PinyinUnit
	PinyinUnit as a base data structure to save the string that Chinese characters  
	converted to Pinyin characters.
	
### Function:
	public static void parse(PinyinSearchUnit pinyinSearchUnit);
	public static boolean match(PinyinSearchUnit pinyinSearchUnit,String search);
	public static String getSortKey(PinyinSearchUnit pinyinSearchUnit);
	public static String getFirstLetter(PinyinSearchUnit pinyinSearchUnit);
	public static String getFirstCharacter(PinyinSearchUnit pinyinSearchUnit);
	public static boolean isKanji(char chr);

### Function call:
	PinyinUtil.parse(...);
	T9Util.match(...);
	QwertyUtil.match(...);
	PinyinUtil.getSortKey(...);
	PinyinUtil.getFirstLetter(...);
	PinyinUtil.getFirstCharacter(...);
	PinyinUtil.isKanji(...);
	
Usage
---------------	
### Function call in detail:
	Import packages when use PinyinSearch Library(Dependent on pinyin4j-x.x.x.jar):
	import com.pinyinsearch.util.*;
	import com.pinyinsearch.model.*;
	
	The first step:  Data parsing  (ps:Must init baseData of PinyinSearchUnit before parse)
	    * (PinyinUtil.parse(...))
    The second step: Data matching 
	    * (T9Util.match(...) or QwertyUtil.match(...))
	
	For details, please see project ContactsSearch and AppSearch.

### Support Library
[pinyinsearch.jar](https://github.com/handsomezhou/PinyinSearchLibrary/blob/master/external_res/jar/pinyinsearch.jar?raw=true)

[pinyin4j-2.5.0.jar](https://github.com/handsomezhou/PinyinSearchLibrary/blob/master/external_res/jar/pinyin4j-2.5.0.jar?raw=true)
	 
### Demo Apk
[ContactsSearch](https://github.com/handsomezhou/PinyinSearchLibrary/blob/master/external_res/bin/ContactsSearch.apk?raw=true)

[AppSearch](https://github.com/handsomezhou/PinyinSearchLibrary/blob/master/external_res/bin/AppSearch.apk?raw=true)

Algorithm introduction
---------------
1.[Pinyin search contacts analysis and implementation](http://blog.csdn.net/zjqyjg/article/details/41360769)

2.[T9 search contacts analysis and implementation](http://blog.csdn.net/zjqyjg/article/details/41182911)

3.[Qwerty search contacts analysis and implementation](http://blog.csdn.net/zjqyjg/article/details/41318907)

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
