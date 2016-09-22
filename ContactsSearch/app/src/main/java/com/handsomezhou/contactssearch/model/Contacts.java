package com.handsomezhou.contactssearch.model;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import android.text.TextUtils;
import android.util.Log;

import com.pinyinsearch.model.PinyinSearchUnit;

public class Contacts extends BaseContacts implements Cloneable{
	private static final String TAG="ContactsContacts";
	public enum SearchByType {
		SearchByNull, SearchByName, SearchByPhoneNumber,
	}
	private String mSortKey; // as the sort key word

	private PinyinSearchUnit mNamePinyinSearchUnit;// save the mName converted to Pinyin characters.
	
	private SearchByType mSearchByType; // Used to save the type of search
	private StringBuffer mMatchKeywords;// Used to save the type of Match Keywords.(name or phoneNumber)
	private int mMatchStartIndex;		//the match start  position of mMatchKeywords in original string(name or phoneNumber).
	private int mMatchLength;			//the match length of mMatchKeywords in original string(name or phoneNumber).
	private boolean mSelected;	//whether select contact
	private boolean mFirstMultipleContacts;//whether the first multiple Contacts
	private boolean mHideMultipleContacts;		//whether hide multiple contacts
	private boolean mBelongMultipleContactsPhone; //whether belong multiple contacts phone, the value of the variable will not change once you set.
	
	private boolean mHideOperationView; 		//whether hide operation view
	private Contacts mNextContacts; //point the contacts information who has multiple numbers. 

