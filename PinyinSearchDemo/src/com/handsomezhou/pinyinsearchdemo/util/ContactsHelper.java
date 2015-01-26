package com.handsomezhou.pinyinsearchdemo.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.provider.ContactsContract;
import android.util.Log;

import com.handsomezhou.pinyinsearchdemo.main.PinyinSearchApplication;
import com.handsomezhou.pinyinsearchdemo.model.Contacts;
import com.handsomezhou.pinyinsearchdemo.model.Contacts.SearchByType;
import com.handsomezhou.pinyinsearchdemo.view.QuickAlphabeticBar;
import com.pinyinsearch.model.PinyinBaseUnit;
import com.pinyinsearch.model.PinyinUnit;
import com.pinyinsearch.util.PinyinUtil;
import com.pinyinsearch.util.QwertyMatchPinyinUnits;
import com.pinyinsearch.util.T9MatchPinyinUnits;

public class ContactsHelper {
	private static final String TAG = "ContactsHelper";
	private Context mContext;
	private static ContactsHelper mInstance = null;
	private List<Contacts> mBaseContacts = null; // The basic data used for the search
	private List<Contacts> mSearchContacts = null; // The search results from the basic data
	/*
	 * save the first input string which search no result.
	 * mFirstNoSearchResultInput.size<=0, means that the first input string
	 * which search no result not appear. mFirstNoSearchResultInput.size>0,
	 * means that the first input string which search no result has appeared,
	 * it's mFirstNoSearchResultInput.toString(). We can reduce the number of
	 * search basic data by the first input string which search no result.
	 */
	private StringBuffer mFirstNoSearchResultInput = null;
	private AsyncTask<Object, Object, List<Contacts>> mLoadTask = null;
	private OnContactsLoad mOnContactsLoad = null;
	/*
	 * private OnContactsChanged mOnContactsChanged=null; private
	 * ContentObserver mContentObserver;
	 */
	private boolean mContactsChanged = true;
	private HashMap<String, Contacts> mSelectedContactsHashMap=null; //(id+phoneNumber)as key
	
	/* private Handler mContactsHandler=new Handler(); */

	public interface OnContactsLoad {
		void onContactsLoadSuccess();

		void onContactsLoadFailed();
	}

	public interface OnContactsChanged {
		void onContactsChanged();
	}

	private ContactsHelper() {
		initContactsHelper();
		// registerContentObserver();
	}

	public static ContactsHelper getInstance() {
		if (null == mInstance) {
			mInstance = new ContactsHelper();
		}

		return mInstance;
	}

	public void destroy() {
		if (null != mInstance) {
			// unregisterContentObserver();
			mInstance = null;// the system will free other memory.
		}
	}

	public List<Contacts> getBaseContacts() {
		return mBaseContacts;
	}

	// public void setBaseContacts(List<Contacts> baseContacts) {
	// mBaseContacts = baseContacts;
	// }

	public List<Contacts> getSearchContacts() {
		return mSearchContacts;
	}

	public int getSearchContactsIndex(Contacts contacts) {
		int index = -1;
		if (null == contacts) {
			return -1;
		}
		int searchContactsCount = mSearchContacts.size();
		for (int i = 0; i < searchContactsCount; i++) {
			if (contacts.getName().charAt(0) == mSearchContacts.get(i)
					.getName().charAt(0)) {
				index = i;
				break;
			}
		}

		return index;
	}

	// public void setSearchContacts(List<Contacts> searchContacts) {
	// mSearchContacts = searchContacts;
	// }

	public OnContactsLoad getOnContactsLoad() {
		return mOnContactsLoad;
	}

	public void setOnContactsLoad(OnContactsLoad onContactsLoad) {
		mOnContactsLoad = onContactsLoad;
	}

	private boolean isContactsChanged() {
		return mContactsChanged;
	}

	private void setContactsChanged(boolean contactsChanged) {
		mContactsChanged = contactsChanged;
	}
	
	
	public HashMap<String, Contacts> getSelectedContacts() {
		return mSelectedContactsHashMap;
	}

