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

	private String mId;
	private String mName;
//	private String mPhoneNumber;
	private List<String> mPhoneNumberList;

	private String mSortKey; // as the sort key word

	private List<PinyinUnit> mNamePinyinUnits; // save the mName converted to
												// Pinyin characters.

	private SearchByType mSearchByType; // Used to save the type of search
	private StringBuffer mMatchKeywords; // Used to save the type of Match Keywords.(name or phoneNumber)
	
	private List<Contacts> mMultipleNumbersContacts; //save the contacts information who has multiple numbers. 
	private boolean mSelected;	//weather select contact
	private boolean mHide;		//weather hide list item of contact 

	public Contacts(String id ,String name, String phoneNumber) {
		// super();
		mId=id;
		mName = name;
		
//		mPhoneNumber = phoneNumber;
		setPhoneNumberList(new ArrayList<String>());
		getPhoneNumberList().add(phoneNumber);
		
		setNamePinyinUnits(new ArrayList<PinyinUnit>());
		setSearchByType(SearchByType.SearchByNull);
		mMatchKeywords = new StringBuffer();
		mMatchKeywords.delete(0, mMatchKeywords.length());
		
		setMultipleNumbersContacts(new ArrayList<Contacts>());
		setSelected(false);
		setHide(false);
	}
	
	public Contacts(String id, String name, String phoneNumber, String sortKey) {
		// super();
		mId=id;
		mName = name;
		/*mPhoneNumber = phoneNumber;*/
		setPhoneNumberList(new ArrayList<String>());
		getPhoneNumberList().add(phoneNumber);
		
		mSortKey = sortKey;
		setNamePinyinUnits(new ArrayList<PinyinUnit>());
		setSearchByType(SearchByType.SearchByNull);
		mMatchKeywords = new StringBuffer();
		mMatchKeywords.delete(0, mMatchKeywords.length());
		
		setMultipleNumbersContacts(new ArrayList<Contacts>());
		setSelected(false);
		setHide(false);
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

	public String getId(){
		return mId;
	}
	
	public void setId(String id){
		mId=id;
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

	/*public String getPhoneNumber() {
		return mPhoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		mPhoneNumber = phoneNumber;
	}
*/
	
	public String getPhoneNumber(){
		if((null==mPhoneNumberList)||mPhoneNumberList.size()<1){
			return null;
		}
		
		return mPhoneNumberList.get(0);
	}
	
	public List<String> getPhoneNumberList() {
		return mPhoneNumberList;
	}

	public void setPhoneNumberList(List<String> phoneNumberList) {
		mPhoneNumberList = phoneNumberList;
	}
	
	public void addPhoneNumber(String phoneNumber){
		if(null==mPhoneNumberList){
			mPhoneNumberList=new ArrayList<String>();
		}
		
		int i=0;
		for (i = 0; i < mPhoneNumberList.size(); i++) {
			if (mPhoneNumberList.get(i).equals(phoneNumber)) {
				break;
			}
		}
		
		if (i >= mPhoneNumberList.size()) {
			mPhoneNumberList.add(phoneNumber);
			Contacts cs=new Contacts(mId, mName, phoneNumber);
			cs.setSortKey(mSortKey);
			cs.setNamePinyinUnits(mNamePinyinUnits);// not deep copy
			cs.setHide(true);
			
			mMultipleNumbersContacts.add(cs);
		}
		
		return;
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
	
	public List<Contacts> getMultipleNumbersContacts() {
		return mMultipleNumbersContacts;
	}

	public void setMultipleNumbersContacts(List<Contacts> multipleNumbersContacts) {
		mMultipleNumbersContacts = multipleNumbersContacts;
	}
	
	public boolean isSelected() {
		return mSelected;
	}

	public void setSelected(boolean selected) {
		mSelected = selected;
	}
	
	public boolean isHide() {
		return mHide;
	}

	public void setHide(boolean hide) {
		mHide = hide;
	}
	public void showContacts(){
		Log.i(TAG,"mId=["+mId+"]mSortKey=["+mSortKey+"]"+"mName=["+mName+"] phoneNumberCount=["+mPhoneNumberList.size()+"]");
		for(String number:mPhoneNumberList){
			Log.i(TAG, "phone=["+number+"]");
		}
	}
}
