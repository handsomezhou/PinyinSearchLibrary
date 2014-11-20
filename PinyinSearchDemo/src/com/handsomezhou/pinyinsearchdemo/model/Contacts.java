package com.handsomezhou.pinyinsearchdemo.model;

import java.util.ArrayList;
import java.util.List;
import com.pinyinsearch.model.PinyinUnit;

public class Contacts {
	public enum SearchByType{
		SearchByNull,
		SearchByName,
		SearchByPhoneNumber,
	}
	
	private String mName;			
	private String mPhoneNumber;
	private List<PinyinUnit> mNamePinyinUnits;	//save the mName converted to Pinyin characters.
	
	private SearchByType mSearchByType;			//Used to save the type of search
	private StringBuffer mMatchKeywords;		//Used to save the type of Match Keywords.(name or phoneNumber)

	public Contacts(String name, String phoneNumber) {
		//super();
		mName = name;
		mPhoneNumber = phoneNumber;
		setNamePinyinUnits(new ArrayList<PinyinUnit>());
		setSearchByType(SearchByType.SearchByNull);
		mMatchKeywords=new StringBuffer();
		mMatchKeywords.delete(0, mMatchKeywords.length());
	}

	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public List<PinyinUnit> getNamePinyinUnits() {
		return mNamePinyinUnits;
	}

	public void setNamePinyinUnits(List<PinyinUnit> namePinyinUnits) {
		mNamePinyinUnits = namePinyinUnits;
	}
	
	public String getPhoneNumber() {
		return mPhoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		mPhoneNumber = phoneNumber;
	}

	public SearchByType getSearchByType() {
		return mSearchByType;
	}

	public void setSearchByType(SearchByType searchByType) {
		mSearchByType = searchByType;
	}
	
	public StringBuffer getMatchKeywords() {
		return mMatchKeywords;
	}

//	public void setMatchKeywords(StringBuffer matchKeywords) {
//		mMatchKeywords = matchKeywords;
//	}
	
	public void setMatchKeywords(String matchKeywords){
		mMatchKeywords.delete(0, mMatchKeywords.length());
		mMatchKeywords.append(matchKeywords);
	}
	
	public void clearMatchKeywords(){
		mMatchKeywords.delete(0, mMatchKeywords.length());
	}
}