	public void setSelectedContacts(HashMap<String, Contacts> selectedContacts) {
		mSelectedContactsHashMap = selectedContacts;
	}
	
	/**
	 * Provides an function to start load contacts
	 * 
	 * @return start load success return true, otherwise return false
	 */
	public boolean startLoadContacts() {
		if (true == isSearching()) {
			return false;
		}

		if (false == isContactsChanged()) {
			return false;
		}

		initContactsHelper();

		mLoadTask = new AsyncTask<Object, Object, List<Contacts>>() {

			@Override
			protected List<Contacts> doInBackground(Object... params) {
				return loadContacts(mContext);
			}

			@Override
			protected void onPostExecute(List<Contacts> result) {
				parseContacts(result);
				super.onPostExecute(result);
				setContactsChanged(false);
				mLoadTask = null;
			}
		}.execute();

		return true;
	}

	/**
	 * @description search base data according to string parameter
	 * @param search
	 *            (valid characters include:'0'~'9','*','#')
	 * @return void
	 *
	 * 
	 */
	public void parseT9InputSearchContacts(String search) {
		/*List<Contacts> mSearchByNameContacts=new ArrayList<Contacts>();
		List<Contacts> mSearchByPhoneNumberContacts=new ArrayList<Contacts>();*/

		if (null == search) {// add all base data to search
			if (null != mSearchContacts) {
				mSearchContacts.clear();
			} else {
				mSearchContacts = new ArrayList<Contacts>();
			}

			for (Contacts contacts : mBaseContacts) {
				contacts.setSearchByType(SearchByType.SearchByNull);
				contacts.clearMatchKeywords();
				mSearchContacts.add(contacts);
				if(contacts.getMultipleNumbersContacts().size()>0){
					List<Contacts> multipleContacts=contacts.getMultipleNumbersContacts();
					for(Contacts cs:multipleContacts){
						cs.setSearchByType(SearchByType.SearchByNull);
						cs.clearMatchKeywords();
						if(false==cs.isHideMultipleContacts()){
							mSearchContacts.add(cs);
						}
						
					}
					
				}
			}

			//mSearchContacts.addAll(mBaseContacts);
			mFirstNoSearchResultInput.delete(0,
					mFirstNoSearchResultInput.length());
			Log.i(TAG, "null==search,mFirstNoSearchResultInput.length()="
					+ mFirstNoSearchResultInput.length());
			return;
		}

		if (mFirstNoSearchResultInput.length() > 0) {
			if (search.contains(mFirstNoSearchResultInput.toString())) {
				Log.i(TAG,
						"no need  to search,null!=search,mFirstNoSearchResultInput.length()="
								+ mFirstNoSearchResultInput.length() + "["
								+ mFirstNoSearchResultInput.toString() + "]"
								+ ";searchlen=" + search.length() + "["
								+ search + "]");
				return;
			} else {
				Log.i(TAG,
						"delete  mFirstNoSearchResultInput, null!=search,mFirstNoSearchResultInput.length()="
								+ mFirstNoSearchResultInput.length()
								+ "["
								+ mFirstNoSearchResultInput.toString()
								+ "]"
								+ ";searchlen="
								+ search.length()
								+ "["
								+ search + "]");
				mFirstNoSearchResultInput.delete(0,
						mFirstNoSearchResultInput.length());
			}
		}

		if (null != mSearchContacts) {
			mSearchContacts.clear();
		} else {
			mSearchContacts = new ArrayList<Contacts>();
		}

		int contactsCount = mBaseContacts.size();

		/**
		 * search process: 1:Search by name (1)Search by name pinyin
		 * characters(org name->name pinyin characters) ('0'~'9','*','#')
		 * (2)Search by org name ('0'~'9','*','#') 2:Search by phone number
		 * ('0'~'9','*','#')
		 */
		for (int i = 0; i < contactsCount; i++) {

			List<PinyinUnit> pinyinUnits = mBaseContacts.get(i)
					.getNamePinyinUnits();
			StringBuffer chineseKeyWord = new StringBuffer();// In order to get
																// Chinese
																// KeyWords.Of
																// course it's
																// maybe not
																// Chinese
																// characters.
			String name = mBaseContacts.get(i).getName();
			if (true == T9MatchPinyinUnits.matchPinyinUnits(pinyinUnits, name,
					search, chineseKeyWord)) {// search by NamePinyinUnits;
				mBaseContacts.get(i).setSearchByType(SearchByType.SearchByName);
				mBaseContacts.get(i)
						.setMatchKeywords(chineseKeyWord.toString());
				chineseKeyWord.delete(0, chineseKeyWord.length());
				mSearchContacts.add(mBaseContacts.get(i));
				//mSearchByNameContacts.add(mBaseContacts.get(i));
				
				if(mBaseContacts.get(i).getPhoneNumberList().size()>1){
					int phoneNumberCount=mBaseContacts.get(i).getMultipleNumbersContacts().size();
					for(int j=0; j<phoneNumberCount; j++){
						Contacts cs=mBaseContacts.get(i).getMultipleNumbersContacts().get(j);
						cs.setSearchByType(SearchByType.SearchByName);
						cs.setMatchKeywords(mBaseContacts.get(i)
								.getMatchKeywords().toString());
						mSearchContacts.add(cs);
						//mSearchByNameContacts.add(cs);
				
					}
				}
				continue;
			} else {		
				if (mBaseContacts.get(i).getPhoneNumberList().get(0).contains(search)) { // search by phone number
					mBaseContacts.get(i).setSearchByType(
							SearchByType.SearchByPhoneNumber);
					mBaseContacts.get(i).setMatchKeywords(search);
					mSearchContacts.add(mBaseContacts.get(i));
					//mSearchByPhoneNumberContacts.add(mBaseContacts.get(i));
					
				}
				
				if(mBaseContacts.get(i).getPhoneNumberList().size()>1){
					int phoneNumberCount=mBaseContacts.get(i).getMultipleNumbersContacts().size();
					for(int j=0; j<phoneNumberCount; j++){
						Contacts cs=mBaseContacts.get(i).getMultipleNumbersContacts().get(j);
						if(cs.getPhoneNumberList().get(0).contains(search)){
							cs.setSearchByType(SearchByType.SearchByPhoneNumber);
							cs.setMatchKeywords(search);
							mSearchContacts.add(cs);
							//mSearchByPhoneNumberContacts.add(cs);
						}
					}
				}
				continue;

			}
		}
		/*mSearchContacts.clear();
		mSearchContacts.addAll(mSearchByNameContacts);
		mSearchContacts.addAll(mSearchByPhoneNumberContacts);*/
		if (mSearchContacts.size() <= 0) {
			if (mFirstNoSearchResultInput.length() <= 0) {
				mFirstNoSearchResultInput.append(search);
				Log.i(TAG,
						"no search result,null!=search,mFirstNoSearchResultInput.length()="
								+ mFirstNoSearchResultInput.length() + "["
								+ mFirstNoSearchResultInput.toString() + "]"
								+ ";searchlen=" + search.length() + "["
								+ search + "]");
			} else {

			}
		}

	}

