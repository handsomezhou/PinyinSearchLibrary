package com.handsomezhou.pinyinsearchdemo.model;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.util.Log;

import com.pinyinsearch.model.PinyinUnit;

public class Contacts {
	private static final String TAG="ContactsContacts";
	public enum SearchByType {
		SearchByNull, SearchByName, SearchByPhoneNumber,
	}

	private String mName;
	private String mPhoneNumber;

	private String mSortKey; // as the sort key word

	private List<PinyinUnit> mNamePinyinUnits; // save the mName converted to
												// Pinyin characters.

	private SearchByType mSearchByType; // Used to save the type of search
	private StringBuffer mMatchKeywords; // Used to save the type of Match
											// Keywords.(name or phoneNumber)

	public Contacts(String name, String phoneNumber) {
		// super();
		mName = name;
		mPhoneNumber = phoneNumber;
		setNamePinyinUnits(new ArrayList<PinyinUnit>());
		setSearchByType(SearchByType.SearchByNull);
		mMatchKeywords = new StringBuffer();
		mMatchKeywords.delete(0, mMatchKeywords.length());
	}
	
	public Contacts(String name, String phoneNumber, String sortKey) {
		// super();
		mName = name;
		mPhoneNumber = phoneNumber;
		mSortKey = sortKey;
		setNamePinyinUnits(new ArrayList<PinyinUnit>());
		setSearchByType(SearchByType.SearchByNull);
		mMatchKeywords = new StringBuffer();
		mMatchKeywords.delete(0, mMatchKeywords.length());
	}
	
	private static Comparator<Object> mChineseComparator = Collator.getInstance(Locale.CHINA);
	
	public static Comparator<Contacts> mDesComparator = new Comparator<Contacts>() {

		@Override
		public int compare(Contacts lhs, Contacts rhs) {
		
			return mChineseComparator.compare(rhs.mSortKey, lhs.mSortKey);
		}
	};

	public static Comparator<Contacts> mAscComparator = new Comparator<Contacts>() {

		@Override
		public int compare(Contacts lhs, Contacts rhs) {
			return mChineseComparator.compare(lhs.mSortKey, rhs.mSortKey);
		}
	};

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

	public String getSortKey() {
		return mSortKey;
	}

	public void setSortKey(String sortKey) {
		mSortKey = sortKey;
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

	// public void setMatchKeywords(StringBuffer matchKeywords) {
	// mMatchKeywords = matchKeywords;
	// }

	public void setMatchKeywords(String matchKeywords) {
		mMatchKeywords.delete(0, mMatchKeywords.length());
		mMatchKeywords.append(matchKeywords);
	}

	public void clearMatchKeywords() {
		mMatchKeywords.delete(0, mMatchKeywords.length());
	}
	
	public void showContacts(){
		Log.i(TAG, "sortKey["+mSortKey+"]name=["+mName+"] phoneNumber=["+mPhoneNumber+"]");
	}
}
