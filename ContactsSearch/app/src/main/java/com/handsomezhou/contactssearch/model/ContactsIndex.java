package com.handsomezhou.contactssearch.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 
 * Contacts classified according to '#','A' ~ 'Z'. 
 * The variable of ContactsIndex will save a certain type of contacts.
 * @author handsomezhou
 * @date 2014-12-15
 */
public class ContactsIndex {
	private static final String INDEX_KEY_DEFAULT_VALUE="#";
	String mIndexKey;//'#','A'~'Z'
	List<Contacts> mContacts;
	
	public ContactsIndex(){
		mIndexKey=INDEX_KEY_DEFAULT_VALUE;
		mContacts = new ArrayList<Contacts>();
	}
	
	public ContactsIndex(String indexKey) {
	
		mIndexKey = indexKey;
		mContacts = new ArrayList<Contacts>();
	}
	
	public String getIndexKey() {
		return mIndexKey;
	}
	
	public void setIndexKey(String indexKey) {
		mIndexKey = indexKey;
	}
	
	public List<Contacts> getContacts() {
		return mContacts;
	}
	
	/*public void setContacts(List<Contacts> contacts) {
		mContacts = contacts;
	}*/
	
	public boolean addContacts(Contacts contacts){
		if(null==mContacts){
			mContacts=new ArrayList<Contacts>();
		}
		
		if(null==contacts){
			return false;
		}
		
		return mContacts.add(contacts);
	}
	
	public void clearContacts(){
		if(null==mContacts){
			return;
		}
		mContacts.clear();
	}
}