	/**
	 * @description search base data according to string parameter
	 * @param search
	 * @return void
	 */
	public void parseQwertyInputSearchContacts(String search) {
		if (null == search) {// add all base data to search
			if (null != mSearchContacts) {
				mSearchContacts.clear();
			} else {
				mSearchContacts = new ArrayList<Contacts>();
			}

			for (Contacts contacts : mBaseContacts) {
				contacts.setSearchByType(SearchByType.SearchByNull);
				contacts.clearMatchKeywords();
				mSearchContacts.add(contacts);
				if(contacts.getMultipleNumbersContacts().size()>0){
					List<Contacts> multipleContacts=contacts.getMultipleNumbersContacts();
					for(Contacts cs:multipleContacts){
						cs.setSearchByType(SearchByType.SearchByNull);
						cs.clearMatchKeywords();
						if(false==cs.isHideMultipleContacts()){
							mSearchContacts.add(cs);
						}
					}
					
				}
			}

			//mSearchContacts.addAll(mBaseContacts);
			mFirstNoSearchResultInput.delete(0,
					mFirstNoSearchResultInput.length());
			Log.i(TAG, "null==search,mFirstNoSearchResultInput.length()="
					+ mFirstNoSearchResultInput.length());
			return;
		}

		if (mFirstNoSearchResultInput.length() > 0) {
			if (search.contains(mFirstNoSearchResultInput.toString())) {
				Log.i(TAG,
						"no need  to search,null!=search,mFirstNoSearchResultInput.length()="
								+ mFirstNoSearchResultInput.length() + "["
								+ mFirstNoSearchResultInput.toString() + "]"
								+ ";searchlen=" + search.length() + "["
								+ search + "]");
				return;
			} else {
				Log.i(TAG,
						"delete  mFirstNoSearchResultInput, null!=search,mFirstNoSearchResultInput.length()="
								+ mFirstNoSearchResultInput.length()
								+ "["
								+ mFirstNoSearchResultInput.toString()
								+ "]"
								+ ";searchlen="
								+ search.length()
								+ "["
								+ search + "]");
				mFirstNoSearchResultInput.delete(0,
						mFirstNoSearchResultInput.length());
			}
		}

		if (null != mSearchContacts) {
			mSearchContacts.clear();
		} else {
			mSearchContacts = new ArrayList<Contacts>();
		}

		int contactsCount = mBaseContacts.size();

		/**
		 * search process: 1:Search by name (1)Search by original name (2)Search
		 * by name pinyin characters(original name->name pinyin characters)
		 * 2:Search by phone number
		 */
		for (int i = 0; i < contactsCount; i++) {

			List<PinyinUnit> pinyinUnits = mBaseContacts.get(i)
					.getNamePinyinUnits();
			StringBuffer chineseKeyWord = new StringBuffer();// In order to get
																// Chinese
																// KeyWords.Of
																// course it's
																// maybe not
																// Chinese
																// characters.
			String name = mBaseContacts.get(i).getName();
			if (true == QwertyMatchPinyinUnits.matchPinyinUnits(pinyinUnits,
					name, search, chineseKeyWord)) {// search by NamePinyinUnits;
				mBaseContacts.get(i).setSearchByType(SearchByType.SearchByName);
				mBaseContacts.get(i)
						.setMatchKeywords(chineseKeyWord.toString());
				chineseKeyWord.delete(0, chineseKeyWord.length());
				mSearchContacts.add(mBaseContacts.get(i));
				
				if(mBaseContacts.get(i).getPhoneNumberList().size()>1){
					int phoneNumberCount=mBaseContacts.get(i).getMultipleNumbersContacts().size();
					for(int j=0; j<phoneNumberCount; j++){
						Contacts cs=mBaseContacts.get(i).getMultipleNumbersContacts().get(j);
						cs.setSearchByType(SearchByType.SearchByName);
						cs.setMatchKeywords(mBaseContacts.get(i)
								.getMatchKeywords().toString());
						mSearchContacts.add(cs);
					
					}
				}
				continue;
			} else {
				if (mBaseContacts.get(i).getPhoneNumberList().get(0).contains(search)) { // search  by phone number
					mBaseContacts.get(i).setSearchByType(
							SearchByType.SearchByPhoneNumber);
					mBaseContacts.get(i).setMatchKeywords(search);
					mSearchContacts.add(mBaseContacts.get(i));
					//continue;
				}

				if(mBaseContacts.get(i).getPhoneNumberList().size()>1){
					int phoneNumberCount=mBaseContacts.get(i).getMultipleNumbersContacts().size();
					for(int j=0; j<phoneNumberCount; j++){
						Contacts cs=mBaseContacts.get(i).getMultipleNumbersContacts().get(j);
						if(cs.getPhoneNumberList().get(0).contains(search)){
							cs.setSearchByType(SearchByType.SearchByPhoneNumber);
							cs.setMatchKeywords(search);
							mSearchContacts.add(cs);
						}
					}
				}
				continue;
			}
		}

		if (mSearchContacts.size() <= 0) {
			if (mFirstNoSearchResultInput.length() <= 0) {
				mFirstNoSearchResultInput.append(search);
				Log.i(TAG,
						"no search result,null!=search,mFirstNoSearchResultInput.length()="
								+ mFirstNoSearchResultInput.length() + "["
								+ mFirstNoSearchResultInput.toString() + "]"
								+ ";searchlen=" + search.length() + "["
								+ search + "]");
			} else {

			}
		}

	}

