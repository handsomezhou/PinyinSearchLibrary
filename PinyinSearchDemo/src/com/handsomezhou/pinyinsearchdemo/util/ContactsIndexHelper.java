package com.handsomezhou.pinyinsearchdemo.util;

import java.util.ArrayList;
import java.util.List;

import com.handsomezhou.pinyinsearchdemo.model.Contacts;
import com.handsomezhou.pinyinsearchdemo.model.ContactsIndex;
import com.handsomezhou.pinyinsearchdemo.view.QuickAlphabeticBar;

/**
 * @Description
 * 
 * @author handsomezhou
 * @date 2014-12-15
 */
public class ContactsIndexHelper{
	private static final String TAG="ContactsIndexHelper";
	private static ContactsIndexHelper mInstance = null;
	private List<ContactsIndex> mContactsIndexs;
	
	private ContactsIndexHelper(){
		initContactsIndexHelper();
	}
	
	public static ContactsIndexHelper getInstance() {
		if (null == mInstance) {
			mInstance = new ContactsIndexHelper();
		}
		
		return mInstance;
	}
	
	private void initContactsIndexHelper(){
		mContactsIndexs=new ArrayList<ContactsIndex>();
		
		for(int i=0; i<QuickAlphabeticBar.getSelectCharacters().length; i++){
			ContactsIndex contactsIndex=new ContactsIndex(String.valueOf(QuickAlphabeticBar.getSelectCharacters()[i]));
			mContactsIndexs.add(contactsIndex);
		}
		
		return ;
	}
	
	public void clearContactsIndexHelper(){
		if(null==mContactsIndexs){
			mContactsIndexs=new ArrayList<ContactsIndex>();
			return;
		}
		
		mContactsIndexs.clear();
		mInstance=null;
	}
	
	public List<ContactsIndex> getContactsIndexs() {
		return mContactsIndexs;
	}

	public void praseContacts(List<Contacts> contacts){
		if(null==contacts){
			return;
		}
		
		int contactsCount=contacts.size();
		for(int i=0; i<contactsCount; i++){
			Contacts cs=contacts.get(i);
			for(int j=0; j<mContactsIndexs.size(); j++){
				if(String.valueOf(cs.getSortKey().charAt(0)).equals(mContactsIndexs.get(j).getIndexKey())){
					mContactsIndexs.get(j).getContacts().add(cs);
					break;
				}
				
			}
		}
	}
	/*public void setContactsIndexs(List<ContactsIndex> contactsIndexs) {
		mContactsIndexs = contactsIndexs;
	}*/
	
	public void showContactsInfo2(){
		if(null==mContactsIndexs){
			return;
		}
		
		int contactsIndexsSize=mContactsIndexs.size();
		for(int i=0; i<contactsIndexsSize; i++){
			//Log.i("ContactsContacts", "indexKey=["+mContactsIndexs.get(i).getIndexKey()+"]");
			int contactsCount=mContactsIndexs.get(i).getContacts().size();
			for(int j=0; j<contactsCount; j++){
				mContactsIndexs.get(i).getContacts().get(j).showContacts();
			}
		}
	}
}
