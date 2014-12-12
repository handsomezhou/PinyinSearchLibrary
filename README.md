PinyinSearchLibrary
===================

Provide data analysis methods, data matching method and so on for T9 pinyin search and Qwerty pinyin search.

Library:
PinyinSearch,a Java Library Which provide data analysis methods, data matching method  for T9 pinyin search and Qwerty pinyin search algorithm.

Import packages when use PinyinSearch Library:
import com.pinyinsearch.util.*;
import com.pinyinsearch.model.*;

Data structure:PinyinUnit
PinyinUnit as a base data structure to save the string that Chinese characters  converted to Pinyin characters.

Function:
public static void chineseStringToPinyinUnit(String chineseString,List<PinyinUnit> pinyinUnit);
public static boolean matchPinyinUnits(final List<PinyinUnit> pinyinUnits,final String baseData, String search,StringBuffer chineseKeyWord);
public static String getSortKey(List<PinyinUnit> pinyinUnit)
public static String getFirstLetter(List<PinyinUnit> pinyinUnit);
public static String getFirstCharacter(List<PinyinUnit> pinyinUnit);

Function call methods:
T9MatchPinyinUnits.matchPinyinUnits(...);
QwertyMatchPinyinUnits.matchPinyinUnits(...);
PinyinUtil.chineseStringToPinyinUnit(...);
PinyinUtil.getSortKey();
PinyinUtil.getFirstCharacter(...);
PinyinUtil.getFirstLetter();


Function call methods in detail:
Reference PinyinSearchDemo Project.