	public void clearSelectedContacts(){
		if(null==mSelectedContactsHashMap){
			mSelectedContactsHashMap=new HashMap<String, Contacts>();
			return;
		}
		
		mSelectedContactsHashMap.clear();
	}
	
	public boolean addSelectedContacts(Contacts contacts){
		do{
			if(null==contacts){
				break;
			}
			
			if(null==mSelectedContactsHashMap){
				mSelectedContactsHashMap=new HashMap<String, Contacts>();
			}
			
			mSelectedContactsHashMap.put(getSelectedContactsKey(contacts), contacts);
			
			return true;
		}while(false);
		
		return false;
	
	}
	
	public void removeSelectedContacts(Contacts contacts){
		if(null==contacts){
			return;
		}
		
		if(null==mSelectedContactsHashMap){
			return;
		}
		
		mSelectedContactsHashMap.remove(getSelectedContactsKey(contacts));
	}
	
	// just for debug
	public void showContactsInfo() {
		int contactsCount = ContactsHelper.getInstance().getBaseContacts()
				.size();
		for (int i = 0; i < contactsCount; i++) {
			String name = ContactsHelper.getInstance().getBaseContacts().get(i)
					.getName();
			List<PinyinUnit> pinyinUnit = ContactsHelper.getInstance()
					.getBaseContacts().get(i).getNamePinyinUnits();
			Log.i(TAG,
					"++++++++++++++++++++++++++++++:name=[" + name + "]"
							+ "firstCharacter=["
							+ PinyinUtil.getFirstCharacter(pinyinUnit) + "]"
							+ "firstLetter=["
							+ PinyinUtil.getFirstLetter(pinyinUnit) + "]"
							+ "+++++++++++++++++++++++++++++");
			int pinyinUnitCount = pinyinUnit.size();
			for (int j = 0; j < pinyinUnitCount; j++) {
				PinyinUnit pyUnit = pinyinUnit.get(j);
				Log.i(TAG, "j=" + j + ",isPinyin[" + pyUnit.isPinyin()
						+ "],startPosition=[" + pyUnit.getStartPosition() + "]");
				List<PinyinBaseUnit> stringIndex = pyUnit
						.getPinyinBaseUnitIndex();
				int stringIndexLength = stringIndex.size();
				for (int k = 0; k < stringIndexLength; k++) {
					Log.i(TAG, "k=" + k + "["
							+ stringIndex.get(k).getOriginalString() + "]"
							+ "[" + stringIndex.get(k).getPinyin() + "]+["
							+ stringIndex.get(k).getNumber() + "]");
				}
			}
		}
	}