	public Contacts(String id ,String name, String phoneNumber) {
		super();
		setId(id);
		setName(name);
		setPhoneNumber(phoneNumber);
		setNamePinyinSearchUnit(new PinyinSearchUnit(name));
		setSearchByType(SearchByType.SearchByNull);
		setMatchKeywords(new StringBuffer());
		getMatchKeywords().delete(0, getMatchKeywords().length());
		setMatchStartIndex(-1);
		setMatchLength(0);
		setNextContacts(null);
		setSelected(false);
		setFirstMultipleContacts(true);
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
		setNamePinyinSearchUnit(new PinyinSearchUnit(name));
		setSearchByType(SearchByType.SearchByNull);
		setMatchKeywords(new StringBuffer());
		getMatchKeywords().delete(0, getMatchKeywords().length());
		setMatchStartIndex(-1);
		setMatchLength(0);
		setNextContacts(null);
		setSelected(false);
		setFirstMultipleContacts(true);
		setHideMultipleContacts(false);
		setHideOperationView(true);
		setBelongMultipleContactsPhone(false);
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		Contacts obj=(Contacts) super.clone();
		obj.mNamePinyinSearchUnit=(PinyinSearchUnit) mNamePinyinSearchUnit.clone();
		obj.mSearchByType=mSearchByType;
		obj.mMatchKeywords=new StringBuffer(mMatchKeywords);
		obj.mNextContacts=mNextContacts;
		
		return super.clone();
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
	
	/*public static Comparator<List<Contacts>> mAscComparator = new Comparator<List<Contacts>>() {

		@Override
		public int compare(List<Contacts> lhs, List<Contacts> rhs) {
			if((null==lhs)||(lhs.size()<=0)||(null==rhs)||(rhs.size()<=0)){
				return 0;
			}
			return mChineseComparator.compare(lhs.get(0).mSortKey, rhs.get(0).mSortKey);
		}
	};*/
	
	public static Comparator<Contacts> mSearchComparator = new Comparator<Contacts>() {

		@Override
		public int compare(Contacts lhs, Contacts rhs) {
			int compareMatchStartIndex=(lhs.mMatchStartIndex-rhs.mMatchStartIndex);
			return ((0!=compareMatchStartIndex)?(compareMatchStartIndex):(rhs.mMatchLength-lhs.mMatchLength));
		}
	};



	
	public PinyinSearchUnit getNamePinyinSearchUnit() {
		return mNamePinyinSearchUnit;
	}

	public void setNamePinyinSearchUnit(PinyinSearchUnit namePinyinSearchUnit) {
		mNamePinyinSearchUnit = namePinyinSearchUnit;
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

	public boolean isSelected() {
		return mSelected;
	}

	public void setSelected(boolean selected) {
		mSelected = selected;
	}
	
	public boolean isFirstMultipleContacts() {
		return mFirstMultipleContacts;
	}

	public void setFirstMultipleContacts(boolean firstMultipleContacts) {
		mFirstMultipleContacts = firstMultipleContacts;
	}
	
	public boolean isHideMultipleContacts() {
		return mHideMultipleContacts;
	}

	public void setHideMultipleContacts(boolean hideMultipleContacts) {
		mHideMultipleContacts = hideMultipleContacts;
	}
	
	public boolean isBelongMultipleContactsPhone() {
		return mBelongMultipleContactsPhone;
	}

	public void setBelongMultipleContactsPhone(boolean belongMultipleContactsPhone) {
		mBelongMultipleContactsPhone = belongMultipleContactsPhone;
	}

	public boolean isHideOperationView() {
		return mHideOperationView;
	}

	public void setHideOperationView(boolean hideOperationView) {
		mHideOperationView = hideOperationView;
	}
	
	public Contacts getNextContacts() {
		return mNextContacts;
	}

	public void setNextContacts(Contacts nextContacts) {
		mNextContacts = nextContacts;
	}

	public static Contacts addMultipleContact(Contacts contacts, String phoneNumber){
		do{
			if((TextUtils.isEmpty(phoneNumber))||(null==contacts)){
				break;
			}
			
			Contacts currentContact=null;
			Contacts nextContacts=null;
			for(nextContacts=contacts; null!=nextContacts; nextContacts=nextContacts.getNextContacts()){
				currentContact=nextContacts;
				if(nextContacts.getPhoneNumber().equals(phoneNumber)){
					break;
				}
			}
			Contacts cts=null;
			if(null==nextContacts){
				Contacts cs=currentContact;
				cts=new Contacts(cs.getId(), cs.getName(),phoneNumber);
				cts.setSortKey(cs.getSortKey());
				cts.setNamePinyinSearchUnit(cs.getNamePinyinSearchUnit());// not deep copy
				cts.setFirstMultipleContacts(false);
				cts.setHideMultipleContacts(true);
				cts.setBelongMultipleContactsPhone(true);
				cs.setBelongMultipleContactsPhone(true);
				cs.setNextContacts(cts);
			}
			
			return cts;
		}while(false);
		
		return null;
	}
	
	public static int getMultipleNumbersContactsCount(Contacts contacts){
		int contactsCount=0;
		if(null==contacts){
			return contactsCount;
		}
		Contacts currentContacts=contacts.getNextContacts();
		Contacts nextContacts=null;
		while(null!=currentContacts){
			contactsCount++;
			nextContacts=currentContacts;
			currentContacts=nextContacts.getNextContacts();
		}
		
		return contactsCount;
	}
	
	public static void hideOrUnfoldMultipleContactsView(Contacts contacts){
		if(null==contacts){
			return;
		}
		
		if(null==contacts.getNextContacts()){
			return;
		}
		
		boolean hide=!contacts.getNextContacts().isHideMultipleContacts();
		
		Contacts currentContact=contacts.getNextContacts();
		Contacts nextContact=null;
		while(null!=currentContact){
			currentContact.setHideMultipleContacts(hide);
			nextContact=currentContact;
			currentContact=nextContact.getNextContacts();
		}

		
		if(hide){
			Log.i(TAG, "hideMultipleContactsView");
		}else{
			Log.i(TAG, "UnfoldMultipleContactsView");
		}
		
	}
/*	public void showContacts(){
		Log.i(TAG,"mId=["+getId()+"]mSortKey=["+mSortKey+"]"+"mName=["+getName()+"]+"+"mPhoneNumber:"+getPhoneNumber()+"+ phoneNumberCount=["+mMultipleNumbersContacts.size()+1+"]");
		for(Contacts contacts:mMultipleNumbersContacts){
			Log.i(TAG, "phone=["+contacts.getPhoneNumber()+"]");
		}
	}*/
}
