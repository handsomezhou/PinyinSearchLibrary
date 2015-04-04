package com.handsomezhou.pinyinsearchdemo.model;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.text.TextUtils;
import android.util.Log;

import com.pinyinsearch.model.PinyinUnit;

public class Contacts extends BaseContacts{
	private static final String TAG="ContactsContacts";
	public enum SearchByType {
		SearchByNull, SearchByName, SearchByPhoneNumber,
	}
	private String mSortKey; // as the sort key word

	private List<PinyinUnit> mNamePinyinUnits; // save the mName converted to
												// Pinyin characters.

	private SearchByType mSearchByType; // Used to save the type of search
	private StringBuffer mMatchKeywords;// Used to save the type of Match Keywords.(name or phoneNumber)
	private int mMatchStartIndex;		//the match start  position of mMatchKeywords in original string(name or phoneNumber).
	private int mMatchLength;			//the match length of mMatchKeywords in original string(name or phoneNumber).
	private List<Contacts> mMultipleNumbersContacts; //save the contacts information who has multiple numbers. 
	private boolean mSelected;	//weather select contact
	private boolean mHideMultipleContacts;		//whether hide multiple contacts
	private boolean mHideOperationView; 		//whether hide operation view
	private boolean mBelongMultipleContactsPhone; //whether belong multiple contacts phone

	public Contacts(String id ,String name, String phoneNumber) {
		super();
		setId(id);
		setName(name);
		setPhoneNumber(phoneNumber);
		setNamePinyinUnits(new ArrayList<PinyinUnit>());
		setSearchByType(SearchByType.SearchByNull);
		setMatchKeywords(new StringBuffer());
		getMatchKeywords().delete(0, getMatchKeywords().length());
		setMatchStartIndex(-1);
		setMatchLength(0);
		setMultipleNumbersContacts(new ArrayList<Contacts>());
		setSelected(false);
		setHideMultipleContacts(false);
		setHideOperationView(true);
		setBelongMultipleContactsPhone(false);
	}
	
	public Contacts(String id, String name, String phoneNumber, String sortKey) {
		super();
		setId(id);
		setName(name);
		setPhoneNumber(phoneNumber);
		setSortKey(sortKey);
		setNamePinyinUnits(new ArrayList<PinyinUnit>());
		setSearchByType(SearchByType.SearchByNull);
		setMatchKeywords(new StringBuffer());
		getMatchKeywords().delete(0, getMatchKeywords().length());
		setMatchStartIndex(-1);
		setMatchLength(0);
		setMultipleNumbersContacts(new ArrayList<Contacts>());
		setSelected(false);
		setHideMultipleContacts(false);
		setHideOperationView(true);
		setBelongMultipleContactsPhone(false);
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
	
	public static Comparator<Contacts> mSearchComparator = new Comparator<Contacts>() {

		@Override
		public int compare(Contacts lhs, Contacts rhs) {
			int compareMatchStartIndex=(lhs.mMatchStartIndex-rhs.mMatchStartIndex);
			return ((0!=compareMatchStartIndex)?(compareMatchStartIndex):(rhs.mMatchLength-lhs.mMatchLength));
		}
	};



	public List<PinyinUnit> getNamePinyinUnits() {
		return mNamePinyinUnits;
	}

	public void setNamePinyinUnits(List<PinyinUnit> namePinyinUnits) {
		mNamePinyinUnits = namePinyinUnits;
	}
	
	/*public List<String> getPhoneNumberList() {
		return mPhoneNumberList;
	}

	public void setPhoneNumberList(List<String> phoneNumberList) {
		mPhoneNumberList = phoneNumberList;
	}*/
	
	public void addPhoneNumber(String phoneNumber){
		if(TextUtils.isEmpty(phoneNumber)){
			return;
		}
		
		if(getPhoneNumber().equals(phoneNumber)){
			return;
		}
		
		int i=0;
		for (i = 0; i < mMultipleNumbersContacts.size(); i++) {
			if (mMultipleNumbersContacts.get(i).getPhoneNumber().equals(phoneNumber)) {
				break;
			}
		}
		
		if (i >= mMultipleNumbersContacts.size()) {
			Contacts cs=new Contacts(getId(), getName(), phoneNumber);
			cs.setSortKey(mSortKey);
			cs.setNamePinyinUnits(mNamePinyinUnits);// not deep copy
			cs.setHideMultipleContacts(true);
			cs.setBelongMultipleContactsPhone(true);
			mMultipleNumbersContacts.add(cs);
			setBelongMultipleContactsPhone(true);
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

	public void setMatchKeywords(StringBuffer matchKeywords) {
		mMatchKeywords = matchKeywords;
	}

	public void setMatchKeywords(String matchKeywords) {
		mMatchKeywords.delete(0, mMatchKeywords.length());
		mMatchKeywords.append(matchKeywords);
	}

	public void clearMatchKeywords() {
		mMatchKeywords.delete(0, mMatchKeywords.length());
	}
	
	public int getMatchStartIndex() {
		return mMatchStartIndex;
	}

	public void setMatchStartIndex(int matchStartIndex) {
		mMatchStartIndex = matchStartIndex;
	}

	public int getMatchLength() {
		return mMatchLength;
	}

	public void setMatchLength(int matchLength) {
		mMatchLength = matchLength;
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
	
	public boolean isHideMultipleContacts() {
		return mHideMultipleContacts;
	}

	public void setHideMultipleContacts(boolean hideMultipleContacts) {
		mHideMultipleContacts = hideMultipleContacts;
	}
	
	public boolean isHideOperationView() {
		return mHideOperationView;
	}

	public void setHideOperationView(boolean hideOperationView) {
		mHideOperationView = hideOperationView;
	}
	
	public boolean isBelongMultipleContactsPhone() {
		return mBelongMultipleContactsPhone;
	}

	public void setBelongMultipleContactsPhone(boolean belongMultipleContactsPhone) {
		mBelongMultipleContactsPhone = belongMultipleContactsPhone;
	}

	public void showContacts(){
		Log.i(TAG,"mId=["+getId()+"]mSortKey=["+mSortKey+"]"+"mName=["+getName()+"]+"+"mPhoneNumber:"+getPhoneNumber()+"+ phoneNumberCount=["+mMultipleNumbersContacts.size()+1+"]");
		for(Contacts contacts:mMultipleNumbersContacts){
			Log.i(TAG, "phone=["+contacts.getPhoneNumber()+"]");
		}
	}
}