	private void initContactsHelper() {
		mContext = PinyinSearchApplication.getContextObject();
		setContactsChanged(true);
		if (null == mBaseContacts) {
			mBaseContacts = new ArrayList<Contacts>();
		} else {
			mBaseContacts.clear();
		}

		if (null == mSearchContacts) {
			mSearchContacts = new ArrayList<Contacts>();
		} else {
			mSearchContacts.clear();
		}

		if (null == mFirstNoSearchResultInput) {
			mFirstNoSearchResultInput = new StringBuffer();
		} else {
			mFirstNoSearchResultInput.delete(0,
					mFirstNoSearchResultInput.length());
		}
		
		if(null==mSelectedContactsHashMap){
			mSelectedContactsHashMap=new HashMap<String, Contacts>();
		}else{
			mSelectedContactsHashMap.clear();
		}
	}

	/*
	 * private void registerContentObserver(){ if(null==mContentObserver){
	 * mContentObserver=new ContentObserver(mContactsHandler) {
	 * 
	 * @Override public void onChange(boolean selfChange) {
	 * setContactsChanged(true); if(null!=mOnContactsChanged){
	 * Log.i("ActivityTest"
	 * ,"mOnContactsChanged mContactsChanged="+mContactsChanged);
	 * mOnContactsChanged.onContactsChanged(); } super.onChange(selfChange); }
	 * 
	 * }; }
	 * 
	 * if(null!=mContext){
	 * mContext.getContentResolver().registerContentObserver(
	 * ContactsContract.CommonDataKinds.Phone.CONTENT_URI, true,
	 * mContentObserver); } }
	 */
	/*
	 * private void unregisterContentObserver(){ if(null!=mContentObserver){
	 * if(null!=mContext){
	 * mContext.getContentResolver().unregisterContentObserver
	 * (mContentObserver); } } }
	 */

	private boolean isSearching() {
		return (mLoadTask != null && mLoadTask.getStatus() == Status.RUNNING);
	}

	@SuppressLint("DefaultLocale")
	private List<Contacts> loadContacts(Context context) {
		List<Contacts> kanjiStartContacts = new ArrayList<Contacts>();
		HashMap<String, Contacts> kanjiStartContactsHashMap=new HashMap<String, Contacts>();
		
		List<Contacts> nonKanjiStartContacts = new ArrayList<Contacts>();
		HashMap<String, Contacts> nonKanjiStartContactsHashMap=new HashMap<String, Contacts>();
		
		List<Contacts> contacts=new ArrayList<Contacts>();
		//HashMap<String, Contacts> contactsHashMap=new HashMap<String, Contacts>();
		
		Contacts cs = null;
		Cursor cursor = null;
		long startLoadTime=System.currentTimeMillis();
		try {

			cursor = context.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					null, null, "sort_key");
			
			String sortkey = null;
			while (cursor.moveToNext()) {

				String contactsId=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
				String displayName = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				String phoneNumber = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			//	Log.i(TAG, "contactsId=["+contactsId+"]name=["+displayName+"]"+"number=["+phoneNumber+"]");
				
				boolean kanjiStartContactsExist=kanjiStartContactsHashMap.containsKey(contactsId);
				boolean nonKanjiStartContactsExist=nonKanjiStartContactsHashMap.containsKey(contactsId);
				
				if(true==kanjiStartContactsExist){
					cs=kanjiStartContactsHashMap.get(contactsId);
					cs.addPhoneNumber(phoneNumber);
				}else if(true==nonKanjiStartContactsExist){
					cs=nonKanjiStartContactsHashMap.get(contactsId);
					cs.addPhoneNumber(phoneNumber);
				}else{
					cs = new Contacts(contactsId,displayName, phoneNumber);
					PinyinUtil.chineseStringToPinyinUnit(cs.getName(),
							cs.getNamePinyinUnits());
					sortkey = PinyinUtil.getSortKey(cs.getNamePinyinUnits())
							.toUpperCase();
					cs.setSortKey(praseSortKey(sortkey));
					boolean isKanji=PinyinUtil.isKanji(cs.getName().charAt(0));
					if(true==isKanji){
						kanjiStartContactsHashMap.put(contactsId, cs);
					}else{
						nonKanjiStartContactsHashMap.put(contactsId, cs);
					}
					
				}
			}
		} catch (Exception e) {

		} finally {
			if (null != cursor) {
				cursor.close();
				cursor = null;
			}
		}
		
		kanjiStartContacts.addAll(kanjiStartContactsHashMap.values());
		Collections.sort(kanjiStartContacts, Contacts.mAscComparator);
		
		nonKanjiStartContacts.addAll(nonKanjiStartContactsHashMap.values());
		Collections.sort(nonKanjiStartContacts, Contacts.mAscComparator);
		
		//contacts.addAll(nonKanjiStartContacts);
		contacts.addAll(kanjiStartContacts);
		
		//merge nonKanjiStartContacts and kanjiStartContacts
		int lastIndex=0;
		boolean shouldBeAdd=false;
		for(int i=0; i<nonKanjiStartContacts.size(); i++){
			String nonKanfirstLetter=PinyinUtil.getFirstLetter(nonKanjiStartContacts.get(i).getNamePinyinUnits());
			//Log.i(TAG, "nonKanfirstLetter=["+nonKanfirstLetter+"]");
			int j=0;
			for(j=0+lastIndex; j<contacts.size(); j++){
				String firstLetter=PinyinUtil.getFirstLetter(contacts.get(j).getNamePinyinUnits());
				lastIndex++;
				if(firstLetter.charAt(0)>nonKanfirstLetter.charAt(0)){
					shouldBeAdd=true;
					break;
				}else{
					shouldBeAdd=false;
				}
			}
			
			if(true==shouldBeAdd){
				contacts.add(j, nonKanjiStartContacts.get(i));
				//Log.i(TAG, "=================================j=["+j+"]");
				shouldBeAdd=false;
			}
		}
	
		long endLoadTime=System.currentTimeMillis();
		Log.i(TAG, "endLoadTime-startLoadTime=["+(endLoadTime-startLoadTime)+"]");
		/*for(Contacts c:contacts){
			c.showContacts();
		}*/
		return contacts;
	}
	
	@SuppressLint("DefaultLocale")
	private List<Contacts> loadContactsOK(Context context) {
		List<Contacts> contacts=new ArrayList<Contacts>();
		HashMap<String, Contacts> contactsHashMap=new HashMap<String, Contacts>();
		
		Contacts cs = null;
		Cursor cursor = null;
		long startLoadTime=System.currentTimeMillis();
		try {

			cursor = context.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					null, null, "sort_key");
			
			String sortkey = null;
			while (cursor.moveToNext()) {

				String contactsId=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
				String displayName = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				String phoneNumber = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			//	Log.i(TAG, "contactsId=["+contactsId+"]name=["+displayName+"]"+"number=["+phoneNumber+"]");
				
				
				boolean contactsExist=contactsHashMap.containsKey(contactsId);
				if(true==contactsExist){
					cs=contactsHashMap.get(contactsId);
					cs.addPhoneNumber(phoneNumber);
				}else{
					cs = new Contacts(contactsId,displayName, phoneNumber);
					PinyinUtil.chineseStringToPinyinUnit(cs.getName(),
							cs.getNamePinyinUnits());
					sortkey = PinyinUtil.getSortKey(cs.getNamePinyinUnits())
							.toUpperCase();
					cs.setSortKey(praseSortKey(sortkey));
					contactsHashMap.put(contactsId, cs);
				}
			}
		} catch (Exception e) {

		} finally {
			if (null != cursor) {
				cursor.close();
				cursor = null;
			}
		}
		
		contacts.addAll(contactsHashMap.values());
		Collections.sort(contacts, Contacts.mAscComparator);
		long endLoadTime=System.currentTimeMillis();
		Log.i(TAG, "endLoadTime-startLoadTime=["+(endLoadTime-startLoadTime)+"]");
/*
		for(Contacts c:contacts){
			c.showContacts();
		}*/
		return contacts;
	}
	
	private void parseContacts(List<Contacts> contacts) {
		if (null == contacts || contacts.size() < 1) {
			if (null != mOnContactsLoad) {
				mOnContactsLoad.onContactsLoadFailed();
			}
			return;
		}

		for (Contacts contact : contacts) {
			if (!mBaseContacts.contains(contact)) {
				mBaseContacts.add(contact);
			}
		}

		if (null != mOnContactsLoad) {
			mOnContactsLoad.onContactsLoadSuccess();
		}

		return;
	}

	private String praseSortKey(String sortKey) {
		if (null == sortKey || sortKey.length() <= 0) {
			return null;
		}

		if ((sortKey.charAt(0) >= 'a' && sortKey.charAt(0) <= 'z')
				|| (sortKey.charAt(0) >= 'A' && sortKey.charAt(0) <= 'Z')) {
			return sortKey;
		}

		return String.valueOf(QuickAlphabeticBar.DEFAULT_INDEX_CHARACTER)
				+ sortKey;
	}
	
	/**
	 * key=id+phoneNumber
	 * */
	private String getSelectedContactsKey(Contacts contacts){
		if(null==contacts){
			return null;
		}
		
		return contacts.getId()+contacts.getPhoneNumber();
	}	
}
